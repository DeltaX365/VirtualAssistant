package com.vnsolution.virtualassistant.ui.travel;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTracking;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.model.StopPoint;
import com.vnsolution.virtualassistant.service.stop_tracking.StopTrackingManager;
import com.vnsolution.virtualassistant.ui.travel.components.travelmatchedplaces.TravelMatchedPlacesAdapter;
import com.vnsolution.virtualassistant.ui.travel.components.travelmatchedplaces.TravelMatchedPlacesRecycleView;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TravelEditActivity extends AppCompatActivity {
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private TextView tvTotalMatchedPlace;
    private TravelMatchedPlacesRecycleView matchedPlacesRecyclerView;
    private TravelMatchedPlacesAdapter matchedPlacesAdapter;

    private TravelViewModel travelViewModel;
    private List<Place> matchedPlaces = new ArrayList<>();
    private List<PlaceType> placeTypes = new ArrayList<>();

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mCurrentLocation;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public StopTrackingManager stopTrackingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_travel_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(this);
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
            }
        });

        tvTotalMatchedPlace = findViewById(R.id.total_matched_places);

        matchedPlacesRecyclerView = findViewById(R.id.list_matched_places);
        matchedPlacesRecyclerView.setHasFixedSize(true);
        matchedPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeTypes = stopTrackingManager.getPlaceTypes();
        matchedPlacesAdapter = new TravelMatchedPlacesAdapter(this, matchedPlaces, placeTypes);
        matchedPlacesRecyclerView.setAdapter(matchedPlacesAdapter);

        StopPoint stopPoint = null;
        Intent intent = getIntent();
        if (intent != null) {
            double longitude = intent.getDoubleExtra("longitude", 0);
            double latitude = intent.getDoubleExtra("latitude", 0);
            if (longitude > 0 && latitude > 0) {
                stopPoint = new StopPoint(longitude, latitude, null, null);
            }
        }
        if (stopPoint != null) {
            travelViewModel = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);
            travelViewModel.searchNearBy(stopPoint).observe(this, new Observer<List<Place>>() {
                @Override
                public void onChanged(@Nullable List<Place> places) {
                    matchedPlaces = places;
                    presentVisitedPlaces(matchedPlaces);
                    String total = matchedPlaces.size() + " " + ((matchedPlaces.size() == 1) ? "matched place" : "matched places");
                    tvTotalMatchedPlace.setText(total);
                    matchedPlacesAdapter.setData(matchedPlaces);
                }
            });
        }

        startWatchingPermissions();
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (!PermissionUtils.hasPermissions(this, StopTracking.PERMISSIONS_LOCATION)) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    private void startWatchingPermissions() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StopTracking.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                enableMyLocation();
                requestLocationUpdates();
            }
        }, filter);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (!PermissionUtils.hasPermissions(this, StopTracking.PERMISSIONS_LOCATION)) {
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
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }

    private void updateLocation() {
        if(mCurrentLocation != null && mGoogleMap != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
        }
    }

    private void removeLocationUpdates() {
        if (mLocationCallback != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        removeLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}