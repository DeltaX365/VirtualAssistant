package com.vnsolution.virtualassistant.service.stoptracking;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.PlaceHistory;

import java.io.IOException;
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

    private PendingIntent activityTransitionPendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;
    private FusedLocationProviderClient fusedLocationClient;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private BroadcastReceiver broadcastReceiver;

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

        activityRecognitionClient = new ActivityRecognitionClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        activityTransitionPendingIntent = getActivityTransitionPendingIntent();
        geofencePendingIntent = getGeofencePendingIntent();
        broadcastReceiver = registerBroadcastReceiver();

        startActivityTransitionMonitor();
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopActivityTransitionMonitor();
//        stopGeofenceMonitor();
//    }

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

    private BroadcastReceiver registerBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcast(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_STOP_DETECTED);
        filter.addAction(Constants.BROADCAST_GEOFENCE_EXIST_DETECTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
        return broadcastReceiver;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void startActivityTransitionMonitor() {
        ActivityTransitionRequest request = buildActivityTransitionRequest();
        Task<Void> task = activityRecognitionClient
                .requestActivityTransitionUpdates(request, activityTransitionPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(StopTrackingService.this,"Successfully requested activity updates", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    simulateTransition();
                }
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

    private void handleBroadcast(Intent intent) {
        if (intent.getAction() == Constants.BROADCAST_STOP_DETECTED) {
            checkIn();
        } else if (intent.getAction() == Constants.BROADCAST_GEOFENCE_EXIST_DETECTED) {
            checkOut();
        }
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

    private void checkIn() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    StopPoint stopPoint = new StopPoint(location, new Date(), new Date());
                    stopPoints.add(stopPoint);
                    stopActivityTransitionMonitor();
                    startGeofenceMonitor(stopPoint);
                }
            }
        });
        //requestLocationUpdates();
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
                    }
                }).start();
                stopGeofenceMonitor();
                startActivityTransitionMonitor();
            }
        }
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.GEOLOCATION_INTERVAL_UPDATE);
        locationRequest.setFastestInterval(Constants.GEOLOCATION_FAST_INTERVAL_UPDATE);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
//                    Location location = locationList.get(locationList.size() - 1);
//                    Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
//                    mLastLocation = location;
//                    if (mCurrLocationMarker != null) {
//                        mCurrLocationMarker.remove();
//                    }
//
//                    //Place current location marker
//                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(latLng);
//                    markerOptions.title("Current Position");
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//
//                    //move map camera
//                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                }
            }
        }, Looper.getMainLooper());
    }

    private StopPoint getLatestStopPoint() {
        return (!stopPoints.isEmpty() ? stopPoints.get(stopPoints.size() - 1) : null);
    }

    private void saveToDb(StopPoint stopPoint) {
        if (stopPoint != null && stopPoint.location != null) {
            List<Address> addresses = getAddressFromLocation(stopPoint.location);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                // Save place
                Place place = new Place();
                place.id = UUID.randomUUID().toString();
                place.placeName = address.getFeatureName();
                place.formattedAddress = address.getAddressLine(0);
                place.latitude = address.getLatitude();
                place.longitude = address.getLongitude();
                place.createdDate = new Date();
                placeDao.insert(place);

                // Save place history
                PlaceHistory placeHistory = new PlaceHistory();
                placeHistory.id = UUID.randomUUID().toString();
                placeHistory.placeId = place.id;
                placeHistory.enterTime = stopPoint.enter;
                placeHistory.existTime = stopPoint.exist;
                placeHistoryDao.insert(placeHistory);
            }
        }
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

//    /**
//     * Posts a notification in the notification bar when a transition is detected.
//     * If the user clicks the notification, control goes to the MainActivity.
//     */
//    private void sendNotification(String notificationDetails) {
//        // Get an instance of the Notification manager
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Android O requires a Notification Channel.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.app_name);
//            // Create the channel for the notification
//            NotificationChannel mChannel =
//                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
//
//            // Set the Notification Channel for the Notification Manager.
//            mNotificationManager.createNotificationChannel(mChannel);
//        }
//
//        // Create an explicit content Intent that starts the main Activity.
//        Intent notificationIntent = new Intent(getApplicationContext(), DashboardActivity.class);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Add the main Activity to the task stack as the parent.
//        stackBuilder.addParentStack(DashboardActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Get a notification builder that's compatible with platform versions >= 4
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//        // Define the notification settings.
//        builder.setSmallIcon(R.drawable.ic_launcher)
//                // In a real app, you may want to use a library like Volley
//                // to decode the Bitmap.
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
//                        R.drawable.ic_launcher))
//                .setColor(Color.RED)
//                .setContentTitle(notificationDetails)
//                .setContentText(getString(R.string.geofence_transition_notification_text))
//                .setContentIntent(notificationPendingIntent);
//
//        // Set the Channel ID for Android O.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder.setChannelId(CHANNEL_ID); // Channel ID
//        }
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Issue the notification
//        mNotificationManager.notify(0, builder.build());
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
