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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.util.StringUtil;

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
import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.common.StopTracking;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceWithHistoryDetailEntity;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceHistory;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.model.StopPoint;
import com.vnsolution.virtualassistant.service.stop_tracking.StopTrackingManager;
import com.vnsolution.virtualassistant.ui.common.BaseAppActivity;
import com.vnsolution.virtualassistant.ui.travel.components.travelmatchedplaces.TravelMatchedPlacesAdapter;
import com.vnsolution.virtualassistant.ui.travel.components.travelmatchedplaces.TravelMatchedPlacesRecycleView;
import com.vnsolution.virtualassistant.ui.travel.events.OnTravelActionListerner;
import com.vnsolution.virtualassistant.util.MappingUtils;
import com.vnsolution.virtualassistant.util.PermissionUtils;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class TravelEditActivity extends BaseAppActivity implements OnTravelActionListerner {
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private TextView textPlaceName;
    private TextView textPlaceAddress;
    private TravelMatchedPlacesRecycleView matchedPlacesRecyclerView;
    private TravelMatchedPlacesAdapter matchedPlacesAdapter;

    private TravelViewModel travelViewModel;
    private Place visitedPlace;
    private List<GoogleMapNearByDto.NearBy> matchedPlaces = new ArrayList<>();
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

    private class UpdateVisitedPlaceTask extends AsyncTask<GoogleMapNearByDto.NearBy, Void, String> {

        @Override
        protected String doInBackground(GoogleMapNearByDto.NearBy... nearByList) {
            GoogleMapNearByDto.NearBy nearByPlace = nearByList.length > 0 ? nearByList[0] : null;
            if (nearByPlace != null) {
                PlaceEntity placeEntity = MappingUtils.convertNearByDtoToPlaceEntity(nearByPlace);
                placeEntity.id = visitedPlace.id;
                placeEntity.confirmed = 1;
                placeEntity.updatedDate = new Date();
                placeDao.update(placeEntity);
            }
            return visitedPlace.id;
        }

        @Override
        protected void onPostExecute(String placeId) {
            super.onPostExecute(placeId);
            Toast.makeText(TravelEditActivity.this, "The visited place is updated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_travel_edit);
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

        textPlaceName = findViewById(R.id.place_name);
        textPlaceAddress = findViewById(R.id.place_address);

        matchedPlacesRecyclerView = findViewById(R.id.list_matched_places);
        matchedPlacesRecyclerView.setHasFixedSize(true);
        matchedPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeTypes = stopTrackingManager.getPlaceTypes();
        matchedPlacesAdapter = new TravelMatchedPlacesAdapter(this, matchedPlaces, placeTypes);
        matchedPlacesRecyclerView.setAdapter(matchedPlacesAdapter);
        matchedPlacesRecyclerView.setOnItemAction(this);

        travelViewModel = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);

        Intent intent = getIntent();
        if (intent != null) {
            String placeId = intent.getStringExtra("PlaceId");
            travelViewModel.getPlaceById(placeId).observe(this, new Observer<Place>() {
                @Override
                public void onChanged(Place place) {
                    visitedPlace = place;
                    presentVisitedPlace(visitedPlace);
                    travelViewModel.searchNearBy(place).observe(TravelEditActivity.this, new Observer<List<GoogleMapNearByDto.NearBy>>() {
                        @Override
                        public void onChanged(@Nullable List<GoogleMapNearByDto.NearBy> nearByList) {
                            matchedPlaces = nearByList;
                            presentMatchedPlaces(matchedPlaces);
                        }
                    });
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
                //updateLocation();
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }

    private void updateLocation() {
        if(mCurrentLocation != null && mGoogleMap != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
        }
    }

    private void removeLocationUpdates() {
        if (mLocationCallback != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void presentVisitedPlace(Place place) {
        if (place != null) {
            if (mGoogleMap != null) {
                LatLng latLng = new LatLng(place.latitude, place.longitude);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                String placeName = (place.placeName == null || place.placeName == "") ? "Unknown Address" : place.placeName;
                String placeAddress = (place.formattedAddress == null || place.formattedAddress == "") ? (place.latitude + "," + place.longitude) : place.formattedAddress;
                markerOptions.title(placeName + " (" + placeAddress + ")");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mGoogleMap.addMarker(markerOptions);
            }
            textPlaceName.setText(place.placeName);
            textPlaceAddress.setText(place.formattedAddress);
        }
    }

    private void presentMatchedPlaces(List<GoogleMapNearByDto.NearBy> nearByList) {
        if (nearByList != null) {
//            if (mGoogleMap != null) {
//                for (GoogleMapNearByDto.NearBy nearBy : nearByList) {
//                    LatLng latLng = new LatLng(nearBy.geometry.location.lat, nearBy.geometry.location.lng);
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(latLng);
//                    String placeName = (nearBy.name == null || nearBy.name == "") ? "Unknown Address" : nearBy.name;
//                    String placeAddress = (nearBy.vicinity == null || nearBy.vicinity == "") ? (nearBy.geometry.location.lat + "," + nearBy.geometry.location.lng) : nearBy.vicinity;
//                    markerOptions.title(placeName + " (" + placeAddress + ")");
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                    mGoogleMap.addMarker(markerOptions);
//                }
//            }
            matchedPlacesAdapter.setData(nearByList);
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

    @Override
    public void onPlaceConfirmed(String placeId) {

    }

    @Override
    public void onPlaceEdited(String placeId) {

    }

    @Override
    public void onTap(View view, int position) {
        GoogleMapNearByDto.NearBy nearByPlace = matchedPlaces.get(position);
        new UpdateVisitedPlaceTask().execute(nearByPlace);
    }

    @Override
    public void onLongTap(View view, int position) {

    }

    @Override
    public void onDoubleTap(View view, int position) {

    }
}