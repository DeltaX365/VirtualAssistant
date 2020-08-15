package com.vnsolution.virtualassistant.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTracking;
import com.vnsolution.virtualassistant.ui.applications.InstalledApplicationsActivity;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsActivity;
import com.vnsolution.virtualassistant.ui.places.VisitedPlacesActivity;
import com.vnsolution.virtualassistant.ui.settings.SettingsActivity;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class HomeV2Activity extends AppCompatActivity implements HasAndroidInjector {
    private Button btnShowVisitedPlaces;
    private Button btnShowInstalledApps;
    private Button btnShowCallLogs;
    private Button btnShowSettings;

    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_home_v2);

        btnShowVisitedPlaces = findViewById(R.id.btn_visited_places);
        btnShowVisitedPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeV2Activity.this, VisitedPlacesActivity.class);
                startActivity(intent);
            }
        });

        btnShowInstalledApps = findViewById(R.id.btn_installed_applications);
        btnShowInstalledApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeV2Activity.this, InstalledApplicationsActivity.class);
                startActivity(intent);
            }
        });

        btnShowCallLogs = findViewById(R.id.btn_call_logs);
        btnShowCallLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeV2Activity.this, CallLogsActivity.class);
                startActivity(intent);
            }
        });

        btnShowSettings = findViewById(R.id.btn_settings);
        btnShowSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeV2Activity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        requestPermissions();
    }

    private boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtils.hasPermissions(this, StopTracking.PERMISSIONS_ACTIVITY_RECOGNITION)) {
                requestPermissions(StopTracking.PERMISSIONS_ACTIVITY_RECOGNITION, StopTracking.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            Intent intentUA = new Intent(StopTracking.BROADCAST_PERMISSIONS_USER_ACTIVITY_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentUA);

            if (!PermissionUtils.hasPermissions(this, StopTracking.PERMISSIONS_LOCATION)) {
                requestPermissions(StopTracking.PERMISSIONS_LOCATION, StopTracking.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            Intent intentL = new Intent(StopTracking.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentL);

            Intent intentA = new Intent(StopTracking.BROADCAST_PERMISSIONS_ALL_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentA);
        }
        Toast.makeText(this, "Stop tracking permissions have been granted", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StopTracking.PERMISSIONS_REQUEST_CODE) {
            requestPermissions();
        }
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
