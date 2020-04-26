package com.vnsolution.virtualassistant.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.service.activities.ActivityTrackingService;
import com.vnsolution.virtualassistant.service.geolocation.GeolocationTrackingService;

public class AppService {
    private static Context mContext;
    private static BroadcastReceiver mBroadcastReceiver;

    public static void init(Context context) {
        mContext = context;
        if(!startServices()) {
            startObservingPermissionChanged();
        }
    }

    private static boolean startServices() {
//        return
//            startActivitiesObserverService() &&
//            startGeolocationObserverService();
        return startActivitiesObserverService();
    }

    private static void startObservingPermissionChanged() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handlePermissionChanged(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_ACTIVITY_RECOGNITION_PERMISSION_CHANGED);
        filter.addAction(Constants.BROADCAST_LOCATION_PERMISSION_CHANGED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, filter);
    }

    private static void stopObservingPermissionChanged() {
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
        }
    }

    private static void handlePermissionChanged(Intent intent) {
        switch (intent.getAction()) {
            case Constants.BROADCAST_ACTIVITY_RECOGNITION_PERMISSION_CHANGED:
                startActivitiesObserverService();
                break;
            case Constants.BROADCAST_LOCATION_PERMISSION_CHANGED:
                startGeolocationObserverService();
                break;
        }
    }

    private static boolean startActivitiesObserverService() {
        if(hasPermissions(ActivityTrackingService.REQUEST_PERMISSIONS) == true) {
            Intent intent = new Intent(mContext, ActivityTrackingService.class);
            mContext.startService(intent);
            return true;
        }
        return false;
    }

    private static boolean startGeolocationObserverService() {
        if(hasPermissions(GeolocationTrackingService.REQUEST_PERMISSIONS) == true) {
            Intent intent = new Intent(mContext, GeolocationTrackingService.class);
            mContext.startService(intent);
            return true;
        }
        return false;
    }

    private static boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission) == false)
                return false;
        }
        return true;
    }

    private static boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
