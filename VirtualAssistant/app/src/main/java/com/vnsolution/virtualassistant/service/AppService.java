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

import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.service.bubble.FloatingViewBroadcastReceiver;
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
        startWatchingPermissions();
    }

    private void startBackgroundServices() {
        startStopTrackingService();
        startDeviceSyncWorker();
    }

    private void startStopTrackingService() {
        if (PermissionUtils.hasPermissions(mContext, StopTrackingOptions.PERMISSIONS_STOP_TRACKING)) {
            Intent serviceIntent = new Intent(mContext, StopTrackingService.class);
            serviceIntent.setAction(StopTrackingOptions.INTENT_ACTION_INITIAL);
            ContextCompat.startForegroundService(mContext, serviceIntent);
        }
        final PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(StopTrackingWorker.class, 24, TimeUnit.HOURS).build();
        WorkManager.getInstance(mContext).enqueueUniquePeriodicWork("StopTrackingWorker", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    private void startDeviceSyncWorker() {
        final PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(DeviceWorker.class, 3, TimeUnit.HOURS).build();
        WorkManager.getInstance(mContext).enqueueUniquePeriodicWork("DeviceSyncWorker", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
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
        filter.addAction(StopTrackingOptions.BROADCAST_PERMISSIONS_ALL_GRANTED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mPermissionChangedBroadcastReceiver, filter);
    }

    private void stopWatchingPermissions() {
        if (mPermissionChangedBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mPermissionChangedBroadcastReceiver);
        }
    }
}
