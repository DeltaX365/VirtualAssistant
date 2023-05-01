package vn.sttechnology.virtualassistant.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.common.BotSetting;
import vn.sttechnology.virtualassistant.common.DeviceSetting;
import vn.sttechnology.virtualassistant.common.StopTrackingSetting;
import vn.sttechnology.virtualassistant.common.UiSetting;
import vn.sttechnology.virtualassistant.ui.apps.AppListActivity;
import vn.sttechnology.virtualassistant.ui.common.BaseActivity;
import vn.sttechnology.virtualassistant.ui.common.RecyclerViewAdapter;
import vn.sttechnology.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import vn.sttechnology.virtualassistant.ui.contacts.ContactListActivity;
import vn.sttechnology.virtualassistant.ui.apps.AppDashboardActivity;
import vn.sttechnology.virtualassistant.ui.contacts.ContactDashboardActivity;
import vn.sttechnology.virtualassistant.ui.phonecall.v1.PhoneCallActivity;
import vn.sttechnology.virtualassistant.ui.phonesms.v1.PhoneSmsActivity;
import vn.sttechnology.virtualassistant.ui.reminders.ReminderListActivity;
import vn.sttechnology.virtualassistant.ui.settings.PermissionActivity;
import vn.sttechnology.virtualassistant.ui.settings.SettingActivity;
import vn.sttechnology.virtualassistant.ui.travel.TravelDashboardActivity;
import vn.sttechnology.virtualassistant.ui.travel.TravelTrackingActivity;
import vn.sttechnology.virtualassistant.util.PermissionUtils;

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
//        Glide.with(this).load(R.drawable.img_loading).into(botProcessingImageView);

        availableActionsRecyclerView = findViewById(R.id.list_available_actions);
        availableActionsRecyclerView.setHasFixedSize(true);

//        availableActionsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        availableActionsRecyclerView.setLayoutManager(availableActionsLayoutManager);
        availableActionsLayoutManager = new GridLayoutManager(this,2);
        availableActionsRecyclerView.setLayoutManager(availableActionsLayoutManager);

