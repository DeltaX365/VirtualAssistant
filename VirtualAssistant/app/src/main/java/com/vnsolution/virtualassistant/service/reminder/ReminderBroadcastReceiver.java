package com.vnsolution.virtualassistant.service.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.ReminderOptions;
import com.vnsolution.virtualassistant.ui.reminder.ReminderListActivity;
import com.vnsolution.virtualassistant.ui.reminder.ReminderShowFullScreenActivity;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Android full screen notification shows over the lock screen
        // Link: https://developer.android.com/training/notify-user/time-sensitive
        createFullScreenNotification(context, intent);
    }

    private void createFullScreenNotification(Context context, Intent intent) {
        // The notification chanel should have a HIGH IMPORTANT. Otherwise, it doesn't work.
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (soundUri == null) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (soundUri == null) {
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        long[] vibratePattern = { 500, 500, 500, 500, 500, 500, 500, 500, 500 };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ReminderOptions.NOTIFY_CHANEL_ID, ReminderOptions.NOTIFY_CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.GREEN);
            channel.enableLights(true);
            channel.setVibrationPattern(vibratePattern);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        String reminderContent = "";
        if (intent != null && intent.getExtras() != null) {
            reminderContent = intent.getExtras().getString("ReminderContent", "");
        }
        PendingIntent dismissPendingIntent = ReminderShowFullScreenActivity.getDismissIntent(ReminderOptions.NOTIFY_ID, context);
        Intent fullScreenIntent = new Intent(context, ReminderShowFullScreenActivity.class);
//        fullScreenIntent.setFlags(
//                Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        | Intent.FLAG_ACTIVITY_NO_USER_ACTION
//                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
//        );
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ReminderOptions.NOTIFY_CHANEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_hearing_24)
                .setContentTitle(ReminderOptions.NOTIFY_TITLE)
                .setContentText(reminderContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setVibrate(vibratePattern)
                .addAction(R.drawable.ic_baseline_close_24, "DISMISS", dismissPendingIntent)
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
        notificationManager.notify(ReminderOptions.NOTIFY_ID, fullScreenNotification);
//        NotificationManagerCompat.from(context).notify(9999, fullScreenNotification);
    }
}
