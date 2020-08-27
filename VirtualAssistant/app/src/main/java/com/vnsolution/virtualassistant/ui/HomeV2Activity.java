package com.vnsolution.virtualassistant.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTracking;
import com.vnsolution.virtualassistant.ui.developer.DeveloperActivity;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class HomeV2Activity extends AppCompatActivity implements HasAndroidInjector {
    private ImageView ivVoiceAssistant;
    private Button btnShowDeveloperPage;

    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_home_v2);

        ivVoiceAssistant = findViewById(R.id.iv_voice_assistant);
        Glide.with(this).load(R.drawable.voice_assistant_motion_2).into(ivVoiceAssistant);

        btnShowDeveloperPage = findViewById(R.id.btn_developer);
        btnShowDeveloperPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeV2Activity.this, DeveloperActivity.class);
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
