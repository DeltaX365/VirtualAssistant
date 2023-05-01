package vn.sttechnology.virtualassistant.ui.settings;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.ui.common.BaseActivity;
import vn.sttechnology.virtualassistant.util.PermissionUtils;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class PermissionActivity extends BaseActivity implements HasAndroidInjector, Switch.OnClickListener {
    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    private Switch postNotificationSwitch;
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

        postNotificationSwitch = findViewById(R.id.post_notification);
        postNotificationSwitch.setOnClickListener(this);
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
        postNotificationSwitch.setChecked(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || PermissionUtils.hasPermission(this, Manifest.permission.POST_NOTIFICATIONS));
        postNotificationSwitch.setClickable(!physicalActivitySwitch.isChecked());
        physicalActivitySwitch.setChecked(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || PermissionUtils.hasPermission(this, Manifest.permission.ACTIVITY_RECOGNITION));
        physicalActivitySwitch.setClickable(!physicalActivitySwitch.isChecked());
        accessForegroundLocationSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        accessForegroundLocationSwitch.setClickable(!accessForegroundLocationSwitch.isChecked());
        accessBackgroundLocationSwitch.setChecked(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION));
        accessBackgroundLocationSwitch.setClickable(!accessBackgroundLocationSwitch.isChecked());

        readCallLogSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_CALL_LOG));
        readCallLogSwitch.setClickable(!readCallLogSwitch.isChecked());
        readPhoneStateSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_PHONE_STATE));
        readPhoneStateSwitch.setClickable(!readPhoneStateSwitch.isChecked());
        readSmsSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_SMS));
        readSmsSwitch.setClickable(!readSmsSwitch.isChecked());
        receiveSmsSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.RECEIVE_SMS));
        receiveSmsSwitch.setClickable(!receiveSmsSwitch.isChecked());
        readContactsSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_CONTACTS));
        readContactsSwitch.setClickable(!readContactsSwitch.isChecked());
        writeContactsSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.WRITE_CONTACTS));
        writeContactsSwitch.setClickable(!writeContactsSwitch.isChecked());
        readSDCardSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE));
        readSDCardSwitch.setClickable(!readSDCardSwitch.isChecked());
        writeSDCardSwitch.setChecked(PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        writeSDCardSwitch.setClickable(!writeSDCardSwitch.isChecked());

        boolean packageUsageStatSwitchEnabled = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            packageUsageStatSwitchEnabled = canAccessPackageUsageStat();
        }
        packageUsageStatSwitch.setChecked(packageUsageStatSwitchEnabled);
        packageUsageStatSwitch.setClickable(!packageUsageStatSwitch.isChecked());
        appearOnTopSwitch.setChecked(canDrawOverlays());
        appearOnTopSwitch.setClickable(!appearOnTopSwitch.isChecked());
    }

    @Override
    public void onClick(View v) {
        String[] permissions = null;
        switch (v.getId()) {
            case R.id.post_notification:
                permissions = new String[] {Manifest.permission.POST_NOTIFICATIONS};
                break;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 9999 && resultCode == RESULT_OK) {
            refreshView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private boolean canAccessPackageUsageStat() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, calendar.getTimeInMillis(), System.currentTimeMillis());
        return !usageStats.isEmpty();
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
