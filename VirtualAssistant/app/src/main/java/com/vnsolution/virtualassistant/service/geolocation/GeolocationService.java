package com.vnsolution.virtualassistant.service.geolocation;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.PlaceHistory;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class GeolocationService extends Service implements OnSuccessListener<Location> {
    public static final String[] REQUEST_PERMISSIONS = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION };

    private BroadcastReceiver mBroadcastReceiver;
    private FusedLocationProviderClient fusedLocationClient;
    private int activityType;
    private int activityConfidence;

    private boolean isInGeofence;
    private List<StopPoint> stopPoints = new ArrayList<>();

    @Inject
    public PlaceDao placeDao;

    @Inject
    public PlaceHistoryDao placeHistoryDao;

    private class StopPoint {
        public Location location;
        public Date enter;
        public Date exist;
        public Boolean isSynced;

        public StopPoint(Location location, Date enter, Date exist) {
            this.location = location;
            this.enter = enter;
            this.exist = exist;
            this.isSynced = false;
        }
    }

    public GeolocationService() {
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startObservingActivityUpdates();
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

    private void startObservingActivityUpdates() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleActivityUpdated(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_ACTIVITY_DETECTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    private void handleActivityUpdated(Intent intent) {
        activityType = intent.getIntExtra("type", -1);
        activityConfidence = intent.getIntExtra("confidence", 0);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this);
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            Toast.makeText(getApplicationContext(), "Activity: " + activityType + "\nConfidence: " + activityConfidence + "\nlatitude : " + location.getLatitude() + "\nlongitude : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            if (isStoppedOutGeofence(location)) {
                checkIn(location);
            }
            if (isMovingOutGeofence(location)) {
                checkOut();
            }
            if (isStoppedInGeofence(location) || isMovingInGeofence(location)) {
                keepTrack();
            }
        }
    }

    private void checkIn(Location location) {
        isInGeofence = true;
        StopPoint stopPoint = new StopPoint(location, new Date(), new Date());
        stopPoints.add(stopPoint);
    }

    private void checkOut() {
        if (isInGeofence) {
            keepTrack();
        }
        isInGeofence = false;
    }

    private void keepTrack() {
        StopPoint stopPoint = getLatestStopPoint();
        if (stopPoint != null) {
            stopPoint.exist = new Date();
            if (!stopPoint.isSynced) {
                    //&& (stopPoint.exist.getTime() - stopPoint.enter.getTime() >= Constants.STOP_DETECTION_IN_MILLISECONDS)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveToDb(stopPoint);
                        stopPoint.isSynced = true;

                        Intent intent = new Intent(Constants.BROADCAST_PLACE_ADDED);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                }).start();
            }
        }
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

    private boolean isStoppedOutGeofence(Location location) {
        StopPoint stopPoint = getLatestStopPoint();
        return (((activityType == DetectedActivity.STILL || activityType == DetectedActivity.TILTING) && (activityConfidence >= Constants.CONFIDENCE))
                && ((stopPoint == null) || ((stopPoint != null) && (location.distanceTo(stopPoint.location) >= Constants.DISTANCE))));
    }

    private boolean isMovingOutGeofence(Location location) {
        StopPoint stopPoint = getLatestStopPoint();
        return ((!(activityType == DetectedActivity.STILL || activityType == DetectedActivity.TILTING) && (activityConfidence >= Constants.CONFIDENCE))
                && (stopPoint != null)
                && (location.distanceTo(stopPoint.location) >= Constants.DISTANCE));
    }

    private boolean isStoppedInGeofence(Location location) {
        StopPoint stopPoint = getLatestStopPoint();
        return (((activityType == DetectedActivity.STILL || activityType == DetectedActivity.TILTING) && (activityConfidence >= Constants.CONFIDENCE))
                && ((stopPoint != null) && (location.distanceTo(stopPoint.location) < Constants.DISTANCE)));
    }

    private boolean isMovingInGeofence(Location location) {
        StopPoint stopPoint = getLatestStopPoint();
        return ((!(activityType == DetectedActivity.STILL || activityType == DetectedActivity.TILTING) && (activityConfidence >= Constants.CONFIDENCE))
                && (stopPoint != null)
                && (location.distanceTo(stopPoint.location) < Constants.DISTANCE));
    }

    private StopPoint getLatestStopPoint() {
        return (!stopPoints.isEmpty() ? stopPoints.get(stopPoints.size() - 1) : null);
    }
}

