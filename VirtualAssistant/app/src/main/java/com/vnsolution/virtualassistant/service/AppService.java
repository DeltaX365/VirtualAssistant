package com.vnsolution.virtualassistant.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.vnsolution.virtualassistant.common.StopTrackingSetting;
import com.vnsolution.virtualassistant.service.device.AppLockService;
import com.vnsolution.virtualassistant.service.device.DeviceWorker;
import com.vnsolution.virtualassistant.service.stoptracking.StopTrackingService;
import com.vnsolution.virtualassistant.service.stoptracking.StopTrackingWorker;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.concurrent.TimeUnit;

public class AppService {
    private Context mContext;
    private BroadcastReceiver mPermissionChangedBroadcastReceiver;

    public AppService(Context context) {
        mContext = context;
    }

    public void run() {
        startBackgroundServices();
        startWorkers();
        startWatchingPermissions();
    }

    private void startBackgroundServices() {
        // Running stop tracking service for auto check-in.
        Intent stopTrackingServiceIntent = new Intent(mContext, StopTrackingService.class);
        stopTrackingServiceIntent.setAction(StopTrackingSetting.INTENT_ACTION_INITIAL);
        ContextCompat.startForegroundService(mContext, stopTrackingServiceIntent);

        // Running app lock service for protecting locked apps.
        Intent appLockServiceIntent = new Intent(mContext, AppLockService.class);
        ContextCompat.startForegroundService(mContext, appLockServiceIntent);
    }

    private void startWorkers() {
        // Syncing stop points to match Google places.
        final PeriodicWorkRequest placeSyncRequest = new PeriodicWorkRequest.Builder(StopTrackingWorker.class, 24, TimeUnit.HOURS).build();
        WorkManager.getInstance(mContext).enqueueUniquePeriodicWork("StopTrackingWorker", ExistingPeriodicWorkPolicy.REPLACE, placeSyncRequest);

        // Syncing everything related to device as follows: apps, contacts, calls & sms.
        final PeriodicWorkRequest deviceSyncRequest = new PeriodicWorkRequest.Builder(DeviceWorker.class, 24, TimeUnit.HOURS).build();
        WorkManager.getInstance(mContext).enqueueUniquePeriodicWork("DeviceSyncWorker", ExistingPeriodicWorkPolicy.REPLACE, deviceSyncRequest);
    }

    private void startWatchingPermissions() {
        if (mPermissionChangedBroadcastReceiver == null) {
            mPermissionChangedBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    run();
                }
            };
        }
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mPermissionChangedBroadcastReceiver);
        IntentFilter filter = new IntentFilter();
        filter.addAction(StopTrackingSetting.BROADCAST_PERMISSIONS_ALL_GRANTED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mPermissionChangedBroadcastReceiver, filter);
    }

    private void stopWatchingPermissions() {
        if (mPermissionChangedBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mPermissionChangedBroadcastReceiver);
        }
    }
}
