package com.vnsolution.virtual_assistant.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.vnsolution.virtual_assistant.R;

public class NotificationUtils {
    private static final String NOTIFICATION_CHANNEL_ID = "com.vnsolution.virtualassistant.notification";

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    public static void sendNotification(Context context, String title, String message) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

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
        builder.setContentTitle(title)
                .setSmallIcon(R.drawable.ic_unknown)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setContentIntent(notificationPendingIntent)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setPriority(Notification.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify((int) (Math.random() * 999999), builder.build());
    }
}
