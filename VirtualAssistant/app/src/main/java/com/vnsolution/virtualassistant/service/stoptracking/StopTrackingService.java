package com.vnsolution.virtualassistant.service.stoptracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.api.GoogleApi;
import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDetailDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDto;
import com.vnsolution.virtualassistant.common.StopTracking;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.model.StopPoint;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtualassistant.util.NotificationUtils;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Response;

public class StopTrackingService extends Service {

    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent activityTransitionPendingIntent;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private PendingIntent locationUpdatesPendingIntent;

    @Inject
    public PlaceDao placeDao;

    @Inject
    public PlaceHistoryDao placeHistoryDao;

    @Inject
    public GoogleApi googleService;

    public class SaveToDBTask extends AsyncTask<StopPoint, Void, PlaceEntity> {

        @Override
        protected PlaceEntity doInBackground(StopPoint... stopPoints) {
            StopPoint stopPoint = stopPoints[0];

            // Place
            PlaceEntity placeEntity = placeDao.getByLocation(stopPoint.latitude, stopPoint.longitude);
            PlaceEntity detectedPlace = null;
            if (placeEntity == null) {
                Address address = getAddressFromLocation(stopPoint);
                if (address != null) {
                    detectedPlace = convertToPlaceEntityFrom(address);
                }
                GoogleMapPlaceDetailDto.PlaceDetail placeDetail = getPlaceDetailByLocation(stopPoint); // Address address = getAddressFromLocation(stopPoint);
                if (placeDetail != null) {
                    detectedPlace = convertToPlaceEntityFrom(placeDetail);
                }
                if (detectedPlace != null) {
                    placeEntity = placeDao.getByName(detectedPlace.placeName);
                    if (placeEntity == null) {
                        placeEntity = placeDao.getByGGPlaceId(detectedPlace.ggPlaceId);
                    }
                    if (placeEntity == null) {
                        placeEntity = placeDao.getByAddress(detectedPlace.formattedAddress);
                    }
                }
            }
            if (placeEntity == null) {
                placeEntity = (detectedPlace != null) ? detectedPlace : convertToPlaceEntityFrom(stopPoint);
                placeDao.insert(placeEntity);
            } else {
                if (((placeEntity.ggPlaceId == null) || (placeEntity.ggPlaceId == "")
                        || (placeEntity.placeName == null) || (placeEntity.placeName == "")) && detectedPlace != null) {
                    detectedPlace.id = placeEntity.id;
                    placeEntity = detectedPlace;
                    placeDao.update(placeEntity);
                }
            }

            // Place History
            if (placeEntity != null) {
                PlaceHistoryEntity placeHistoryEntity = new PlaceHistoryEntity();
                placeHistoryEntity.id = UUID.randomUUID().toString();
                placeHistoryEntity.placeId = placeEntity.id;
                placeHistoryEntity.enterTime = stopPoint.enter;
                placeHistoryEntity.existTime = stopPoint.exist;
                placeHistoryDao.insert(placeHistoryEntity);
            }

            return placeEntity;
        }

        @Override
        protected void onPostExecute(PlaceEntity placeEntity) {
            String message = "Can't detect a place.";
            if (placeEntity != null) {
                message = "(" + placeEntity.latitude + "," + placeEntity.longitude + ")";
                if ((placeEntity.placeName != null) && (placeEntity.placeName != "")
                        && (placeEntity.formattedAddress != null) && (placeEntity.formattedAddress != "")) {
                    message = placeEntity.placeName + "\n" + placeEntity.formattedAddress;
                }
            }
            NotificationUtils.sendNotification(StopTrackingService.this, "CHECK OUT", message);
        }
    }

    public StopTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activityRecognitionClient = new ActivityRecognitionClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AndroidInjection.inject(this);
        //validate
        if (!PermissionUtils.hasPermissions(this, StopTracking.PERMISSIONS_STOP_TRACKING)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            stopSelf();
            return START_NOT_STICKY;
        }