//        availableActionsAdapter = new RecyclerViewAdapter<String>(this, UiSetting.BOT_AVAILABLE_ACTIONS, R.layout.item_bot_action);
        availableActionsAdapter = new RecyclerViewAdapter<String>(this, UiSetting.BOT_AVAILABLE_ACTIONS, R.layout.card_bot_action);
        availableActionsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<String>() {
            @Override
            public void onTap(String action) {
                Intent intent = null;
                switch (action) {
                    case BotSetting.SHOW_TRAVEL_HISTORY:
                        intent = new Intent(HomeV2Activity.this, TravelDashboardActivity.class);
                        break;
                    case BotSetting.COVID_TRACKING:
                        intent = new Intent(HomeV2Activity.this, TravelTrackingActivity.class);
                        break;
                    case BotSetting.MANAGE_APPS:
                        intent = new Intent(HomeV2Activity.this, AppListActivity.class);
                        break;
                    case BotSetting.SHOW_APP_USAGE:
                        intent = new Intent(HomeV2Activity.this, AppDashboardActivity.class);
                        break;
                    case BotSetting.MANAGE_CONTACTS:
                        intent = new Intent(HomeV2Activity.this, ContactListActivity.class);
                        break;
                    case BotSetting.SHOW_LOCKED_CONTACTS:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                            intent = telecomManager.createManageBlockedNumbersIntent();
                        }
                        break;
                    case BotSetting.SHOW_CALL_STAT:
                        intent = new Intent(HomeV2Activity.this, ContactDashboardActivity.class);
                        break;
                    case BotSetting.SHOW_CALL_LOGS:
                        intent = new Intent(HomeV2Activity.this, PhoneCallActivity.class);
                        break;
                    case BotSetting.SHOW_SMS:
                        intent = new Intent(HomeV2Activity.this, PhoneSmsActivity.class);
                        break;
                    case BotSetting.SHOW_REMINDERS:
                        intent = new Intent(HomeV2Activity.this, ReminderListActivity.class);
                        break;
                    case BotSetting.MANAGE_PERMISSIONS:
                        intent = new Intent(HomeV2Activity.this, PermissionActivity.class);
                        break;
                    case BotSetting.SHOW_SETTINGS:
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
        availableActions.add(BotSetting.SHOW_TRAVEL_HISTORY);
        availableActions.add(BotSetting.COVID_TRACKING);
        availableActions.add(BotSetting.MANAGE_CONTACTS);
//        availableActions.add(BotSetting.MANAGE_LOCKED_CONTACTS);
        availableActions.add(BotSetting.SHOW_CALL_STAT);
        availableActions.add(BotSetting.MANAGE_APPS);
        availableActions.add(BotSetting.SHOW_APP_USAGE);
        availableActions.add(BotSetting.SHOW_REMINDERS);
//        availableActions.add(BotOptions.SHOW_NOTIFICATIONS);
//        availableActions.add(BotOptions.SHOW_CALL_LOGS);
//        availableActions.add(BotOptions.SHOW_SENT_RECEIVED_SMS);
//        availableActions.add(BotSetting.MANAGE_PERMISSIONS);
        availableActions.add(BotSetting.SHOW_SETTINGS);
        availableActionsAdapter.setData(availableActions);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Stop tracking permissions.
            // TODO: Review check for devices with Android 10 (29+).
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_POST_NOTIFICATION)) {
                requestPermissions(StopTrackingSetting.PERMISSIONS_POST_NOTIFICATION, StopTrackingSetting.PERMISSIONS_REQUEST_CODE);
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_ACTIVITY_RECOGNITION)) {
                requestPermissions(StopTrackingSetting.PERMISSIONS_ACTIVITY_RECOGNITION, StopTrackingSetting.PERMISSIONS_REQUEST_CODE);
                return;
            }
            Intent intentUA = new Intent(StopTrackingSetting.BROADCAST_PERMISSIONS_USER_ACTIVITY_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentUA);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !PermissionUtils.hasPermissions(this, StopTrackingSetting.PERMISSIONS_LOCATION)) {
                requestPermissions(StopTrackingSetting.PERMISSIONS_LOCATION, StopTrackingSetting.PERMISSIONS_REQUEST_CODE);
                return;
            }
            Intent intentL = new Intent(StopTrackingSetting.BROADCAST_PERMISSIONS_LOCATION_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentL);

            Intent intentA = new Intent(StopTrackingSetting.BROADCAST_PERMISSIONS_ALL_GRANTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentA);

            // Phone contact/call/sms permissions
            if (!PermissionUtils.hasPermissions(this, DeviceSetting.PERMISSIONS_PHONE_CALL)) {
                requestPermissions(DeviceSetting.PERMISSIONS_PHONE_CALL, DeviceSetting.PERMISSIONS_REQUEST_CODE);
                return;
            }
            if (!PermissionUtils.hasPermissions(this, DeviceSetting.PERMISSIONS_PHONE_SMS)) {
                requestPermissions(DeviceSetting.PERMISSIONS_PHONE_SMS, DeviceSetting.PERMISSIONS_REQUEST_CODE);
                return;
            }
            if (!PermissionUtils.hasPermissions(this, DeviceSetting.PERMISSIONS_PHONE_CONTACT)) {
                requestPermissions(DeviceSetting.PERMISSIONS_PHONE_CONTACT, DeviceSetting.PERMISSIONS_REQUEST_CODE);
                return;
            }

            // Full Screen permission
//            if (!PermissionUtils.hasPermissions(this, new String[] {Manifest.permission.USE_FULL_SCREEN_INTENT})) {
//                requestPermissions(new String[] {Manifest.permission.USE_FULL_SCREEN_INTENT}, DeviceOptions.PERMISSIONS_REQUEST_CODE);
//                return false;
//            }

            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, StopTrackingSetting.PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StopTrackingSetting.PERMISSIONS_REQUEST_CODE
                || requestCode == DeviceSetting.PERMISSIONS_REQUEST_CODE) {
            requestPermissions();
        }
    }
}
