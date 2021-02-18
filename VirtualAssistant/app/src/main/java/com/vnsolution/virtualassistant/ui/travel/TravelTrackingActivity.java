package com.vnsolution.virtualassistant.ui.travel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.cache.UserCache;
import com.vnsolution.virtualassistant.common.StopTrackingSetting;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.util.GoogleMapUtils;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TravelTrackingActivity extends BaseActivity {
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;

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
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;
//                mGoogleMap.setPadding(0, 0, 0, GoogleMapUtils.dpToPx(TravelTrackingActivity.this, 350));
//                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
//                enableMyLocation();
//                adjustMyLocation();
            }
        });
        startWatchingPermissions();
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (!PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_LOCATION)) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        requestLocationUpdates();
        //refreshView();
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
                refreshView();
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

    private void refreshView() {
        mCovidVnDocRef.collection("list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && mGoogleMap != null) {
                            mGoogleMap.clear();

                            if (mCurrentLocation != null) {
                                LatLng myLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                                GoogleMapUtils.drawMarker(mGoogleMap, myLatLng, R.drawable.ic_red_dot_10);
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18f));
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                if (data != null && data.containsKey("latitude") && data.containsKey("longitude")) {
                                    LatLng latLng = new LatLng(Double.valueOf((String) data.get("latitude")), Double.valueOf((String) data.get("longitude")));
                                    String placeName = (String) data.get("address");
                                    GoogleMapUtils.drawMarker(mGoogleMap, latLng, placeName, R.drawable.img_baseline_coronavirus_24);
                                }
                            }
                        } else {
                            Log.d("Virtual Assistant", "Error getting documents: ", task.getException());
                        }
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
}