        //notification channel
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, StopTracking.NOTIFY_CHANNEL_ID)
                .setContentTitle(StopTracking.NOTIFY_FOREGROUND_SERVICE_TITLE)
                .setContentText(StopTracking.NOTIFY_FOREGROUND_SERVICE_TEXT)
                .setSmallIcon(R.drawable.ic_unknown)
                .build();
        startForeground((int) (Math.random() * 999999), notification);

        //do heavy work on a background thread
        if (intent != null) {
            switch (intent.getAction()) {
                case StopTracking.INTENT_ACTION_INITIAL:
                    NotificationUtils.sendNotification(this, "EVENT TRIGGER", "Initial service.");
                    runInit();
                    break;
                case StopTracking.INTENT_ACTION_CHECK_IN:
                    NotificationUtils.sendNotification(this, "EVENT TRIGGER", "Check in.");
                    checkIn(true);
                    break;
                case StopTracking.INTENT_ACTION_CHECK_IN_SCHEDULE:
                    NotificationUtils.sendNotification(this, "EVENT TRIGGER", "Schedule check in.");
                    checkIn(false);
                    break;
//                case Constants.INTENT_ACTION_LOCATION_UPDATED:
//                    AppService.sendNotification(this, "EVENT TRIGGER", "Update location.");
//                    updatePosition(intent);
//                    break;
                case StopTracking.INTENT_ACTION_CHECK_OUT:
                    NotificationUtils.sendNotification(this, "EVENT TRIGGER", "Check out.");
                    checkOut();
                    break;
            }
            //stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private List<String> processChainTasks(List<String> taskQueue) {
        List<String> taskResult = new ArrayList<>();
        processChainTasksRecurring(taskQueue, 0, taskResult);
        return taskResult;
    }

    private void processChainTasksRecurring(List<String> taskQueue, int taskIndex, List<String> taskResult) {
        if (taskQueue == null || taskQueue.size() == 0 || taskIndex > taskQueue.size() - 1) return;
        String taskName = taskQueue.get(taskIndex);

        Task<Void> runningTask = null;
        switch (taskName) {
            case StopTracking.REQUEST_START_ACTIVITY_TRANSACTION_MONITOR:
                runningTask = startActivityTransitionMonitor();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    simulateTransition();
//                }
                break;
            case StopTracking.REQUEST_STOP_ACTIVITY_TRANSACTION_MONITOR:
                runningTask = stopActivityTransitionMonitor();
                break;
            case StopTracking.REQUEST_START_LOCATION_UPDATES:
                runningTask = startLocationUpdates();
                break;
            case StopTracking.REQUEST_STOP_LOCATION_UPDATES:
                runningTask = stopLocationUpdates();
                break;
            case StopTracking.REQUEST_START_GEOFENCE_MONITOR:
                runningTask = startGeofenceMonitor();
                break;
            case StopTracking.REQUEST_STOP_GEOFENCE_MONITOR:
                runningTask = stopGeofenceMonitor();
                break;
            case StopTracking.REQUEST_MARK_AS_VISITED:
                maskAsVisited();
                break;
            case StopTracking.REQUEST_STOP_FOREGROUND_SERVICE:
                stopSelf();
                break;
        }

        if (runningTask != null) {
            runningTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String event = taskName + "_SUCCESS";
//                    AppService.sendNotification(StopTrackingService.this, event);
                    taskResult.add(event);
                    processChainTasksRecurring(taskQueue, taskIndex + 1, taskResult);
                }
            });
            runningTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String event = taskName + "_FAILURE";
//                    AppService.sendNotification(StopTrackingService.this, event);
                    taskResult.add(event);
                    processChainTasksRecurring(taskQueue, taskIndex + 1, taskResult);
                }
            });
