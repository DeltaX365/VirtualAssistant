package com.vnsolution.virtualassistant.service.stoptracking;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.service.AppService;
import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.PlaceHistory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

public class StopTrackingService extends Service {
    public static final String[] REQUEST_PERMISSIONS = {
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    public static final String CHANNEL_ID = "FOREGROUND_SERVICE";
    public static final String INTENT_ACTION_INITIAL = "com.vnsolution.virtualassistant.ACTION_INITIAL";
    public static final String INTENT_ACTION_CHECK_IN = "com.vnsolution.virtualassistant.ACTION_CHECK_IN";
    public static final String INTENT_ACTION_CHECK_OUT = "com.vnsolution.virtualassistant.ACTION_CHECK_OUT";
    public static final int MAX_STOP_POINTS = 10;

    private ActivityRecognitionClient activityRecognitionClient; //user activities
    private PendingIntent activityTransitionPendingIntent;
    private GeofencingClient geofencingClient; //geofence
    private PendingIntent geofencePendingIntent;
    private FusedLocationProviderClient fusedLocationClient; //geolocation

    // Stop position
    private List<StopPoint> stopPoints = new ArrayList<>();

    @Inject
    public PlaceDao placeDao;

    @Inject
    public PlaceHistoryDao placeHistoryDao;

    private class StopPoint {
        public String id;
        public Location location;
        public Date enter;
        public Date exist;
        public Boolean isSynced;

        public StopPoint(Location location, Date enter, Date exist) {
            this.id = UUID.randomUUID().toString();
            this.location = location;
            this.enter = enter;
            this.exist = exist;
            this.isSynced = false;
        }
    }

    public StopTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // For tracking user activities.
        activityRecognitionClient = new ActivityRecognitionClient(this);
        activityTransitionPendingIntent = getActivityTransitionPendingIntent();

        // For tracking user geofence.
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofencePendingIntent = getGeofencePendingIntent();

        // For getting geolocation.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        stopPoints = getStopPointsFromCache();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Checking activity...")
                .setSmallIcon(R.drawable.ic_unknown)
                .build();
        startForeground((int) (Math.random() * 999999), notification);

        //do heavy work on a background thread
        switch (intent.getAction()) {
            case INTENT_ACTION_INITIAL:
                startActivityTransitionMonitor();
                AppService.sendNotification(this, "Initial");
                break;
            case INTENT_ACTION_CHECK_IN:
                checkIn();
                AppService.sendNotification(this, "Check-in");
                break;
            case INTENT_ACTION_CHECK_OUT:
                checkOut();
                AppService.sendNotification(this, "Check-out");
                break;
        }
        //stopSelf();

        return START_NOT_STICKY;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopActivityTransitionMonitor();
//        stopGeofenceMonitor();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startActivityTransitionMonitor() {
        ActivityTransitionRequest request = buildActivityTransitionRequest();
        Task<Void> task = activityRecognitionClient
                .requestActivityTransitionUpdates(request, activityTransitionPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(StopTrackingService.this,"Successfully requested activity updates", Toast.LENGTH_SHORT).show();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    simulateTransition();
//                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StopTrackingService.this,"Failed to request activity updates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopActivityTransitionMonitor() {
        Task<Void> task = activityRecognitionClient
                .removeActivityTransitionUpdates(activityTransitionPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                activityTransitionPendingIntent.cancel();
                Toast.makeText(StopTrackingService.this,"Successfully cancel activity transition updates", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StopTrackingService.this,"Failed to cancel activity transition updates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startGeofenceMonitor(StopPoint stopPoint) {
        GeofencingRequest request = buildGeofencingRequest(stopPoint);
        geofencingClient.addGeofences(request, geofencePendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(StopTrackingService.this,"Successfully started geofence monitor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopGeofenceMonitor() {
        geofencingClient.removeGeofences(geofencePendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(StopTrackingService.this,"Failed to start geofence monitor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIn() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    StopPoint stopPoint = new StopPoint(location, new Date(), new Date());
                    saveStopPointInCache(stopPoint);

                    stopActivityTransitionMonitor();
                    startGeofenceMonitor(stopPoint);
                    stopSelf();
                }
            }
        });
    }

    private void checkOut() {
        StopPoint stopPoint = getLatestStopPoint();
        if (stopPoint != null) {
            stopPoint.exist = new Date();
            if (!stopPoint.isSynced
                    && (stopPoint.exist.getTime() - stopPoint.enter.getTime() >= Constants.STOP_DETECTION_IN_MILLISECONDS)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveToDb(stopPoint);
                        stopPoint.isSynced = true;

                        Intent intent = new Intent(Constants.BROADCAST_PLACE_ADDED);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        AppService.sendNotification(StopTrackingService.this, "New place is added");
                    }
                }).start();
            }
        }
        stopGeofenceMonitor();
        startActivityTransitionMonitor();
        stopSelf();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
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

    private ActivityTransitionRequest buildActivityTransitionRequest() {
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

//        transitions.add(
//                new ActivityTransition.Builder()
//                        .setActivityType(DetectedActivity.TILTING)
//                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//                        .build());

        return new ActivityTransitionRequest(transitions);
    }

    private GeofencingRequest buildGeofencingRequest(StopPoint stopPoint) {
        ArrayList<Geofence> geofences = new ArrayList<>();

        geofences.add(new Geofence.Builder()
                .setRequestId(stopPoint.id)
                .setCircularRegion(
                        stopPoint.location.getLatitude(),
                        stopPoint.location.getLongitude(),
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_EXIT |
                        Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        //builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);

        return builder.build();
    }

    private StopPoint getLatestStopPoint() {
        return (!stopPoints.isEmpty() ? stopPoints.get(stopPoints.size() - 1) : null);
    }

    private void saveToDb(StopPoint stopPoint) {
        if (stopPoint != null && stopPoint.location != null) {
            List<Address> addresses = getAddressFromLocation(stopPoint.location);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                //Save place
                Place place = new Place();
                place.id = UUID.randomUUID().toString();
                place.placeName = address.getFeatureName();
                place.formattedAddress = address.getAddressLine(0);
                place.latitude = address.getLatitude();
                place.longitude = address.getLongitude();
                place.createdDate = new Date();
                placeDao.insert(place);

                //Save place history
                PlaceHistory placeHistory = new PlaceHistory();
                placeHistory.id = UUID.randomUUID().toString();
                placeHistory.placeId = place.id;
                placeHistory.enterTime = stopPoint.enter;
                placeHistory.existTime = stopPoint.exist;
                placeHistoryDao.insert(placeHistory);

                //Notify
                AppService.sendNotification(this, place.formattedAddress);
            }
        }
    }

    private void saveStopPointInCache(StopPoint stopPoint) {
        SharedPreferences sharedPref = getSharedPreferences("com.vnsolution.VirtualAssistant", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();

        // Only keep 10 recent stop points
        stopPoints.add(stopPoint);
        if (stopPoints.size() > MAX_STOP_POINTS) {
            int startIndex = stopPoints.size() - MAX_STOP_POINTS - 1;
            int stopIndex = stopPoints.size() - 1;
            stopPoints = stopPoints.subList(startIndex, stopIndex);
        }

        editor.putString("StopPoints", gson.toJson(stopPoints));
        editor.commit();
    }

    private List<StopPoint> getStopPointsFromCache() {
        SharedPreferences sharedPref = getSharedPreferences("com.vnsolution.VirtualAssistant", MODE_PRIVATE);
        String stopPointsString = sharedPref.getString("StopPoints", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<StopPoint>>() {}.getType();
        return !stopPointsString.isEmpty()? gson.fromJson(stopPointsString, type) : new ArrayList<>();
    }

    private List<Address> getAddressFromLocation(Location location) {
        List<Address> addresses = new ArrayList<>();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addresses;
        } catch (IOException exception) {
            exception.printStackTrace();
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return addresses;
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
