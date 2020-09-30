package com.vnsolution.virtualassistant.ui.developer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTracking;
import com.vnsolution.virtualassistant.service.stop_tracking.StopTrackingService;
import com.vnsolution.virtualassistant.ui.phoneapps.v2.PhoneAppsV2Activity;
import com.vnsolution.virtualassistant.ui.phonecalls.v1.PhoneCallsActivity;
import com.vnsolution.virtualassistant.ui.phonecontacts.PhoneContactsActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelDashboardActivity;
import com.vnsolution.virtualassistant.ui.settings.SettingsActivity;
import com.vnsolution.virtualassistant.ui.phonesms.v1.PhoneSmsActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class DeveloperActivity extends AppCompatActivity implements HasAndroidInjector {
    private Button btnShowVisitedPlaces;
    private Button btnManualCheckIn;
    private Button btnShowInstalledApps;
    private Button btnShowCallLogs;
    private Button btnSms;
    private Button btnShowContacts;
    private Button btnShowSettings;

    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_developer);

        btnShowVisitedPlaces = findViewById(R.id.btn_visited_places);
        btnShowVisitedPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, TravelDashboardActivity.class);
                startActivity(intent);
            }
        });

        btnManualCheckIn = findViewById(R.id.btn_manual_check_in);
        btnManualCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(DeveloperActivity.this, StopTrackingService.class);
                serviceIntent.setAction(StopTracking.INTENT_ACTION_CHECK_OUT);
                ContextCompat.startForegroundService(DeveloperActivity.this, serviceIntent);
            }
        });

        btnShowInstalledApps = findViewById(R.id.btn_installed_applications);
        btnShowInstalledApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, PhoneAppsV2Activity.class);
                startActivity(intent);
            }
        });

        btnShowCallLogs = findViewById(R.id.btn_call_logs);
        btnShowCallLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, PhoneCallsActivity.class);
                startActivity(intent);
            }
        });

        btnSms = findViewById(R.id.btn_sms);
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, PhoneSmsActivity.class);
                startActivity(intent);
            }
        });

        btnShowContacts = findViewById(R.id.btn_contacts);
        btnShowContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, PhoneContactsActivity.class);
                startActivity(intent);
            }
        });

        btnShowSettings = findViewById(R.id.btn_settings);
        btnShowSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