//            runningTask.addOnCanceledListener(new OnCanceledListener() {
//                @Override
//                public void onCanceled() {
//                    String event = taskName + "_CANCELED";
//                    AppService.sendNotification(StopTrackingService.this, event);
//                    taskResult.add(event);
//                    processChainTasksRecurring(tasks, taskIndex + 1, taskResult);
//                }
//            });
//            runningTask.addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    String event = taskName + "_COMPLETE";
//                    AppService.sendNotification(StopTrackingService.this, event);
//                    taskResult.add(event);
//                    processChainTasksRecurring(tasks, taskIndex + 1, taskResult);
//                }
//            });
        } else {
            String event = taskName + "_SUCCESS";
//            AppService.sendNotification(StopTrackingService.this, event);
            taskResult.add(event);
            processChainTasksRecurring(taskQueue, taskIndex + 1, taskResult);
        }
    }

    private void runInit() {
        List<String> chainTasks = new ArrayList<>();

        chainTasks.add(StopTracking.REQUEST_START_ACTIVITY_TRANSACTION_MONITOR);
        chainTasks.add(StopTracking.REQUEST_STOP_FOREGROUND_SERVICE);

        processChainTasks(chainTasks);
    }

    private Task<Void> checkIn(boolean isFirstCheckedIn) {
        if (!PermissionUtils.hasPermissions(this, StopTracking.PERMISSIONS_LOCATION)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                    double longitude = Math.round(location.getLongitude() * Math.pow(10, 7)) / Math.pow(10, 7);
                    double latitude = Math.round(location.getLatitude() * Math.pow(10, 7)) / Math.pow(10, 7);

                    StopPoint stopPoint = getStopPointFromCache();
                    if (stopPoint == null) {
                        stopPoint = new StopPoint(longitude, latitude, new Date(), new Date());
                    } else {
                        stopPoint.longitude = longitude;
                        stopPoint.latitude = latitude;
                        stopPoint.exist = new Date();
                    }
                    saveStopPointInCache(stopPoint);
                    notifyPosition(stopPoint);

                    List<String> taskQueue = new ArrayList<>();
                    if (isFirstCheckedIn) {
                        taskQueue.add(StopTracking.REQUEST_START_GEOFENCE_MONITOR);
                        taskQueue.add(StopTracking.REQUEST_STOP_ACTIVITY_TRANSACTION_MONITOR);
                    }
                    taskQueue.add(StopTracking.REQUEST_STOP_FOREGROUND_SERVICE);
                    processChainTasks(taskQueue);
                }
            }
        };
        @SuppressLint("MissingPermission") Task<Void> requestLocationUpdates = fusedLocationClient.requestLocationUpdates(buildLocationUpdatesRequest(), locationCallback, Looper.getMainLooper());
        return requestLocationUpdates;
    }

//    private void updatePosition(Intent intent) {
//        Location location = extractLocationResult(intent);
//        StopPoint stopPoint = getStopPointFromCache();
//        if (location != null && stopPoint != null) {
//            stopPoint.longitude = location.getLongitude();
//            stopPoint.latitude = location.getLatitude();
//            stopPoint.exist = new Date();
//            saveStopPointInCache(stopPoint);
//            notifyPosition(stopPoint);
//        }
//        List<String> chainTasks = new ArrayList<>();
//        chainTasks.add(Constants.STOP_REQUEST_LOCATION_UPDATES);
//        chainTasks.add(Constants.STOP_FOREGROUND_SERVICE);
//        processChainTasks(chainTasks);
//    }

    private void checkOut() {
        List<String> chainTasks = new ArrayList<>();

        chainTasks.add(StopTracking.REQUEST_MARK_AS_VISITED);
        chainTasks.add(StopTracking.REQUEST_STOP_GEOFENCE_MONITOR);
        chainTasks.add(StopTracking.REQUEST_START_ACTIVITY_TRANSACTION_MONITOR);
        chainTasks.add(StopTracking.REQUEST_STOP_FOREGROUND_SERVICE);

        processChainTasks(chainTasks);
    }

    private Task<Void> startActivityTransitionMonitor() {
        if (!PermissionUtils.hasPermissions(this, StopTracking.PERMISSIONS_ACTIVITY_RECOGNITION)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        ActivityTransitionRequest activityTrackingRequest = buildActivityTransitionRequest();
        Task<Void> activityTrackingTask = activityRecognitionClient.requestActivityTransitionUpdates(activityTrackingRequest, getActivityTransitionPendingIntent());
        return activityTrackingTask;
    }

    private Task<Void> stopActivityTransitionMonitor() {
        Task<Void> activityTrackingTask = activityRecognitionClient.removeActivityTransitionUpdates(getActivityTransitionPendingIntent());
        return activityTrackingTask;
    }

    private Task<Void> startLocationUpdates() {
        if (!PermissionUtils.hasPermissions(this, StopTracking.PERMISSIONS_LOCATION)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        LocationRequest locationRequest = buildLocationUpdatesRequest();
        @SuppressLint("MissingPermission") Task<Void> requestLocationUpdatesTask = fusedLocationClient.requestLocationUpdates(locationRequest, getLocationUpdatesPendingIntent());
        return requestLocationUpdatesTask;
    }

    private Task<Void> stopLocationUpdates() {
        Task<Void> removeLocationUpdatesTask = fusedLocationClient.removeLocationUpdates(getLocationUpdatesPendingIntent());
        return removeLocationUpdatesTask;
    }

    private Task<Void> startGeofenceMonitor() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        StopPoint stopPoint = getStopPointFromCache();
        GeofencingRequest geofencingRequest = buildGeofencingRequest(stopPoint);
        Task<Void> geofenceTrackingTask = geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent());
        return geofenceTrackingTask;
    }

    private Task<Void> stopGeofenceMonitor() {
        Task<Void> geofenceTrackingTask = geofencingClient.removeGeofences(getGeofencePendingIntent());
        return geofenceTrackingTask;
    }

