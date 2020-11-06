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
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.service.place.PlaceManager;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.util.DateTimeUtils;
import com.vnsolution.virtualassistant.util.GoogleMapUtil;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class TravelHistoryActivity extends BaseActivity {
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private TextView totalTextView;
    private RecyclerView travelHistoryRecyclerView;
    private RecyclerViewAdapter<Place> visitedPlacesAdapter;
    private RecyclerViewAdapter<String> visitedRegionsAdapter;

    private TravelViewModel vm;
    private List<Place> visitedPlaces = new ArrayList<>();
    private List<String> visitedRegions = new ArrayList<>();
    private List<PlaceType> placeTypes = new ArrayList<>();
    private String uiOption;

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mCurrentLocation;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public PlaceManager placeManager;

    private class ConfirmVisitedPlaceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String placeId = params.length > 0 ? params[0] : "";
            PlaceEntity placeEntity = placeManager.confirmDetectedPlace(placeId);
            return (placeEntity != null && placeEntity.confirmed == 0) ? placeId : null;
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
                visitedPlacesAdapter.setData(visitedPlaces);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);
        setupView(savedInstanceState);
    }

    private void setupView(Bundle savedInstanceState) {
        setContentView(R.layout.screen_travel_history);
        setupActionBar();

        placeTypes = placeManager.getPlaceTypes();
        Intent intent = getIntent();
        if (intent != null) {
            uiOption = intent.getStringExtra("UiOption");
        }

        setupMap(savedInstanceState);
        setupBottomSheet();
    }

    private void setupMap(Bundle savedInstanceState) {
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
        startWatchingPermissions();
    }

    private void setupBottomSheet() {
        totalTextView = findViewById(R.id.total_visited_places);
        travelHistoryRecyclerView = findViewById(R.id.list_visited_places);
        travelHistoryRecyclerView.setHasFixedSize(true);

        if (uiOption.equals(UiOptions.PLACE_SUMMARY_YESTERDAY) || uiOption.equals(UiOptions.PLACE_SUMMARY_ALL)) {
            travelHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            visitedPlacesAdapter = new RecyclerViewAdapter<Place>(this, uiOption, R.layout.item_place);
            visitedPlacesAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Place>() {
                @Override
                public void onTap(Place place) {
                    super.onTap(place);
                    Intent intent = new Intent(TravelHistoryActivity.this, TravelViewActivity.class);
                    intent.putExtra("PlaceId", place.id);
                    startActivity(intent);
                }

                @Override
                public void onAction(Place place, String action) {
                    super.onAction(place, action);
                    if (action == UiOptions.PLACE_ACTION_CONFIRM) {
                        new ConfirmVisitedPlaceTask().execute(place.id);
                    }
                    if (action == UiOptions.PLACE_ACTION_EDIT) {
                        Intent intent = new Intent(TravelHistoryActivity.this, TravelEditActivity.class);
                        intent.putExtra("PlaceId", place.id);
                        startActivity(intent);
                    }
                }
            });
            travelHistoryRecyclerView.setAdapter(visitedPlacesAdapter);
        }
        if (uiOption.equals(UiOptions.PLACE_SUMMARY_CITY) || uiOption.equals(UiOptions.PLACE_SUMMARY_COUNTRY)) {
            travelHistoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            visitedRegionsAdapter = new RecyclerViewAdapter<String>(this, uiOption, R.layout.card_region);
            travelHistoryRecyclerView.setAdapter(visitedRegionsAdapter);
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (!PermissionUtils.hasPermissions(this, StopTrackingOptions.PERMISSIONS_LOCATION)) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    private void startWatchingPermissions() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StopTrackingOptions.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
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
        if (!PermissionUtils.hasPermissions(this, StopTrackingOptions.PERMISSIONS_LOCATION)) {
            return;
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(StopTrackingOptions.CONFIG_GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS);
        locationRequest.setFastestInterval(StopTrackingOptions.CONFIG_GEOLOCATION_FAST_INTERVAL_UPDATE_IN_MILLISECONDS);
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
        if(places != null) {
            for(Place place: places) {
                LatLng latLng = new LatLng(place.latitude, place.longitude);
                String placeName = (place.placeName != null && place.placeName != "")? place.placeName : (place.latitude + ", " + place.longitude);
                GoogleMapUtil.drawMarker(mGoogleMap, latLng, placeName, R.drawable.ic_red_dot_10);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        requestLocationUpdates();
        if (uiOption.equals(UiOptions.PLACE_SUMMARY_ALL) || uiOption.equals(UiOptions.PLACE_SUMMARY_YESTERDAY)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            Date yesterday = calendar.getTime();
            Date startOfYesterday = DateTimeUtils.getStartOfDay(yesterday);
            Date endOfYesterday = DateTimeUtils.getEndOfDay(yesterday);
            LiveData<List<Place>> placesLiveData = (uiOption.equals(UiOptions.PLACE_SUMMARY_ALL)) ? vm.getPlaces() : vm.getPlacesByDateRange(startOfYesterday, endOfYesterday);
            placesLiveData.observe(this, new Observer<List<Place>>() {
                @Override
                public void onChanged(@Nullable List<Place> places) {
                    visitedPlaces = places;
                    presentVisitedPlaces(visitedPlaces);
                    String total = visitedPlaces.size() + " " + ((visitedPlaces.size() == 1) ? "visited place" : "visited places");
                    totalTextView.setText(total);
                    visitedPlacesAdapter.setData(visitedPlaces);
                }
            });
        }
        if (uiOption.equals(UiOptions.PLACE_SUMMARY_COUNTRY) || uiOption.equals(UiOptions.PLACE_SUMMARY_CITY)) {
            LiveData<List<String>> regionsLiveData = (uiOption.equals(UiOptions.PLACE_SUMMARY_COUNTRY)) ? vm.getVisitedCountries() : vm.getVisitedCities();
            regionsLiveData.observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(@Nullable List<String> regions) {
                    visitedRegions = regions;
                    String total = visitedRegions.size() + " " + ((visitedRegions.size() == 1) ? "visited region" : "visited regions");
                    totalTextView.setText(total);
                    visitedRegionsAdapter.setData(visitedRegions);
                }
            });
        }
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