package com.vnsolution.virtualassistant.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;

import java.util.List;

public class DashboardActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int ALL_PERMISSIONS_RESULT = 1000;

    private String[] permissions = {
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    private GoogleMap mGoogleMap;
    private Marker mCurrentMarker;
    private Circle mCurrentCircle;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkPermissions()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{permission}, ALL_PERMISSIONS_RESULT);
                    return false;
                }
                String action = "";
                switch (permission) {
                    case Manifest.permission.ACTIVITY_RECOGNITION:
                        action = Constants.BROADCAST_ACTIVITY_RECOGNITION_PERMISSION_CHANGED;
                        break;
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                    case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                        action = Constants.BROADCAST_LOCATION_PERMISSION_CHANGED;
                        break;
                }
                if (!action.isEmpty()) {
                    Intent intent = new Intent(action);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            checkPermissions();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Add a marker in current location.
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                handleLocationUpdated(location);
            }
        });
    }

    private void handleLocationUpdated(Location location) {
        if(location != null && mGoogleMap != null) {
            //Place current location marker.
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(latLng.latitude + "," + latLng.longitude);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mCurrentMarker = mGoogleMap.addMarker(markerOptions);

            //Add geofence to current location.
            if (mCurrentCircle != null) {
                mCurrentCircle.remove();
            }
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .strokeColor(Color.argb(50, 70, 70, 70))
                    .fillColor(Color.argb(100, 150, 150, 150))
                    .radius(Constants.GEOFENCE_RADIUS_IN_METERS);
            mCurrentCircle = mGoogleMap.addCircle(circleOptions);

            //Move map camera.
            float zoom = 15f;
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeLocationUpdates();
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.GEOLOCATION_INTERVAL_UPDATE);
        locationRequest.setFastestInterval(Constants.GEOLOCATION_FAST_INTERVAL_UPDATE);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);
                    handleLocationUpdated(location);
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }

    private void removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}
