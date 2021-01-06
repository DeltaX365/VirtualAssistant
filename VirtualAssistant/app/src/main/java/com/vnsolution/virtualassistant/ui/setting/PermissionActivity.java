package com.vnsolution.virtualassistant.ui.setting;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.db.dao.PhoneAppDao;
import com.vnsolution.virtualassistant.db.dao.PhoneAppUsageDao;
import com.vnsolution.virtualassistant.db.dao.PhoneCallDao;
import com.vnsolution.virtualassistant.db.dao.PhoneContactDao;
import com.vnsolution.virtualassistant.db.dao.PhoneSmsDao;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class PermissionActivity extends BaseActivity implements HasAndroidInjector, Switch.OnClickListener {
    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    private Switch physicalActivitySwitch;
    private Switch accessForegroundLocationSwitch;
    private Switch accessBackgroundLocationSwitch;

    private Switch readCallLogSwitch;
    private Switch readPhoneStateSwitch;
    private Switch readSmsSwitch;
    private Switch receiveSmsSwitch;
    private Switch readContactsSwitch;
    private Switch writeContactsSwitch;
    private Switch readSDCardSwitch;
    private Switch writeSDCardSwitch;

    private Switch packageUsageStatSwitch;
    private Switch appearOnTopSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_permission);
        setupActionBar();

        physicalActivitySwitch = findViewById(R.id.physical_activity);
        physicalActivitySwitch.setOnClickListener(this);
        accessForegroundLocationSwitch = findViewById(R.id.access_foreground_location);
        accessForegroundLocationSwitch.setOnClickListener(this);
        accessBackgroundLocationSwitch = findViewById(R.id.access_background_location);
        accessBackgroundLocationSwitch.setOnClickListener(this);

        readCallLogSwitch = findViewById(R.id.read_call_log);
        readCallLogSwitch.setOnClickListener(this);
        readPhoneStateSwitch = findViewById(R.id.read_phone_state);
        readPhoneStateSwitch.setOnClickListener(this);
        readSmsSwitch = findViewById(R.id.read_sms);
        readSmsSwitch.setOnClickListener(this);
        receiveSmsSwitch = findViewById(R.id.receive_sms);
        receiveSmsSwitch.setOnClickListener(this);
        readContactsSwitch = findViewById(R.id.read_contacts);
        readContactsSwitch.setOnClickListener(this);
        writeContactsSwitch = findViewById(R.id.write_contacts);
        writeContactsSwitch.setOnClickListener(this);
        readSDCardSwitch = findViewById(R.id.read_sd_card);
        readSDCardSwitch.setOnClickListener(this);
        writeSDCardSwitch = findViewById(R.id.write_sd_card);
        writeSDCardSwitch.setOnClickListener(this);

        packageUsageStatSwitch = findViewById(R.id.package_usage_stat);
        packageUsageStatSwitch.setOnClickListener(this);
        appearOnTopSwitch = findViewById(R.id.appear_on_top);
        appearOnTopSwitch.setOnClickListener(this);
    }

    private void refreshView() {
        physicalActivitySwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)? true : false);
        physicalActivitySwitch.setClickable(physicalActivitySwitch.isChecked()? false : true);
        accessForegroundLocationSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)? true : false);
        accessForegroundLocationSwitch.setClickable(accessForegroundLocationSwitch.isChecked()? false : true);
        accessBackgroundLocationSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)? true : false);
        accessBackgroundLocationSwitch.setClickable(accessBackgroundLocationSwitch.isChecked()? false : true);

        readCallLogSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_CALL_LOG)? true : false);
        readCallLogSwitch.setClickable(readCallLogSwitch.isChecked()? false : true);
        readPhoneStateSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_PHONE_STATE)? true : false);
        readPhoneStateSwitch.setClickable(readPhoneStateSwitch.isChecked()? false : true);
        readSmsSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_SMS)? true : false);
        readSmsSwitch.setClickable(readSmsSwitch.isChecked()? false : true);
        receiveSmsSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.RECEIVE_SMS)? true : false);
        receiveSmsSwitch.setClickable(receiveSmsSwitch.isChecked()? false : true);
        readContactsSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_CONTACTS)? true : false);
        readContactsSwitch.setClickable(readContactsSwitch.isChecked()? false : true);
        writeContactsSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.WRITE_CONTACTS)? true : false);
        writeContactsSwitch.setClickable(writeContactsSwitch.isChecked()? false : true);
        readSDCardSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)? true : false);
        readSDCardSwitch.setClickable(readSDCardSwitch.isChecked()? false : true);
        writeSDCardSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)? true : false);
        writeSDCardSwitch.setClickable(writeSDCardSwitch.isChecked()? false : true);

        packageUsageStatSwitch.setChecked(canAccessPackageUsageStat()? true : false);
        packageUsageStatSwitch.setClickable(packageUsageStatSwitch.isChecked()? false : true);
        appearOnTopSwitch.setChecked(canDrawOverlays()? true : false);
        appearOnTopSwitch.setClickable(appearOnTopSwitch.isChecked()? false : true);
    }

    @Override
    public void onClick(View v) {
        String[] permissions = null;
        switch (v.getId()) {
            case R.id.physical_activity:
                permissions = new String[] {Manifest.permission.ACTIVITY_RECOGNITION};
                break;
            case R.id.access_foreground_location:
                permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
                break;
            case R.id.access_background_location:
                permissions = new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                break;
            case R.id.read_call_log:
                permissions = new String[] {Manifest.permission.READ_CALL_LOG};
                break;
            case R.id.read_phone_state:
                permissions = new String[] {Manifest.permission.READ_PHONE_STATE};
                break;
            case R.id.read_sms:
                permissions = new String[] {Manifest.permission.READ_SMS};
                break;
            case R.id.receive_sms:
                permissions = new String[] {Manifest.permission.RECEIVE_SMS};
                break;
            case R.id.read_contacts:
                permissions = new String[] {Manifest.permission.READ_CONTACTS};
                break;
            case R.id.write_contacts:
                permissions = new String[] {Manifest.permission.WRITE_CONTACTS};
                break;
            case R.id.read_sd_card:
                permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
                break;
            case R.id.write_sd_card:
                permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                break;
            case R.id.package_usage_stat:
                permissions = new String[] {Manifest.permission.PACKAGE_USAGE_STATS};
                break;
            case R.id.appear_on_top:
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 9999);
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            requestPermissions(permissions,9999);
        }
    }

    private boolean canAccessPackageUsageStat() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, calendar.getTimeInMillis(), System.currentTimeMillis());
            return usageStats.isEmpty()? false : true;
        }
        return true;
    }

    private boolean canDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
