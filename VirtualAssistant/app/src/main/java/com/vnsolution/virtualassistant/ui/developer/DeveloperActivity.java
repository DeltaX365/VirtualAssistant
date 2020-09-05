package com.vnsolution.virtualassistant.ui.developer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.applications.v1.InstalledApplicationsActivity;
import com.vnsolution.virtualassistant.ui.applications.v2.InstalledApplicationsV2Activity;
import com.vnsolution.virtualassistant.ui.calllogs.v1.CallLogsActivity;
import com.vnsolution.virtualassistant.ui.contacts.ContactsActivity;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2Activity;
import com.vnsolution.virtualassistant.ui.settings.SettingsActivity;
import com.vnsolution.virtualassistant.ui.sms.v1.SmsActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class DeveloperActivity extends AppCompatActivity implements HasAndroidInjector {
    private Button btnShowVisitedPlaces;
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
                Intent intent = new Intent(DeveloperActivity.this, VisitedPlacesV2Activity.class);
                startActivity(intent);
            }
        });

        btnShowInstalledApps = findViewById(R.id.btn_installed_applications);
        btnShowInstalledApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, InstalledApplicationsV2Activity.class);
                startActivity(intent);
            }
        });

        btnShowCallLogs = findViewById(R.id.btn_call_logs);
        btnShowCallLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, CallLogsActivity.class);
                startActivity(intent);
            }
        });

        btnSms = findViewById(R.id.btn_sms);
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, SmsActivity.class);
                startActivity(intent);
            }
        });

        btnShowContacts = findViewById(R.id.btn_contacts);
        btnShowContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeveloperActivity.this, ContactsActivity.class);
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
