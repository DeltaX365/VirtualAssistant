package vn.sttechnology.virtualassistant.ui.travel;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.common.StopTrackingSetting;
import vn.sttechnology.virtualassistant.common.UiSetting;
import vn.sttechnology.virtualassistant.db.entity.PlaceEntity;
import vn.sttechnology.virtualassistant.model.Place;
import vn.sttechnology.virtualassistant.model.PlaceType;
import vn.sttechnology.virtualassistant.service.place.PlaceManager;
import vn.sttechnology.virtualassistant.ui.common.BaseActivity;
import vn.sttechnology.virtualassistant.ui.common.RecyclerViewAdapter;
import vn.sttechnology.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import vn.sttechnology.virtualassistant.util.DateTimeUtils;
import vn.sttechnology.virtualassistant.util.GoogleMapUtils;
import vn.sttechnology.virtualassistant.util.PermissionUtils;

import androidx.annotation.NonNull;
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

    private LinearLayout visitedPlaceBottomSheet;
    private TextView totalTextView;
    private RecyclerView travelHistoryRecyclerView;
    private RecyclerViewAdapter<Place> visitedPlacesAdapter;
    private RecyclerViewAdapter<String> visitedRegionsAdapter;

    private TravelViewModel vm;
    private List<Place> visitedPlaces = new ArrayList<>();
    private List<String> visitedRegions = new ArrayList<>();
    private List<PlaceType> placeTypes = new ArrayList<>();
    private String uiOption;
    private Date selectedDate;
    private String selectedCategory;
    private String selectedCity;
    private String selectedCountry;

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

    private class ToggleFavouritePlaceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String placeId = params.length > 0 ? params[0] : "";
            PlaceEntity placeEntity = placeManager.toggleFavouritePlace(placeId);
            return (placeEntity != null) ? placeId : null;
        }

        @Override
        protected void onPostExecute(String placeId) {
            super.onPostExecute(placeId);
            if (visitedPlaces != null && placeId != null) {
                for(Place place : visitedPlaces) {
                    if (place.id == placeId) {
                        place.favourite = (place.favourite == 0)? 1 : 0;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (uiOption.equals(UiSetting.PLACE_SUMMARY_YESTERDAY)) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_travel_history_options, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_calendar:
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                if (selectedDate != null) {
                    builder.setSelection(selectedDate.getTime());
                }
                MaterialDatePicker<Long> picker = builder.build();
                picker.show(getSupportFragmentManager(), picker.toString());
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);
                        selectedDate = calendar.getTime();
                        refreshView();
                    }
                });
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void setupView(Bundle savedInstanceState) {
        setContentView(R.layout.screen_travel_history);
        setupActionBar();

        Intent intent = getIntent();
        if (intent != null) {
            uiOption = intent.getStringExtra("UiOption");
            selectedCategory = intent.getStringExtra("Category");
            selectedCity = intent.getStringExtra("City");
            selectedCountry = intent.getStringExtra("Country");
        }
        placeTypes = placeManager.getPlaceTypes();

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
                mGoogleMap.setPadding(0, 0, 0, GoogleMapUtils.dpToPx(TravelHistoryActivity.this, 350));
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                enableMyLocation();
                adjustMyLocation();
            }
        });
        startWatchingPermissions();
    }

    private void setupBottomSheet() {
        visitedPlaceBottomSheet = findViewById(R.id.bottomsheet_visited_places);
        totalTextView = findViewById(R.id.total_visited_places);
        travelHistoryRecyclerView = findViewById(R.id.list_visited_places);
        travelHistoryRecyclerView.setHasFixedSize(true);

        if (uiOption.equals(UiSetting.PLACE_SUMMARY_YESTERDAY) || uiOption.equals(UiSetting.PLACE_SUMMARY_ALL)) {
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
                    if (action.equals(UiSetting.PLACE_ACTION_CONFIRM)) {
                        new ConfirmVisitedPlaceTask().execute(place.id);
                    }
                    if (action.equals(UiSetting.PLACE_ACTION_EDIT)) {
                        Intent intent = new Intent(TravelHistoryActivity.this, TravelEditActivity.class);
                        intent.putExtra("PlaceId", place.id);
                        startActivity(intent);
                    }
                    if (action.equals(UiSetting.PLACE_ACTION_FAVOURITE)) {
                        new ToggleFavouritePlaceTask().execute(place.id);
                    }
                }
            });
            travelHistoryRecyclerView.setAdapter(visitedPlacesAdapter);
        }
        if (uiOption.equals(UiSetting.PLACE_SUMMARY_CITY) || uiOption.equals(UiSetting.PLACE_SUMMARY_COUNTRY)) {
            travelHistoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            visitedRegionsAdapter = new RecyclerViewAdapter<String>(this, uiOption, R.layout.card_region);
            visitedRegionsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<String>() {
                @Override
                public void onTap(String region) {
                    super.onTap(region);
                    Intent intent = new Intent(TravelHistoryActivity.this, TravelHistoryActivity.class);
                    if (uiOption.equals(UiSetting.PLACE_SUMMARY_COUNTRY)) {
                        intent.putExtra("UiOption", UiSetting.PLACE_SUMMARY_CITY);
                        intent.putExtra("Country", region);
                    } else {
                        intent.putExtra("UiOption", UiSetting.PLACE_SUMMARY_ALL);
                        intent.putExtra("City", region);
                    }
                    startActivity(intent);
                }
            });
            travelHistoryRecyclerView.setAdapter(visitedRegionsAdapter);
        }

        adjustMyLocation();
    }

    private void adjustMyLocation() {
//        if (visitedPlaceBottomSheet != null) {
//            BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(visitedPlaceBottomSheet);
//            sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                @Override
//                public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                    if (mGoogleMap != null) {
//                        int paddingBottom = GoogleMapUtil.dpToPx(TravelHistoryActivity.this, bottomSheet.getHeight());
//                        mGoogleMap.setPadding(0, 0, 0, paddingBottom);
//                    }
//                }
//
//                @Override
//                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                    if (mGoogleMap != null) {
//                        int paddingBottom = GoogleMapUtil.dpToPx(TravelHistoryActivity.this, bottomSheet.getHeight());
//                        mGoogleMap.setPadding(0, 0, 0, paddingBottom);
//                    }
//                }
//            });
//        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (!PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_LOCATION)) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    private void startWatchingCameraMove() {
        if (mGoogleMap != null) {
            mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    // Set bottom padding for map
                    // Offseting the center of the MapFragment for an animation moving both the target lat/lng and the zoom level
                    // https://stackoverflow.com/questions/15625970/offseting-the-center-of-the-mapfragment-for-an-animation-moving-both-the-target
                    // https://stackoverflow.com/questions/13739075/how-to-get-latitude-longitude-span-in-google-map-v2-for-android
                    VisibleRegion visibleRegion = mGoogleMap.getProjection().getVisibleRegion();
                    double left = visibleRegion.latLngBounds.southwest.longitude;
                    double top = visibleRegion.latLngBounds.northeast.latitude;
                    double right = visibleRegion.latLngBounds.northeast.longitude;
                    double bottom = visibleRegion.latLngBounds.southwest.latitude;
                    double bottomPadding = 350;
                    mGoogleMap.setPadding((int) left, (int) top, (int) right, (int) (bottom - bottomPadding));
                }
            });
        }
    }

    private void startWatchingPermissions() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StopTrackingSetting.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
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
        if (!PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_LOCATION)) {
            return;
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(StopTrackingSetting.CONFIG_GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS);
        locationRequest.setFastestInterval(StopTrackingSetting.CONFIG_GEOLOCATION_FAST_INTERVAL_UPDATE_IN_MILLISECONDS);
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
        if(places != null && mGoogleMap != null) {
            mGoogleMap.clear();
            for(Place place: places) {
                LatLng latLng = new LatLng(place.latitude, place.longitude);
                String placeName = (place.placeName != null && place.placeName != "")? place.placeName : (place.latitude + ", " + place.longitude);
                GoogleMapUtils.drawMarker(mGoogleMap, latLng, placeName, R.drawable.ic_red_dot_10);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        requestLocationUpdates();
        refreshView();
    }

    private void refreshView() {
        if (uiOption.equals(UiSetting.PLACE_SUMMARY_ALL) || uiOption.equals(UiSetting.PLACE_SUMMARY_YESTERDAY)) {
            LiveData<List<Place>> placesLiveData;
            if (uiOption.equals(UiSetting.PLACE_SUMMARY_ALL)) {
                if ((selectedCity != null && selectedCity != "")) {
                    placesLiveData = vm.getPlacesByCity(selectedCity);
                } else if ((selectedCategory != null && selectedCategory != "")) {
                    placesLiveData = vm.getPlacesByCategory(selectedCategory);
                } else {
                    placesLiveData = vm.getPlaces();
                }
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -1);
                selectedDate = (selectedDate != null)? selectedDate : calendar.getTime();
                Date startOfYesterday = DateTimeUtils.getStartOfDay(selectedDate);
                Date endOfYesterday = DateTimeUtils.getEndOfDay(selectedDate);
                placesLiveData = vm.getPlacesByDateRange(startOfYesterday, endOfYesterday);
            }
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
        if (uiOption.equals(UiSetting.PLACE_SUMMARY_COUNTRY) || uiOption.equals(UiSetting.PLACE_SUMMARY_CITY)) {
            LiveData<List<String>> regionsLiveData;
            if (uiOption.equals(UiSetting.PLACE_SUMMARY_COUNTRY)) {
                regionsLiveData = vm.getVisitedCountries();
            } else {
                if (selectedCountry != null && selectedCountry != "") {
                    regionsLiveData = vm.getVisitedCitiesByCountry(selectedCountry);
                } else {
                    regionsLiveData = vm.getVisitedCities();
                }
            }
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