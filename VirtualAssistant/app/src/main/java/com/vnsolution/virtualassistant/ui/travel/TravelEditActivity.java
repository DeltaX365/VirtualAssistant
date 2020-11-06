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
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDetailDto;
import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.service.place.PlaceManager;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.util.GoogleMapUtil;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class TravelEditActivity extends BaseActivity {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private TextView placeNameTextView;
    private TextView placeAddressTextView;
    private RecyclerView matchedPlacesRecyclerView;
    private RecyclerViewAdapter<GoogleMapNearByDto.NearBy> matchedPlacesAdapter;
    private TextView searchPlaceTextView;
    private TextView deletePlaceTextView;

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
    public PlaceManager placeManager;

    private class UpdatePlaceByNearByTask extends AsyncTask<GoogleMapNearByDto.NearBy, Void, String> {

        @Override
        protected String doInBackground(GoogleMapNearByDto.NearBy... nearByList) {
            GoogleMapNearByDto.NearBy nearByPlace = nearByList.length > 0 ? nearByList[0] : null;
            if (nearByPlace != null) {
                placeManager.confirmUpdatedPlace(visitedPlace.id, nearByPlace);
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

    private class UpdatePlaceByPlaceDetailTask extends AsyncTask<com.google.android.libraries.places.api.model.Place, Void, String> {

        @Override
        protected String doInBackground(com.google.android.libraries.places.api.model.Place... places) {
            com.google.android.libraries.places.api.model.Place googlePlace = places.length > 0 ? places[0] : null;
            if (googlePlace != null) {
                GoogleMapPlaceDetailDto.PlaceDetail placeDetail = placeManager.getPlaceDetailById(googlePlace.getId());
                placeManager.confirmUpdatedPlace(visitedPlace.id, placeDetail);
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

    private class RemovePlaceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... placeIds) {
            String placeId = placeIds.length > 0 ? placeIds[0] : null;
            if (placeId != null && placeId != "") {
                placeManager.removePlace(placeId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(TravelEditActivity.this, "The visited place is deleted", Toast.LENGTH_SHORT).show();
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

        placeNameTextView = findViewById(R.id.place_name);
        placeAddressTextView = findViewById(R.id.place_address);
        searchPlaceTextView = findViewById(R.id.search_place);
        deletePlaceTextView = findViewById(R.id.delete_place);

        matchedPlacesRecyclerView = findViewById(R.id.list_matched_places);
        matchedPlacesRecyclerView.setHasFixedSize(true);

        matchedPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeTypes = placeManager.getPlaceTypes();

        matchedPlacesAdapter = new RecyclerViewAdapter<GoogleMapNearByDto.NearBy>(this, UiOptions.PLACE_LIST_NEARBY, R.layout.item_nearby);
        matchedPlacesAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<GoogleMapNearByDto.NearBy>() {
            @Override
            public void onTap(GoogleMapNearByDto.NearBy nearByPlace) {
                super.onTap(nearByPlace);
                new UpdatePlaceByNearByTask().execute(nearByPlace);
            }
        });
        matchedPlacesRecyclerView.setAdapter(matchedPlacesAdapter);

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

        searchPlaceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(
                        com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                        com.google.android.libraries.places.api.model.Place.Field.ADDRESS_COMPONENTS,
                        com.google.android.libraries.places.api.model.Place.Field.ID,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
                );
                Intent autoCompleteActivityIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(TravelEditActivity.this);
                startActivityForResult(autoCompleteActivityIntent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        deletePlaceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemovePlaceTask().execute(visitedPlace.id);
            }
        });

        startWatchingPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);
            new UpdatePlaceByPlaceDetailTask().execute(place);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            LatLng latLng = new LatLng(place.latitude, place.longitude);
            String placeName = (place.placeName == null || place.placeName == "") ? "Unknown Address" : place.placeName;
            String placeAddress = (place.formattedAddress == null || place.formattedAddress == "") ? (place.latitude + "," + place.longitude) : place.formattedAddress;
            GoogleMapUtil.drawMarker(mGoogleMap, latLng, placeName + " (" + placeAddress + ")",true, 15f);
            placeNameTextView.setText(placeName);
            placeAddressTextView.setText(placeAddress);
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
}