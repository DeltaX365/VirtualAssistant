package com.vnsolution.virtualassistant.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.BotOptions;
import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceSummary;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.phoneapps.v2.PhoneAppsV2Activity;
import com.vnsolution.virtualassistant.ui.phonecalls.v1.PhoneCallsActivity;
import com.vnsolution.virtualassistant.ui.phonecontacts.PhoneContactsActivity;
import com.vnsolution.virtualassistant.ui.phonesms.v1.PhoneSmsActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelDashboardActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelHistoryActivity;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeV2Activity extends BaseActivity {
    private RecyclerView availableActionsRecyclerView;
    private RecyclerViewAdapter<String> availableActionsAdapter;
    private RecyclerView.LayoutManager availableActionsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_home_v2);

        availableActionsRecyclerView = findViewById(R.id.list_available_actions);
        availableActionsRecyclerView.setHasFixedSize(true);

        availableActionsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        availableActionsRecyclerView.setLayoutManager(availableActionsLayoutManager);

        availableActionsAdapter = new RecyclerViewAdapter<String>(this, UiOptions.BOT_AVAILABLE_ACTIONS, R.layout.item_bot_action);
        availableActionsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<String>() {
            @Override
            public void onTap(String action) {
                Intent intent = null;
                switch (action) {
                    case BotOptions.SHOW_VISITED_PLACES:
                        intent = new Intent(HomeV2Activity.this, TravelDashboardActivity.class);
                        break;
                    case BotOptions.SHOW_INSTALLED_APPS:
                        intent = new Intent(HomeV2Activity.this, PhoneAppsV2Activity.class);
                        break;
                    case BotOptions.SHOW_PHONE_CONTACTS:
                        intent = new Intent(HomeV2Activity.this, PhoneContactsActivity.class);
                        break;
                    case BotOptions.SHOW_CALL_LOGS:
                        intent = new Intent(HomeV2Activity.this, PhoneCallsActivity.class);
                        break;
                    case BotOptions.SHOW_SENT_RECEIVED_SMS:
                        intent = new Intent(HomeV2Activity.this, PhoneSmsActivity.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        availableActionsRecyclerView.setAdapter(availableActionsAdapter);
        requestPermissions();
    }

    private void refreshView() {
        List<String> availableActions = new ArrayList<String>();
        availableActions.add(BotOptions.SHOW_VISITED_PLACES);
        availableActions.add(BotOptions.SHOW_INSTALLED_APPS);
        availableActions.add(BotOptions.SHOW_PHONE_CONTACTS);
        availableActions.add(BotOptions.SHOW_CALL_LOGS);
        availableActions.add(BotOptions.SHOW_SENT_RECEIVED_SMS);
        availableActionsAdapter.setData(availableActions);
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
