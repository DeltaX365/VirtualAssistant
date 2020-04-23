package com.vnsolution.virtualassistant.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.ui.geolocations.GeolocationHistoryActivity;
import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.PlaceHistory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DashboardActivity extends AppCompatActivity {
    private String TAG = DashboardActivity.class.getSimpleName();

    private static final int ALL_PERMISSIONS_RESULT = 1000;
    private String[] permissions = { Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION };
    BroadcastReceiver broadcastReceiver;

    private TextView txtActivity, txtConfidence, txtPlaces;
    private ImageView imgActivity;
    private List<Place> places = new ArrayList<>();
    private List<PlaceHistory> placeHistories = new ArrayList<>();
    private String history = "";

    @Inject
    public PlaceDao placeDao;

    @Inject
    public PlaceHistoryDao placeHistoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtActivity = findViewById(R.id.txt_activity);
        txtConfidence = findViewById(R.id.txt_confidence);
        imgActivity = findViewById(R.id.img_activity);
        txtPlaces = findViewById(R.id.places);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_ACTIVITY_DETECTED)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                } else if (intent.getAction().equals(Constants.BROADCAST_PLACE_ADDED)) {
                    fetchPlaceHistory();
                }
            }
        };

        checkPermissions();
    }

    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.activity_unknown);
        int icon = R.drawable.ic_still;

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.activity_in_vehicle);
                icon = R.drawable.ic_driving;
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = getString(R.string.activity_on_bicycle);
                icon = R.drawable.ic_on_bicycle;
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = getString(R.string.activity_on_foot);
                icon = R.drawable.ic_walking;
                break;
            }
            case DetectedActivity.RUNNING: {
                label = getString(R.string.activity_running);
                icon = R.drawable.ic_running;
                break;
            }
            case DetectedActivity.STILL: {
                label = getString(R.string.activity_still);
                break;
            }
            case DetectedActivity.TILTING: {
                label = getString(R.string.activity_tilting);
                icon = R.drawable.ic_tilting;
                break;
            }
            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                icon = R.drawable.ic_walking;
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = getString(R.string.activity_unknown);
                break;
            }
        }

        Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);

        txtActivity.setText(label);
        txtConfidence.setText("Confidence: " + confidence);
        imgActivity.setImageResource(icon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_ACTIVITY_DETECTED);
        filter.addAction(Constants.BROADCAST_PLACE_ADDED);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{permission}, ALL_PERMISSIONS_RESULT);
                    return;
                }
                String action = "";
                switch (permission) {
                    case Manifest.permission.ACTIVITY_RECOGNITION:
                        action = Constants.BROADCAST_ACTIVITY_RECOGNITION_PERMISSION_CHANGED;
                        break;
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                    case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                        action = Constants.BROADCAST_LOCATION_PERMISSION_CHANGED;
                        break;
                }
                if (!action.isEmpty()) {
                    Intent intent = new Intent(action);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        }
    }

    private void fetchPlaceHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                places = placeDao.getAll();
                placeHistories = placeHistoryDao.getAll();
                for( PlaceHistory placeHistory : placeHistories) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Place place = places.stream().filter(p -> p.id.equals(placeHistory.placeId)).findFirst().get();
                        if (place != null) {
                            history += place.formattedAddress + " >>> " + placeHistory.enterTime.toString() + " - " + placeHistory.existTime.toString() + "\n";
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtPlaces.setText(history);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            checkPermissions();
        }
    }
}
