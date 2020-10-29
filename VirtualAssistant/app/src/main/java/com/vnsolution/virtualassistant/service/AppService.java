package com.vnsolution.virtualassistant.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.service.stop_tracking.StopTrackingService;
import com.vnsolution.virtualassistant.util.PermissionUtils;

public class AppService {
    private Context mContext;
    private BroadcastReceiver mBroadcastReceiver;

    public AppService(Context context) {
        mContext = context;
    }

    public void run() {
        // Stop tracking foreground service.
        if (PermissionUtils.hasPermissions(mContext, StopTrackingOptions.PERMISSIONS_STOP_TRACKING)) {
            Intent serviceIntent = new Intent(mContext, StopTrackingService.class);
            serviceIntent.setAction(StopTrackingOptions.INTENT_ACTION_INITIAL);
            ContextCompat.startForegroundService(mContext, serviceIntent);
        }
        startWatchingPermissions();
    }

    private void startWatchingPermissions() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    run();
                }
            };
        }
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
        IntentFilter filter = new IntentFilter();
        filter.addAction(StopTrackingOptions.BROADCAST_PERMISSIONS_ALL_GRANTED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, filter);
    }

    private void stopWatchingPermissions() {
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
        }
    }
}
