package com.vnsolution.virtualassistant.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTracking;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.StopPoint;
import com.vnsolution.virtualassistant.ui.travel.TravelViewModel;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DashboardFragment extends Fragment implements Injectable {
    private DashboardViewModel dashboardViewModel;
    private TravelViewModel travelViewModel;

    private List<Place> visitedPlaces = new ArrayList<>();
    private boolean isFirstRunning = true;

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
        View root = inflater.inflate(R.layout.screen_dashboard, container, false);

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
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    enableMyLocation();
                    requestLocationUpdates();
                }
            });
            startWatchingPermissions();
        }

        travelViewModel = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);
        travelViewModel.getPlaces().observe(getViewLifecycleOwner(), new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                visitedPlaces = places;
                presentVisitedPlaces(visitedPlaces);
            }
        });

        return root;
    }

    private void startWatchingPermissions() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StopTracking.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                enableMyLocation();
                requestLocationUpdates();
            }
        }, filter);
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (!PermissionUtils.hasPermissions(getActivity(), StopTracking.PERMISSIONS_LOCATION)) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (!PermissionUtils.hasPermissions(getActivity(), StopTracking.PERMISSIONS_LOCATION)) {
            return;
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(StopTracking.CONFIG_GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS);
        locationRequest.setFastestInterval(StopTracking.CONFIG_GEOLOCATION_FAST_INTERVAL_UPDATE_IN_MILLISECONDS);
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
        if (mLocationCallback != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void updateLocation() {
        if(mCurrentLocation != null && mGoogleMap != null && isFirstRunning) {
            isFirstRunning = false;
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        }
    }

    private void updateGeofence() {
        StopPoint stopPoint = getStopPointFromCache();
        if(stopPoint != null && mGoogleMap != null) {
            // Stop point.
            LatLng latLng = new LatLng(stopPoint.latitude, stopPoint.longitude);

            // Add stop point marker.
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(latLng.latitude + "," + latLng.longitude);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mCurrentMarker = mGoogleMap.addMarker(markerOptions);

            // Add geofence to current location.
            if (mCurrentCircle != null) {
                mCurrentCircle.remove();
            }
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .strokeColor(Color.argb(50, 70, 70, 70))
                    .fillColor(Color.argb(100, 150, 150, 150))
                    .radius(StopTracking.CONFIG_GEOFENCE_RADIUS_IN_METERS);
            mCurrentCircle = mGoogleMap.addCircle(circleOptions);
        }
    }

    private void presentVisitedPlaces(List<Place> places) {
        if(mGoogleMap != null) {
            for(Place place: places) {
                LatLng latLng = new LatLng(place.latitude, place.longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                String placeName = (place.placeName == null || place.placeName == "") ? "Unknown" : place.placeName;
                String placeAddress = (place.formattedAddress == null || place.formattedAddress == "") ? (place.latitude + "," + place.longitude) : place.formattedAddress;
                markerOptions.title(placeName + " (" + placeAddress + ")");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mGoogleMap.addMarker(markerOptions);
            }
        }
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
