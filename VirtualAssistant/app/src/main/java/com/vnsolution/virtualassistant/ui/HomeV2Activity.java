package com.vnsolution.virtualassistant.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.developer.DeveloperActivity;
import com.vnsolution.virtualassistant.util.PermissionUtils;

public class HomeV2Activity extends BaseActivity {
    private ImageView ivVoiceAssistant;
    private Button btnShowDeveloperPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_home_v2);
        setupActionBar();

//        ivVoiceAssistant = findViewById(R.id.iv_voice_assistant);
//        Glide.with(this).load(R.drawable.voice_assistant_motion_2).into(ivVoiceAssistant);

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
            if (!PermissionUtils.hasPermissions(this, StopTrackingOptions.PERMISSIONS_ACTIVITY_RECOGNITION)) {
                requestPermissions(StopTrackingOptions.PERMISSIONS_ACTIVITY_RECOGNITION, StopTrackingOptions.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            Intent intentUA = new Intent(StopTrackingOptions.BROADCAST_PERMISSIONS_USER_ACTIVITY_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentUA);

            if (!PermissionUtils.hasPermissions(this, StopTrackingOptions.PERMISSIONS_LOCATION)) {
                requestPermissions(StopTrackingOptions.PERMISSIONS_LOCATION, StopTrackingOptions.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            Intent intentL = new Intent(StopTrackingOptions.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentL);

            Intent intentA = new Intent(StopTrackingOptions.BROADCAST_PERMISSIONS_ALL_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentA);
        }
        Toast.makeText(this, "Stop tracking permissions have been granted", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StopTrackingOptions.PERMISSIONS_REQUEST_CODE) {
            requestPermissions();
        }
    }
}
