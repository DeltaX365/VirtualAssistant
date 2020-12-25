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
import com.vnsolution.virtualassistant.ui.reminder.ReminderShowFullScreenActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Android full screen notification shows over the lock screen
        // Link: https://developer.android.com/training/notify-user/time-sensitive
        String action = intent.getAction();
        switch (action) {
            case ReminderOptions.NOTIFY_ACTION_SHOW_FULL_SCREEN:
                createFullScreenNotification(context, intent);
                break;
            case ReminderOptions.NOTIFY_ACTION_DISMISS:
                dismissNotification(context);
                break;
        }
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
        if (intent != null && intent.hasExtra(ReminderOptions.NOTIFY_CONTENT_TEXT)) {
            reminderContent = intent.getStringExtra(ReminderOptions.NOTIFY_CONTENT_TEXT);
        }

        Intent fullScreenIntent = new Intent(context, ReminderShowFullScreenActivity.class);
        fullScreenIntent.putExtra(ReminderOptions.NOTIFY_CONTENT_TEXT, reminderContent);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context,0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ReminderOptions.NOTIFY_CHANEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_hearing_24)
                .setContentTitle(ReminderOptions.NOTIFY_TITLE)
                .setContentText(reminderContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setVibrate(vibratePattern)
//                .setCategory(NotificationCompat.CATEGORY_CALL)

                // Use a full-screen intent only for the highest-priority alerts where you
                // have an associated activity that you would like to launch after the user
                // interacts with the notification. Also, if your app targets Android 10
                // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                // order for the platform to invoke this notification.
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .addAction(R.drawable.ic_baseline_close_24, "DISMISS", getDismissPendingIntent(context));

        Notification fullScreenNotification = notificationBuilder.build();
        NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(ReminderOptions.NOTIFY_ID, fullScreenNotification);
//        NotificationManagerCompat.from(context).notify(9999, fullScreenNotification);
    }

    private PendingIntent getDismissPendingIntent(Context context) {
        Intent dismissIntent = new Intent(context, ReminderBroadcastReceiver.class);
        dismissIntent.setAction(ReminderOptions.NOTIFY_ACTION_DISMISS);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context,0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return dismissPendingIntent;
    }

    private void dismissNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(ReminderOptions.NOTIFY_ID);
    }
}
