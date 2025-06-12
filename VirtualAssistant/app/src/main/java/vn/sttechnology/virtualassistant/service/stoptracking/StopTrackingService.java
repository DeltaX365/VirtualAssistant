package vn.sttechnology.virtualassistant.service.stoptracking;

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
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
import com.google.android.gms.location.ActivityRecognition;
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
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import vn.sttechnology.virtualassistant.BuildConfig;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.common.StopTrackingSetting;
import vn.sttechnology.virtualassistant.model.StopPoint;
import vn.sttechnology.virtualassistant.db.entity.PlaceEntity;
import vn.sttechnology.virtualassistant.service.logging.LoggingService;
import vn.sttechnology.virtualassistant.service.place.PlaceBroadcastReceiver;
import vn.sttechnology.virtualassistant.service.place.PlaceManager;
import vn.sttechnology.virtualassistant.ui.travel.TravelEditActivity;
import vn.sttechnology.virtualassistant.util.MappingUtils;
import vn.sttechnology.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class StopTrackingService extends Service {
    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent activityTransitionPendingIntent;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private PendingIntent locationUpdatesPendingIntent;

    @Inject
    public PlaceManager placeManager;

    public class MarkAsVisitedTask extends AsyncTask<Void, Void, PlaceEntity> {
        private boolean newDetectedPlace = false;

        @Override
        protected PlaceEntity doInBackground(Void... params) {
            PlaceEntity visitedPlace = null;
            StopPoint stopPoint = getStopPointFromCache();
            if (stopPoint != null && !stopPoint.isCheckedIn) {
                stopPoint.exist = new Date();
                if (stopPoint.exist.getTime() - stopPoint.enter.getTime() >= StopTrackingSetting.CONFIG_STOP_DETECTION_IN_MILLISECONDS) {
                    PlaceEntity placeEntity = MappingUtils.convertStopPointToPlaceEntity(stopPoint);
                    visitedPlace = placeManager.savePlace(StopTrackingService.this, placeEntity);
                    placeManager.visitPlace(visitedPlace, stopPoint.enter, stopPoint.exist);
                    stopPoint.placeId = visitedPlace.id;
                    stopPoint.isCheckedIn = true;
                    newDetectedPlace = true;
                }
                saveStopPointInCache(stopPoint);
            }
            return visitedPlace;
        }

        @Override
        protected void onPostExecute(PlaceEntity visitedPlace) {
            if (newDetectedPlace && visitedPlace != null) {
                confirmVisitedPlace(visitedPlace);
            }
        }
    }

    public class CheckOutTask extends AsyncTask<Void, Void, PlaceEntity> {
        private boolean newDetectedPlace = false;

        @Override
        protected PlaceEntity doInBackground(Void... params) {
            PlaceEntity visitedPlace = null;
            StopPoint stopPoint = getStopPointFromCache();
            if (stopPoint != null) {
                stopPoint.exist = new Date();
                if (stopPoint.exist.getTime() - stopPoint.enter.getTime() >= StopTrackingSetting.CONFIG_STOP_DETECTION_IN_MILLISECONDS) {
                    PlaceEntity placeEntity = MappingUtils.convertStopPointToPlaceEntity(stopPoint);
                    visitedPlace = placeManager.savePlace(StopTrackingService.this, placeEntity);
                    placeManager.visitPlace(visitedPlace, stopPoint.enter, stopPoint.exist);
                    newDetectedPlace = !stopPoint.isCheckedIn;
                }
                removeStopPointFromCache();
            }
            return visitedPlace;
        }

        @Override
        protected void onPostExecute(PlaceEntity visitedPlace) {
            if (newDetectedPlace && visitedPlace != null) {
                confirmVisitedPlace(visitedPlace);
            }
        }
    }

    public StopTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activityRecognitionClient = ActivityRecognition.getClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AndroidInjection.inject(this);
        LoggingService.getInstance().log("StopTrackingService is starting...");

        //notification channel
        NotificationChannel channel = new NotificationChannel(StopTrackingSetting.NOTIFY_FOREGROUND_SERVICE_CHANNEL_ID, StopTrackingSetting.NOTIFY_FOREGROUND_SERVICE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, StopTrackingSetting.NOTIFY_FOREGROUND_SERVICE_CHANNEL_ID)
                .setContentTitle(StopTrackingSetting.NOTIFY_FOREGROUND_SERVICE_TITLE)
                .setContentText(StopTrackingSetting.NOTIFY_FOREGROUND_SERVICE_TEXT)
                .setSmallIcon(R.drawable.ic_baseline_hearing_24)
                .build();
        startForeground((int) (Math.random() * 999999), notification);

        //validate
        if (!PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_STOP_TRACKING)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            LoggingService.getInstance().log("StopTrackingService is stopped without sticky because of no PERMISSIONS_STOP_TRACKING permission");
            stopSelf();
            return START_NOT_STICKY;
        }

        //do heavy work on a background thread
        try {
            switch (Objects.requireNonNull(intent.getAction())) {
                case StopTrackingSetting.INTENT_ACTION_INITIAL:
//                    NotificationUtils.sendNotification(this, "EVENT TRIGGER", "Initial service.");
                    Toast.makeText(this, "STOP TRACKING INITIALIZED", Toast.LENGTH_LONG).show();
                    StopPoint stopPoint = getStopPointFromCache();
                    if (stopPoint == null) {
                        checkIn(true);
                        processInitialRequest();
                    } else if (!stopPoint.isCheckedIn) {
                        processMarkAsVisitedRequest();
                    } else {
                        processCheckOutRequest();
                        checkIn(true);
                        processInitialRequest();
                    }
                    break;
                case StopTrackingSetting.INTENT_ACTION_CHECK_IN:
//                    NotificationUtils.sendNotification(this, "EVENT TRIGGER", "Check in.");
                    Toast.makeText(this, "STOP TRACKING CHECKED IN", Toast.LENGTH_LONG).show();
                    checkIn(true);
                    break;
                case StopTrackingSetting.INTENT_ACTION_MARK_AS_VISITED:
//                    NotificationUtils.sendNotification(this, "EVENT TRIGGER", "Mark as visited.");
                    Toast.makeText(this, "STOP TRACKING MARKED AS VISITED", Toast.LENGTH_LONG).show();
                    processMarkAsVisitedRequest();
                    break;
                case StopTrackingSetting.INTENT_ACTION_CHECK_OUT:
//                    NotificationUtils.sendNotification(this, "EVENT TRIGGER", "Check out.");
                    Toast.makeText(this, "STOP TRACKING CHECKED OUT", Toast.LENGTH_LONG).show();
                    processCheckOutRequest();
                    break;
            }
            LoggingService.getInstance().log("StopTrackingService is finished with action: " + intent.getAction());
            //stopSelf();
        } catch (Exception e) {
            LoggingService.getInstance().log("StopTrackingService is finished with exception: " + e.getMessage());
            LoggingService.getInstance().recordException(e);
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
        if (taskQueue == null || taskQueue.isEmpty() || taskIndex > taskQueue.size() - 1) return;
        String taskName = taskQueue.get(taskIndex);

        Task<Void> runningTask = null;
        switch (taskName) {
            case StopTrackingSetting.REQUEST_START_ACTIVITY_TRANSACTION_MONITOR:
                runningTask = startActivityTransitionMonitor();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    simulateTransition();
//                }
                break;
            case StopTrackingSetting.REQUEST_STOP_ACTIVITY_TRANSACTION_MONITOR:
                runningTask = stopActivityTransitionMonitor();
                break;
            case StopTrackingSetting.REQUEST_START_LOCATION_UPDATES:
                runningTask = startLocationUpdates();
                break;
            case StopTrackingSetting.REQUEST_STOP_LOCATION_UPDATES:
                runningTask = stopLocationUpdates();
                break;
            case StopTrackingSetting.REQUEST_START_GEOFENCE_CHECK_IN_MONITOR:
                runningTask = startGeofenceMonitor(StopTrackingSetting.ACTION_CHECK_IN_MONITOR);
                break;
            case StopTrackingSetting.REQUEST_START_GEOFENCE_CHECK_OUT_MONITOR:
                runningTask = startGeofenceMonitor(StopTrackingSetting.ACTION_CHECK_OUT_MONITOR);
                break;
            case StopTrackingSetting.REQUEST_STOP_GEOFENCE_MONITOR:
                runningTask = stopGeofenceMonitor();
                break;
            case StopTrackingSetting.REQUEST_MARK_AS_VISITED:
                new MarkAsVisitedTask().execute();
                break;
            case StopTrackingSetting.REQUEST_CHECK_OUT:
                new CheckOutTask().execute();
                break;
            case StopTrackingSetting.REQUEST_STOP_FOREGROUND_SERVICE:
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

    private void processInitialRequest() {
        List<String> chainTasks = new ArrayList<>();

        chainTasks.add(StopTrackingSetting.REQUEST_START_ACTIVITY_TRANSACTION_MONITOR);
        chainTasks.add(StopTrackingSetting.REQUEST_STOP_FOREGROUND_SERVICE);

        processChainTasks(chainTasks);
    }

    private Task<Void> checkIn(boolean isFirstCheckedIn) {
        if (!PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_LOCATION)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
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
                            taskQueue.add(StopTrackingSetting.REQUEST_START_GEOFENCE_CHECK_IN_MONITOR);
                            taskQueue.add(StopTrackingSetting.REQUEST_STOP_ACTIVITY_TRANSACTION_MONITOR);
                        }
                        taskQueue.add(StopTrackingSetting.REQUEST_STOP_FOREGROUND_SERVICE);
                        processChainTasks(taskQueue);
                    }
                }
            };
        }
        fusedLocationClient.removeLocationUpdates(locationCallback);
        @SuppressLint("MissingPermission") Task<Void> requestLocationUpdates = fusedLocationClient.requestLocationUpdates(buildLocationUpdatesRequest(), locationCallback, Looper.getMainLooper());
        return requestLocationUpdates;
    }

    private void processMarkAsVisitedRequest() {
        List<String> taskQueue = new ArrayList<>();

        taskQueue.add(StopTrackingSetting.REQUEST_MARK_AS_VISITED);
        taskQueue.add(StopTrackingSetting.REQUEST_STOP_GEOFENCE_MONITOR);
        taskQueue.add(StopTrackingSetting.REQUEST_START_GEOFENCE_CHECK_OUT_MONITOR);
        taskQueue.add(StopTrackingSetting.REQUEST_STOP_FOREGROUND_SERVICE);

        processChainTasks(taskQueue);
    }

    private void processCheckOutRequest() {
        List<String> taskQueue = new ArrayList<>();

        taskQueue.add(StopTrackingSetting.REQUEST_CHECK_OUT);
        taskQueue.add(StopTrackingSetting.REQUEST_STOP_GEOFENCE_MONITOR);
        taskQueue.add(StopTrackingSetting.REQUEST_START_ACTIVITY_TRANSACTION_MONITOR);
        taskQueue.add(StopTrackingSetting.REQUEST_STOP_FOREGROUND_SERVICE);

        processChainTasks(taskQueue);
    }

    private Task<Void> startActivityTransitionMonitor() {
        if (!PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_ACTIVITY_RECOGNITION)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Task<Void> activityTrackingTask = activityRecognitionClient.removeActivityTransitionUpdates(getActivityTransitionPendingIntent());
        return activityTrackingTask;
    }

    private Task<Void> startLocationUpdates() {
        if (!PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_LOCATION)) {
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

    private Task<Void> startGeofenceMonitor(String... actions) {
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
        GeofencingRequest geofencingRequest = buildGeofencingRequest(stopPoint, actions);
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

    private void confirmVisitedPlace(PlaceEntity visitedPlace) {
        // Create notification channel
        NotificationChannel channel = new NotificationChannel(StopTrackingSetting.NOTIFY_FOREGROUND_SERVICE_CHANNEL_ID, StopTrackingSetting.NOTIFY_FOREGROUND_SERVICE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        // Build notification
        int notifyId = (int) (Math.random() * 999999);

        String placeFormatted = "Unknown Address (" + visitedPlace.latitude + "," + visitedPlace.longitude + ")";
        if ((visitedPlace.placeName != null) && (visitedPlace.placeName != "")
                && (visitedPlace.formattedAddress != null) && (visitedPlace.formattedAddress != "")) {
            placeFormatted = visitedPlace.placeName + "\n" + visitedPlace.formattedAddress;
        }

        Intent travelConfirmYesIntent = new Intent(this, PlaceBroadcastReceiver.class);
        travelConfirmYesIntent.putExtra(StopTrackingSetting.NOTIFY_DETECTED_PLACE_DATA_PLACE_ID, visitedPlace.id);
        travelConfirmYesIntent.putExtra(StopTrackingSetting.NOTIFY_ID, notifyId);
        PendingIntent travelConfirmPendingIntent = PendingIntent.getBroadcast(this,0, travelConfirmYesIntent, PendingIntent.FLAG_MUTABLE);

        Intent travelEditIntent = new Intent(this, TravelEditActivity.class);
        travelEditIntent.putExtra(StopTrackingSetting.NOTIFY_DETECTED_PLACE_DATA_PLACE_ID, visitedPlace.id);
        PendingIntent travelEditPendingIntent = PendingIntent.getActivity(this,0, travelEditIntent, PendingIntent.FLAG_MUTABLE);

        Notification notification = new NotificationCompat.Builder(this, StopTrackingSetting.NOTIFY_FOREGROUND_SERVICE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_hearing_24)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(StopTrackingSetting.NOTIFY_DETECTED_PLACE_TITLE)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(placeFormatted))
                .addAction(R.drawable.ic_baseline_check_24, StopTrackingSetting.NOTIFY_DETECTED_PLACE_ACTION_YES, travelConfirmPendingIntent)
                .addAction(R.drawable.ic_baseline_edit_24, StopTrackingSetting.NOTIFY_DETECTED_PLACE_ACTION_EDIT, travelEditPendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notifyId, notification);
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

//            Address place = placeManager.getAddressByLocation(this, stopPoint.latitude, stopPoint.longitude);
//            if (place != null) {
//                String address = place.getFeatureName() + "\n" + place.getAddressLine(0);
//                NotificationUtils.sendNotification(StopTrackingService.this, "DETECTED PLACE", address, null);
//            } else {
//                NotificationUtils.sendNotification(StopTrackingService.this, "DETECTED PLACE", "Unknown Place", null);
//            }

//            if (placeDetail == null && nearBy == null && place == null) {
//                AppService.sendNotification(StopTrackingService.this, "NOTICE", "Can not get address of stop position.");
//            }
        } else {
//            NotificationUtils.sendNotification(StopTrackingService.this, "NOTICE", "Stop position is not available.", null);
        }
    }

    private PendingIntent getActivityTransitionPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (activityTransitionPendingIntent != null) {
            return activityTransitionPendingIntent;
        }
        Intent activityTransitionIntent = new Intent(this, ActivityTransitionBroadcastReceiver.class);
        activityTransitionIntent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION);
        activityTransitionPendingIntent = PendingIntent.getBroadcast(this, 0, activityTransitionIntent, PendingIntent.FLAG_MUTABLE);
        return activityTransitionPendingIntent;
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        return geofencePendingIntent;
    }

    private PendingIntent getLocationUpdatesPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (locationUpdatesPendingIntent != null) {
            return locationUpdatesPendingIntent;
        }
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.INTENT_ACTION);
        intent.addCategory(StopTrackingSetting.INTENT_CATEGORY_ACTIVITY_TRANSITION);
        locationUpdatesPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
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

    private GeofencingRequest buildGeofencingRequest(StopPoint stopPoint, String... actions) {
        GeofencingRequest.Builder geofencingRequestbuilder = new GeofencingRequest.Builder();

        String action = (actions.length > 0) ? actions[0] : "";
        if (action != null && !action.isEmpty() && stopPoint != null) {
            Geofence.Builder geofenceBuilder = new Geofence.Builder()
                    .setRequestId(stopPoint.id)
                    .setCircularRegion(stopPoint.latitude, stopPoint.longitude, StopTrackingSetting.CONFIG_GEOFENCE_RADIUS_IN_METERS)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE); //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

            if (action.equals(StopTrackingSetting.ACTION_CHECK_IN_MONITOR)) {
                geofenceBuilder
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(StopTrackingSetting.CONFIG_STOP_DETECTION_IN_MILLISECONDS); //CONFIG_GEOFENCE_DWELLING_IN_MILLISECONDS;
                geofencingRequestbuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
            } else if (action.equals(StopTrackingSetting.ACTION_CHECK_OUT_MONITOR)) {
                geofenceBuilder
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT);
                geofencingRequestbuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
            }

            Geofence geofence = geofenceBuilder.build();
            geofencingRequestbuilder.addGeofence(geofence);
        }

        return geofencingRequestbuilder.build();
    }

    private LocationRequest buildLocationUpdatesRequest() {
        return LocationRequest.create()
                .setInterval(StopTrackingSetting.CONFIG_GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS)
                .setFastestInterval(StopTrackingSetting.CONFIG_GEOLOCATION_FAST_INTERVAL_UPDATE_IN_MILLISECONDS)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY);
    }

    private void saveStopPointInCache(StopPoint stopPoint) {
        SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String stopPointString = gson.toJson(stopPoint);
        editor.putString("StopTrackingService.StopPoint", stopPointString);
        editor.apply();
    }

    private StopPoint getStopPointFromCache() {
        SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        String stopPointString = sharedPref.getString("StopTrackingService.StopPoint", "");
        Gson gson = new Gson();
        return !stopPointString.isEmpty()? gson.fromJson(stopPointString, StopPoint.class) : null;
    }

    private void removeStopPointFromCache() {
        SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("StopTrackingService.StopPoint");
        editor.apply();
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