//    private Location extractLocationResult(Intent intent) {
//        if (intent == null) return null;
//
//        String provider = intent.getStringExtra("Provider");
//        double longitude = intent.getDoubleExtra("Longitude", -1);
//        double latitude = intent.getDoubleExtra("Latitude", -1);
//        if (provider == null || provider == "" || longitude == -1 || latitude == -1) return null;
//
//        double roundedLongitude = Math.round(longitude * Math.pow(10, 7)) / Math.pow(10, 7);
//        double roundedLatitude = Math.round(latitude * Math.pow(10, 7)) / Math.pow(10, 7);
//
//        Location location = new Location(provider);
//        location.setLongitude(roundedLongitude);
//        location.setLatitude(roundedLatitude);
//        return location;
//    }

    private void maskAsVisited() {
        StopPoint stopPoint = getStopPointFromCache();
        if (stopPoint != null) {
            stopPoint.exist = new Date();
            if (stopPoint.exist.getTime() - stopPoint.enter.getTime() >= StopTracking.CONFIG_STOP_DETECTION_IN_MILLISECONDS) {
                new SaveToDBTask().execute(stopPoint);
            }
        }
        removeStopPointFromCache();
    }

    private void notifyPosition(StopPoint stopPoint) {
        if (stopPoint != null) {
//            GoogleMapPlaceDetailDto.PlaceDetail placeDetail = getPlaceDetailByLocation(stopPoint); //Address address = getAddressFromLocation(stopPoint);
//            if (placeDetail != null) {
//                String address = placeDetail.name + "\n" + placeDetail.formattedAddress;
//                AppService.sendNotification(StopTrackingService.this, "PLACE", address);
//            }
//            GoogleMapNearByDto.NearBy nearBy = searchNearByFromLocation(stopPoint);
//            if (nearBy != null) {
//                String address = nearBy.name + "\n" + nearBy.vicinity;
//                AppService.sendNotification(StopTrackingService.this, "NEARBY", address);
//            }
            Address place = getAddressFromLocation(stopPoint);
            if (place != null) {
                String address = place.getFeatureName() + "\n" + place.getAddressLine(0);
                NotificationUtils.sendNotification(StopTrackingService.this, "ADDRESS", address);
            } else {
                NotificationUtils.sendNotification(StopTrackingService.this, "NOTICE", "Can not get address of stop position.");
            }
//            if (placeDetail == null && nearBy == null && place == null) {
//                AppService.sendNotification(StopTrackingService.this, "NOTICE", "Can not get address of stop position.");
//            }
        } else {
            NotificationUtils.sendNotification(StopTrackingService.this, "NOTICE", "Stop position is not available.");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(StopTracking.NOTIFY_CHANNEL_ID, StopTracking.NOTIFY_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private PendingIntent getActivityTransitionPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (activityTransitionPendingIntent != null) {
            return activityTransitionPendingIntent;
        }
        Intent activityTransitionIntent = new Intent(this, ActivityTransitionBroadcastReceiver.class);
        activityTransitionIntent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION);
        activityTransitionPendingIntent = PendingIntent.getBroadcast(this, 0, activityTransitionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return activityTransitionPendingIntent;
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private PendingIntent getLocationUpdatesPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (locationUpdatesPendingIntent != null) {
            return locationUpdatesPendingIntent;
        }
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.INTENT_ACTION);
        intent.addCategory(StopTracking.INTENT_CATEGORY_ACTIVITY_TRANSITION);
        locationUpdatesPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return locationUpdatesPendingIntent;
    }

    private ActivityTransitionRequest buildActivityTransitionRequest() {
        List<ActivityTransition> transitions = new ArrayList<>();

        List<Integer> activities = new ArrayList<>();
        activities.add(DetectedActivity.STILL);
        activities.add(DetectedActivity.WALKING);
        activities.add(DetectedActivity.ON_FOOT); //Does it exist?
        activities.add(DetectedActivity.RUNNING);
        activities.add(DetectedActivity.ON_BICYCLE);
        activities.add(DetectedActivity.IN_VEHICLE);

        for (int activity : activities) {
            transitions.add(
                    new ActivityTransition.Builder()
                            .setActivityType(activity)
                            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                            .build());

            transitions.add(
                    new ActivityTransition.Builder()
                            .setActivityType(activity)
                            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                            .build());
        }

        return new ActivityTransitionRequest(transitions);
    }

    private GeofencingRequest buildGeofencingRequest(StopPoint stopPoint) {
        ArrayList<Geofence> geofences = new ArrayList<>();

        geofences.add(new Geofence.Builder()
                .setRequestId(stopPoint.id)
                .setCircularRegion(stopPoint.latitude, stopPoint.longitude, StopTracking.CONFIG_GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE) //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(StopTracking.CONFIG_GEOFENCE_DWELLING_IN_MILLISECONDS)
                .build());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofences);

        return builder.build();
    }

    private LocationRequest buildLocationUpdatesRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(StopTracking.CONFIG_GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS);
        locationRequest.setFastestInterval(StopTracking.CONFIG_GEOLOCATION_FAST_INTERVAL_UPDATE_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void saveStopPointInCache(StopPoint stopPoint) {
        SharedPreferences sharedPref = getSharedPreferences("com.vnsolution.VirtualAssistant", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String stopPointString = gson.toJson(stopPoint);
        editor.putString("StopPoint", stopPointString);
        editor.commit();
    }

    private StopPoint getStopPointFromCache() {
        SharedPreferences sharedPref = getSharedPreferences("com.vnsolution.VirtualAssistant", MODE_PRIVATE);
        String stopPointString = sharedPref.getString("StopPoint", "");
        Gson gson = new Gson();
        return !stopPointString.isEmpty()? gson.fromJson(stopPointString, StopPoint.class) : null;
    }

    private void removeStopPointFromCache() {
        SharedPreferences sharedPref = getSharedPreferences("com.vnsolution.VirtualAssistant", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("StopPoint");
        editor.commit();
    }

    private Address getAddressFromLocation(StopPoint stopPoint) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(stopPoint.latitude, stopPoint.longitude,1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                return address;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private PlaceEntity convertToPlaceEntityFrom(Address address) {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.id = UUID.randomUUID().toString();
        placeEntity.placeName = address.getFeatureName();
        placeEntity.formattedAddress = address.getAddressLine(0);
        placeEntity.latitude = address.getLatitude();
        placeEntity.longitude = address.getLongitude();
        placeEntity.createdDate = new Date();
        return placeEntity;
    }

    private GoogleMapNearByDto.NearBy searchNearByFromLocation(StopPoint stopPoint) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GoogleMapNearByDto.NearBy>> nearByListFuture = executor.submit(new Callable<List<GoogleMapNearByDto.NearBy>>() {
            @Override
            public List<GoogleMapNearByDto.NearBy> call() throws Exception {
                String location = stopPoint.latitude + "," + stopPoint.longitude;
                Response<GoogleMapNearByDto> searchNearByLocationResponse = googleService.searchNearByLocation(location, "distance").execute();
                if (searchNearByLocationResponse.isSuccessful()) {
                    GoogleMapNearByDto nearByPlace = searchNearByLocationResponse.body();
                    return nearByPlace.results;
                }
                return new ArrayList<>();
            }
        });
        GoogleMapNearByDto.NearBy nearBy = null;
        try {
            List<GoogleMapNearByDto.NearBy> nearByList = nearByListFuture.get();
            if (nearByList != null && !nearByList.isEmpty()) {
                nearBy = nearByList.get(0);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
//        String location = stopPoint.latitude + "," + stopPoint.longitude;
//        googleService.searchNearByLocation(location, "distance").observeForever(new Observer<ApiResponse<GoogleMapNearByDto>>() {
//            @Override
//            public void onChanged(ApiResponse<GoogleMapNearByDto> googleMapNearByDtoApiResponse) {
//                if (googleMapNearByDtoApiResponse instanceof ApiSuccessResponse) {
//                    GoogleMapNearByDto googleMapNearByDto = ((ApiSuccessResponse<GoogleMapNearByDto>)googleMapNearByDtoApiResponse).getBody();
//                }
//                if (googleMapNearByDtoApiResponse instanceof ApiErrorResponse) {
//
//                }
//                if (googleMapNearByDtoApiResponse instanceof ApiEmptyResponse) {
//
//                }
//            }
//        });
        return nearBy;
    }

    private PlaceEntity convertToPlaceEntityFrom(GoogleMapNearByDto.NearBy nearBy) {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.id = UUID.randomUUID().toString();
        placeEntity.ggPlaceId = nearBy.placeId;
        placeEntity.placeName = nearBy.name;
        placeEntity.formattedAddress = nearBy.vicinity;
        placeEntity.latitude = (nearBy.geometry != null && nearBy.geometry.location != null)? nearBy.geometry.location.lat : 0;
        placeEntity.longitude = (nearBy.geometry != null && nearBy.geometry.location != null)? nearBy.geometry.location.lng : 0;
        placeEntity.plusCodeCompound = (nearBy.plusCode != null)? nearBy.plusCode.compoundCode : "";
        placeEntity.plusCodeGlobal = (nearBy.plusCode != null)? nearBy.plusCode.globalCode : "";
        placeEntity.types = join(",", nearBy.types);
        placeEntity.icon = nearBy.icon;
        placeEntity.reference = nearBy.reference;
        placeEntity.scope = nearBy.scope;
        placeEntity.userRatingsTotal = nearBy.userRatingsTotal;
        placeEntity.vicinity = nearBy.vicinity;
        placeEntity.createdDate = new Date();
        return placeEntity;
    }

    private GoogleMapPlaceDetailDto.PlaceDetail getPlaceDetailByLocation(StopPoint stopPoint) {
        GoogleMapPlaceDto.Place place = getPlaceByLocation(stopPoint);
        if (place != null && place.placeId != null && place.placeId != "") {
            GoogleMapPlaceDetailDto.PlaceDetail placeDetail = getPlaceDetailById(place.placeId);
            return placeDetail;
        }
        return null;
    }

    private PlaceEntity convertToPlaceEntityFrom(GoogleMapPlaceDetailDto.PlaceDetail placeDetail) {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.id = UUID.randomUUID().toString();
        placeEntity.ggPlaceId = placeDetail.placeId;
        placeEntity.placeName = placeDetail.name;
        placeEntity.formattedAddress = placeDetail.formattedAddress;
        placeEntity.latitude = (placeDetail.geometry != null && placeDetail.geometry.location != null)? placeDetail.geometry.location.lat : 0;
        placeEntity.longitude = (placeDetail.geometry != null && placeDetail.geometry.location != null)? placeDetail.geometry.location.lng : 0;
        placeEntity.plusCodeCompound = (placeDetail.plusCode != null)? placeDetail.plusCode.compoundCode : "";
        placeEntity.plusCodeGlobal = (placeDetail.plusCode != null)? placeDetail.plusCode.globalCode : "";
        placeEntity.types = join(",", placeDetail.types);
        placeEntity.icon = placeDetail.icon;
        placeEntity.rating = placeDetail.rating;
        placeEntity.reference = placeDetail.reference;
        placeEntity.scope = placeDetail.scope;
        placeEntity.url = placeDetail.url;
        placeEntity.userRatingsTotal = placeDetail.userRatingsTotal;
        placeEntity.utcOffset = String.valueOf(placeDetail.utcOffset);
        placeEntity.vicinity = placeDetail.vicinity;
        placeEntity.createdDate = new Date();
        return placeEntity;
    }

    private GoogleMapPlaceDto.Place getPlaceByLocation(StopPoint stopPoint) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GoogleMapPlaceDto.Place>> placeFuture = executor.submit(new Callable<List<GoogleMapPlaceDto.Place>>() {
            @Override
            public List<GoogleMapPlaceDto.Place> call() throws Exception {
                String location = stopPoint.latitude + "," + stopPoint.longitude;
                Response<GoogleMapPlaceDto> placeResponse = googleService.getPlaceByLocation(location).execute();
                if (placeResponse.isSuccessful()) {
                    GoogleMapPlaceDto place = placeResponse.body();
                    return place.results;
                }
                return new ArrayList<>();
            }
        });
        GoogleMapPlaceDto.Place place = null;
        try {
            List<GoogleMapPlaceDto.Place> placeList = placeFuture.get();
            if (placeList != null && !placeList.isEmpty()) {
                place = placeList.get(0);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return place;
    }

    private GoogleMapPlaceDetailDto.PlaceDetail getPlaceDetailById(String placeId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<GoogleMapPlaceDetailDto.PlaceDetail> placeDetailFuture = executor.submit(new Callable<GoogleMapPlaceDetailDto.PlaceDetail>() {
            @Override
            public GoogleMapPlaceDetailDto.PlaceDetail call() throws Exception {
                Response<GoogleMapPlaceDetailDto> placeDetailResponse = googleService.getPlaceDetail(placeId).execute();
                if (placeDetailResponse.isSuccessful()) {
                    GoogleMapPlaceDetailDto placeDetail = placeDetailResponse.body();
                    return placeDetail.result;
                }
                return null;
            }
        });
        GoogleMapPlaceDetailDto.PlaceDetail placeDetail = null;
        try {
            placeDetail = placeDetailFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return placeDetail;
    }

    private PlaceEntity convertToPlaceEntityFrom(StopPoint stopPoint) {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.id = UUID.randomUUID().toString();
        placeEntity.latitude = stopPoint.latitude;
        placeEntity.longitude = stopPoint.longitude;
        placeEntity.createdDate = new Date();
        return placeEntity;
    }

    private String join(String delimiter, List<String> types) {
        StringBuffer sb = new StringBuffer();
        if (types != null) {
            for (String type : types) {
                sb.append(type);
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

//    private BroadcastReceiver registerBroadcastReceiver() {
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                handleBroadcast(intent);
//            }
//        };
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Constants.BROADCAST_STOP_DETECTED);
//        filter.addAction(Constants.BROADCAST_GEOFENCE_EXIST_DETECTED);
//        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
//        return broadcastReceiver;
//    }

//    private void handleBroadcast(Intent intent) {
//        if (intent.getAction() == Constants.BROADCAST_STOP_DETECTED) {
//            checkIn();
//            AppService.sendNotification(this, "Check-in");
//        } else if (intent.getAction() == Constants.BROADCAST_GEOFENCE_EXIST_DETECTED) {
//            checkOut();
//            AppService.sendNotification(this, "Check-out");
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void simulateTransition() {
        Intent intent = new Intent(this, ActivityTransitionBroadcastReceiver.class);
        intent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION);
        List<ActivityTransitionEvent> events = new ArrayList<>();
        ActivityTransitionEvent transitionEvent;
        transitionEvent = new ActivityTransitionEvent(DetectedActivity.STILL,
                ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos());
        events.add(transitionEvent);
//        transitionEvent = new ActivityTransitionEvent(DetectedActivity.WALKING,
//                ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos());
//        events.add(transitionEvent);
        ActivityTransitionResult result = new ActivityTransitionResult(events);
        SafeParcelableSerializer.serializeToIntentExtra(result, intent,
                "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT");
        sendBroadcast(intent);
    }
}
