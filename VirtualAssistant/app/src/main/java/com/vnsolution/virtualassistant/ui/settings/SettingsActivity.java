package com.vnsolution.virtualassistant.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class SettingsActivity extends BaseActivity implements HasAndroidInjector {
    private Context mContext;
    private String[] settings = { "Grant stop tracking permissions", "Reset factory" };

    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public PlaceHistoryDao placeHistoryDao;

    @Inject
    public PlaceDao placeDao;

    @Inject
    public PhoneContactDao phoneContactDao;

    @Inject
    public PhoneCallDao phoneCallDao;

    @Inject
    public PhoneSmsDao phoneSmsDao;

    @Inject
    public PhoneAppDao phoneAppDao;

    @Inject
    public PhoneAppUsageDao phoneAppUsageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_settings);
        setupActionBar();

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.item_setting, settings);
        ListView listSettings = (ListView) findViewById(R.id.list_settings);
        listSettings.setAdapter(adapter);
        listSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        requestPermissions();
                        break;
                    case 1:
                        runResetFactory();
                        Toast.makeText(SettingsActivity.this, "Data is reset successfully.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
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

    private void runResetFactory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                placeHistoryDao.reset();
                placeDao.reset();
                phoneCallDao.reset();
                phoneSmsDao.reset();
                phoneContactDao.reset();
                phoneAppUsageDao.reset();
                phoneAppDao.reset();
            }
        }).start();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
