package com.vnsolution.virtualassistant.ui.travel;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

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
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.service.stop_tracking.StopTrackingManager;
import com.vnsolution.virtualassistant.ui.common.BaseAppActivity;
import com.vnsolution.virtualassistant.ui.travel.events.OnTravelActionListerner;
import com.vnsolution.virtualassistant.ui.travel.components.travelvisitedplaces.TravelVisitedPlaceAdapter;
import com.vnsolution.virtualassistant.ui.travel.components.travelvisitedplaces.TravelVisitedPlaceRecycleView;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TravelHistoryActivity extends BaseAppActivity implements OnTravelActionListerner {
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private TextView tvTotalVisitedPlace;
    private TravelVisitedPlaceRecycleView travelRecyclerView;
    private TravelVisitedPlaceAdapter travelAdapter;

    private TravelViewModel travelViewModel;
    private List<Place> visitedPlaces = new ArrayList<>();
    private List<PlaceType> placeTypes = new ArrayList<>();

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mCurrentLocation;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public StopTrackingManager stopTrackingManager;

    @Inject
    public PlaceDao placeDao;

    private class ConfirmVisitedPlaceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String placeId = params.length > 0 ? params[0] : "";
            PlaceEntity placeEntity = placeDao.getById(placeId);
            if (placeEntity != null && placeEntity.confirmed == 0) {
                placeEntity.confirmed = 1;
                placeDao.update(placeEntity);
                return placeId;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String placeId) {
            super.onPostExecute(placeId);
            if (visitedPlaces != null && placeId != null) {
                for(Place place : visitedPlaces) {
                    if (place.id == placeId) {
                        place.confirmed = 1;
                        break;
                    }
                }
                travelAdapter.setData(visitedPlaces);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_travel_history);
        setupActionBar();

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

        tvTotalVisitedPlace = findViewById(R.id.total_matched_places);

        travelRecyclerView = findViewById(R.id.list_visited_places);
        travelRecyclerView.setHasFixedSize(true);
        travelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeTypes = stopTrackingManager.getPlaceTypes();
        travelAdapter = new TravelVisitedPlaceAdapter(this, visitedPlaces, placeTypes);
        travelRecyclerView.setAdapter(travelAdapter);
        travelRecyclerView.setOnItemAction(this);

        travelViewModel = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);
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
        travelViewModel.getPlaces().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                visitedPlaces = places;
                presentVisitedPlaces(visitedPlaces);
                String total = visitedPlaces.size() + " " + ((visitedPlaces.size() == 1)? "visited place" : "visited places");
                tvTotalVisitedPlace.setText(total);
                travelAdapter.setData(visitedPlaces);
            }
        });
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

    @Override
    public void onTap(View view, int position) {

    }

    @Override
    public void onLongTap(View view, int position) {

    }

    @Override
    public void onDoubleTap(View view, int position) {

    }

    @Override
    public void onPlaceConfirmed(String placeId) {
        new ConfirmVisitedPlaceTask().execute(placeId);
    }

    @Override
    public void onPlaceEdited(String placeId) {
        Intent travelEditIntent = new Intent(TravelHistoryActivity.this, TravelEditActivity.class);
        travelEditIntent.putExtra("PlaceId", placeId);
        startActivity(travelEditIntent);
    }
}