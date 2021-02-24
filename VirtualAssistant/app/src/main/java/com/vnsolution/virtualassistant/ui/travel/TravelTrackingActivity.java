package com.vnsolution.virtualassistant.ui.travel;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.cache.UserCache;
import com.vnsolution.virtualassistant.common.StopTrackingSetting;
import com.vnsolution.virtualassistant.common.UiSetting;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.util.GoogleMapUtils;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TravelTrackingActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener {
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private float mGoogleMapZoom = 11f;

    private LinearLayout mDangerAreasBottomSheet;
    private BottomSheetBehavior mDangerAreasBottomSheetBehavior;
    private TextView mTotalTextView;
    private RecyclerView mDangerAreasRecyclerView;
    private RecyclerViewAdapter<Map<String, Object>> mDangerAreasAdapter;

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    private List<Map<String, Object>> mDangerAreas;

    private DocumentReference mCurrentUserDocRef;
    private DocumentReference mCovidVnDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUserDocRef = FirebaseFirestore.getInstance().collection("users").document(getCurrentUserUid());
        mCovidVnDocRef = FirebaseFirestore.getInstance().collection("areas").document("danger").collection("covid19").document("vietnam");
        setupView(savedInstanceState);
    }

    private void setupView(Bundle savedInstanceState) {
        setContentView(R.layout.screen_travel_tracking);
        setupActionBar();

        setupMap(savedInstanceState);
        setupBottomSheet();
    }

    private void setupMap(Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        startWatchingPermissions();
    }

    private void startWatchingPermissions() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StopTrackingSetting.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                requestLocationUpdates();
            }
        }, filter);
    }

    private void setupBottomSheet() {
        mDangerAreasBottomSheet = findViewById(R.id.bottomsheet_danger_areas);
        mTotalTextView = findViewById(R.id.total_danger_areas);
        mDangerAreasRecyclerView = findViewById(R.id.list_danger_areas);

        mDangerAreasBottomSheetBehavior = BottomSheetBehavior.from(mDangerAreasBottomSheet);

        mDangerAreasRecyclerView.setHasFixedSize(true);
        mDangerAreasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDangerAreasAdapter = new RecyclerViewAdapter<Map<String, Object>>(this, UiSetting.PLACE_LIST_DANGER, R.layout.item_place_danger);;
        mDangerAreasAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Map<String, Object>>() {
            @Override
            public void onTap(Map<String, Object> area) {
                try {
                    double latitude = Double.parseDouble((String) area.get("latitude"));
                    double longitude = Double.parseDouble((String) area.get("longitude"));
                    moveToPosition(latitude, longitude);
                    mDangerAreasBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } finally { }
            }
        });
        mDangerAreasRecyclerView.setAdapter(mDangerAreasAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnCameraMoveListener(this);

        // We will provide our own zoom controls.
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onCameraMove() {
        CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
        mGoogleMapZoom = (cameraPosition.zoom >= 11f)? cameraPosition.zoom : 11f;

        // Set bottom padding for map
        // Offseting the center of the MapFragment for an animation moving both the target lat/lng and the zoom level
        // https://stackoverflow.com/questions/15625970/offseting-the-center-of-the-mapfragment-for-an-animation-moving-both-the-target
        // https://stackoverflow.com/questions/13739075/how-to-get-latitude-longitude-span-in-google-map-v2-for-android
//        VisibleRegion visibleRegion = mGoogleMap.getProjection().getVisibleRegion();
//        double left = visibleRegion.latLngBounds.southwest.longitude;
//        double top = visibleRegion.latLngBounds.northeast.latitude;
//        double right = visibleRegion.latLngBounds.northeast.longitude;
//        double bottom = visibleRegion.latLngBounds.southwest.latitude;
//        double bottomPadding = GoogleMapUtils.dpToPx(TravelTrackingActivity.this, mDangerAreasBottomSheet.getHeight());
//        mGoogleMap.setPadding((int) left, (int) top, (int) right, (int) (bottom - bottomPadding));
    }

    private void moveToPosition(double latitude, double longitude) {
        if (mGoogleMap != null && latitude > 0 && longitude > 0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), mGoogleMapZoom);
//            mGoogleMap.moveCamera(cameraUpdate);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        requestLocationUpdates();
        loadDangerAreas();
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (!PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_LOCATION)) {
            return;
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setSmallestDisplacement(10.0f);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mCurrentLocation = locationResult.getLastLocation();
                syncLocationUp();
                presentCurrentLocationAndDangerAreas();
            }
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }

    private void syncLocationUp() {
        if(mCurrentLocation != null) {
            Map<String, Object> geolocation = new HashMap<>();
            geolocation.put("latitude", mCurrentLocation.getLatitude());
            geolocation.put("longitude", mCurrentLocation.getLongitude());
            geolocation.put("accuracy", mCurrentLocation.getAccuracy());
            geolocation.put("altitude", mCurrentLocation.getAltitude());
            geolocation.put("bearing", mCurrentLocation.getBearing());
            geolocation.put("provider", mCurrentLocation.getProvider());
            geolocation.put("speed", mCurrentLocation.getSpeed());
            geolocation.put("time", mCurrentLocation.getTime());

            Map<String, Object> data = new HashMap<>();
            data.put("location", geolocation);

            mCurrentUserDocRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Virtual Assistant", "Geolocation has been synced!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Virtual Assistant", "Geolocation was not synced!");
                }
            });
        }
    }

    private void loadDangerAreas() {
        mCovidVnDocRef.collection("list")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        mDangerAreas = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            mDangerAreas.add(data);
                        }
                        presentCurrentLocationAndDangerAreas();
                        String total = mDangerAreas.size() + " " + ((mDangerAreas.size() == 1) ? "danger area" : "danger areas");
                        mTotalTextView.setText(total);
                        mDangerAreasAdapter.setData(mDangerAreas);
                    } else {
                        Log.d("Virtual Assistant", "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    private void presentCurrentLocationAndDangerAreas() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();

            // Present current location
            if (mCurrentLocation != null) {
                LatLng myLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                GoogleMapUtils.drawMarker(mGoogleMap, myLatLng, R.drawable.ic_red_dot_10);
                moveToPosition(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            }

            // Present danger areas
            if (mDangerAreas != null) {
                for (Map<String, Object> area : mDangerAreas) {
                    if (area != null && area.containsKey("latitude") && area.containsKey("longitude")) {
                        double latitude;
                        double longitude;
                        try {
                            latitude = Double.parseDouble((String) area.get("latitude"));
                            longitude = Double.parseDouble((String) area.get("longitude"));
                        } finally { }
                        if (latitude > 0 && longitude > 0) {
                            LatLng latLng = new LatLng(latitude, longitude);
                            String placeName = (area.get("address") == null) ? "" : (String) area.get("address");
                            GoogleMapUtils.drawMarker(mGoogleMap, latLng, placeName, R.drawable.img_baseline_coronavirus_24);
                        }
                    }
                }
            }
        }
    }

    private void removeLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private String getCurrentUserUid() {
        String currentUserUid = UserCache.getUserUid(this);
        if (currentUserUid == null || currentUserUid.isEmpty()) {
            currentUserUid = UUID.randomUUID().toString();
            UserCache.saveUserUid(this, currentUserUid);
        }
        return currentUserUid;
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