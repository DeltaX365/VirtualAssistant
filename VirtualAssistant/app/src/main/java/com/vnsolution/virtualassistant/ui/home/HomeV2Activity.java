package com.vnsolution.virtualassistant.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.BotOptions;
import com.vnsolution.virtualassistant.common.DeviceOptions;
import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.phoneapp.PhoneAppDashboardActivity;
import com.vnsolution.virtualassistant.ui.contact.ContactDashboardActivity;
import com.vnsolution.virtualassistant.ui.phonecall.v1.PhoneCallsActivity;
import com.vnsolution.virtualassistant.ui.phonesms.v1.PhoneSmsActivity;
import com.vnsolution.virtualassistant.ui.reminder.ReminderActivity;
import com.vnsolution.virtualassistant.ui.setting.SettingActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelDashboardActivity;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeV2Activity extends BaseActivity {
    private TextView botActionTextView;
//    private ImageView botProcessingImageView;
    private RecyclerView availableActionsRecyclerView;
    private RecyclerViewAdapter<String> availableActionsAdapter;
    private RecyclerView.LayoutManager availableActionsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        botActionTextView.setText("hi man, how can I help you?");
        availableActionsRecyclerView.setVisibility(View.VISIBLE);
//        botProcessingImageView.setVisibility(View.INVISIBLE);
    }

    private void setupView() {
        setContentView(R.layout.screen_home_v2);

        botActionTextView = findViewById(R.id.bot_action);
//        botProcessingImageView = findViewById(R.id.bot_processing);
//        Glide.with(this).load(R.drawable.loading).into(botProcessingImageView);

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
                    case BotOptions.SHOW_TRAVEL_HISTORY:
                        intent = new Intent(HomeV2Activity.this, TravelDashboardActivity.class);
                        break;
                    case BotOptions.MANAGE_YOUR_APPS:
                        intent = new Intent(HomeV2Activity.this, PhoneAppDashboardActivity.class);
                        break;
                    case BotOptions.MANAGE_PHONE_CONTACTS:
                        intent = new Intent(HomeV2Activity.this, ContactDashboardActivity.class);
                        break;
                    case BotOptions.SHOW_CALL_LOGS:
                        intent = new Intent(HomeV2Activity.this, PhoneCallsActivity.class);
                        break;
                    case BotOptions.SHOW_SENT_RECEIVED_SMS:
                        intent = new Intent(HomeV2Activity.this, PhoneSmsActivity.class);
                        break;
                    case BotOptions.SHOW_REMINDERS:
                        intent = new Intent(HomeV2Activity.this, ReminderActivity.class);
                        break;
                    case BotOptions.SHOW_SETTINGS:
                        intent = new Intent(HomeV2Activity.this, SettingActivity.class);
                        break;
                }
                if (intent != null) {
                    botActionTextView.setText(action + " ...");
                    availableActionsRecyclerView.setVisibility(View.INVISIBLE);
//                    botProcessingImageView.setVisibility(View.VISIBLE);
                    startActivity(intent);
                }
            }
        });
        availableActionsRecyclerView.setAdapter(availableActionsAdapter);
        requestPermissions();
    }

    private void refreshView() {
        List<String> availableActions = new ArrayList<String>();
        availableActions.add(BotOptions.SHOW_TRAVEL_HISTORY);
        availableActions.add(BotOptions.MANAGE_PHONE_CONTACTS);
        availableActions.add(BotOptions.MANAGE_YOUR_APPS);
        availableActions.add(BotOptions.SHOW_REMINDERS);
        availableActions.add(BotOptions.SHOW_NOTIFICATIONS);
//        availableActions.add(BotOptions.SHOW_CALL_LOGS);
//        availableActions.add(BotOptions.SHOW_SENT_RECEIVED_SMS);
        availableActions.add(BotOptions.SHOW_SETTINGS);
        availableActionsAdapter.setData(availableActions);
    }

    private boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Stop tracking permissions.
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

            // Phone contact/call/sms permissions
            if (!PermissionUtils.hasPermissions(this, DeviceOptions.PERMISSIONS_PHONE_CALL)) {
                requestPermissions(DeviceOptions.PERMISSIONS_PHONE_CALL, DeviceOptions.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (!PermissionUtils.hasPermissions(this, DeviceOptions.PERMISSIONS_PHONE_SMS)) {
                requestPermissions(DeviceOptions.PERMISSIONS_PHONE_SMS, DeviceOptions.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (!PermissionUtils.hasPermissions(this, DeviceOptions.PERMISSIONS_PHONE_CONTACT)) {
                requestPermissions(DeviceOptions.PERMISSIONS_PHONE_CONTACT, DeviceOptions.PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StopTrackingOptions.PERMISSIONS_REQUEST_CODE
                || requestCode == DeviceOptions.PERMISSIONS_REQUEST_CODE) {
            requestPermissions();
        }
    }
}
