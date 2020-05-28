package com.vnsolution.virtualassistant.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.service.stoptracking.StopTrackingService;

import java.util.Random;

public class AppService {
    private static Context mContext;
    private static BroadcastReceiver mBroadcastReceiver;
    private static final String CHANNEL_ID = "com.vnsolution.virtualassistant.notification";

    public static void init(Context context) {
        mContext = context;
        if(!startServices()) {
            startObservingPermissionChanged();
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    public static void sendNotification(Context context, String message) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

//        // Create an explicit content Intent that starts the main Activity.
//        Intent notificationIntent = new Intent(getApplicationContext(), DashboardActivity.class);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Add the main Activity to the task stack as the parent.
//        stackBuilder.addParentStack(DashboardActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setContentTitle("Event triggered")
                .setSmallIcon(R.drawable.ic_unknown)
                .setContentText(message)
//                .setContentIntent(notificationPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify((int) (Math.random() * 999999), builder.build());
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
        if (hasPermissions(StopTrackingService.REQUEST_PERMISSIONS)) {
            startServices();
        }
    }

    private static boolean startServices() {
        Intent serviceIntent = new Intent(mContext, StopTrackingService.class);
        serviceIntent.setAction(StopTrackingService.INTENT_ACTION_INITIAL);
        ContextCompat.startForegroundService(mContext, serviceIntent);
        return true;
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
