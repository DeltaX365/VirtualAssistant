package com.vnsolution.virtualassistant.service.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.reminder.ReminderListActivity;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Android full screen notification shows over the lock screen
        // Link: https://developer.android.com/training/notify-user/time-sensitive
        createFullScreenNotification(context);
    }

    private void createFullScreenNotification(Context context) {
        Intent fullScreenIntent = new Intent(context, ReminderListActivity.class);
//        fullScreenIntent.setFlags(
//                Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        | Intent.FLAG_ACTIVITY_NO_USER_ACTION
//                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
//        );
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent , PendingIntent.FLAG_UPDATE_CURRENT);

        // The notification chanel should have a HIGH IMPORTANT. Otherwise, it doesn't work.
        String chanelId = "com.vnsolution.virtualassistant.REMINDER_FOREGROUND_SERVICE";
        String chanelName = "Schedule Reminder";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(chanelId, chanelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, chanelId)
                .setSmallIcon(R.drawable.ic_unknown)
                .setContentTitle("Full Screen Alarm Test")
                .setContentText("This is a test")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_CALL)

                // Use a full-screen intent only for the highest-priority alerts where you
                // have an associated activity that you would like to launch after the user
                // interacts with the notification. Also, if your app targets Android 10
                // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                // order for the platform to invoke this notification.
                .setFullScreenIntent(fullScreenPendingIntent, true);

        Notification fullScreenNotification = notificationBuilder.build();
        NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(999, fullScreenNotification);
//        NotificationManagerCompat.from(context).notify(9999, fullScreenNotification);
    }
}
