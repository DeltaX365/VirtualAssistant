package com.vnsolution.virtualassistant.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.StopPoint;

import javax.inject.Inject;

public class DashboardFragment extends Fragment implements Injectable {
    private DashboardViewModel dashboardViewModel;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Marker mCurrentMarker;
    private Circle mCurrentCircle;
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private Context mContext;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        requestLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
        removeLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (mContext != null) {
            // https://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            mMapView = (MapView) root.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume(); // needed to get the map to display immediately
            try {
                MapsInitializer.initialize(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    mGoogleMap = mMap;
                    mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                    mGoogleMap.setMyLocationEnabled(true);
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    requestLocationUpdates();
                }
            });
        }

        return root;
    }

    private void updateLocation() {
        if(mCurrentLocation != null && mGoogleMap != null) {
            //Place current location marker.
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(latLng.latitude + "," + latLng.longitude);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mCurrentMarker = mGoogleMap.addMarker(markerOptions);
        }
    }

    private void updateGeofence() {
        StopPoint stopPoint = getStopPointFromCache();
        if(stopPoint != null && mGoogleMap != null) {
            //Add geofence to current location.
            if (mCurrentCircle != null) {
                mCurrentCircle.remove();
            }
            LatLng latLng = new LatLng(stopPoint.latitude, stopPoint.longitude);
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

    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.GEOLOCATION_INTERVAL_UPDATE);
        locationRequest.setFastestInterval(Constants.GEOLOCATION_FAST_INTERVAL_UPDATE);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mCurrentLocation = locationResult.getLastLocation();
                updateLocation();
                updateGeofence();
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }

    private void removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private StopPoint getStopPointFromCache() {
        String stopPointString = "";
        if (mContext != null) {
            SharedPreferences sharedPref = mContext.getSharedPreferences("com.vnsolution.VirtualAssistant", Context.MODE_PRIVATE);
            stopPointString = sharedPref.getString("StopPoint", "");
        }
        Gson gson = new Gson();
        return !stopPointString.isEmpty() ? gson.fromJson(stopPointString, StopPoint.class) : null;
    }
}
