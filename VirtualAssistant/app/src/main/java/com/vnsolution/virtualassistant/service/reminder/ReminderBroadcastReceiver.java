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
import com.vnsolution.virtualassistant.common.ReminderSetting;
import com.vnsolution.virtualassistant.ui.reminders.ReminderShowFullScreenActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Android full screen notification shows over the lock screen
        // Link: https://developer.android.com/training/notify-user/time-sensitive
        String action = intent.getAction();
        switch (action) {
            case ReminderSetting.NOTIFY_ACTION_SHOW_FULL_SCREEN:
                createFullScreenNotification(context, intent);
                break;
            case ReminderSetting.NOTIFY_ACTION_DISMISS:
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
            NotificationChannel channel = new NotificationChannel(ReminderSetting.NOTIFY_CHANEL_ID, ReminderSetting.NOTIFY_CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH);
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
        if (intent != null && intent.hasExtra(ReminderSetting.NOTIFY_CONTENT_TEXT)) {
            reminderContent = intent.getStringExtra(ReminderSetting.NOTIFY_CONTENT_TEXT);
        }

        Intent fullScreenIntent = new Intent(context, ReminderShowFullScreenActivity.class);
        fullScreenIntent.putExtra(ReminderSetting.NOTIFY_CONTENT_TEXT, reminderContent);
        int pendingIntentFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context,0, fullScreenIntent, pendingIntentFlag);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ReminderSetting.NOTIFY_CHANEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_hearing_24)
                .setContentTitle(ReminderSetting.NOTIFY_TITLE)
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
        notificationManager.notify(ReminderSetting.NOTIFY_ID, fullScreenNotification);
//        NotificationManagerCompat.from(context).notify(9999, fullScreenNotification);
    }

    private PendingIntent getDismissPendingIntent(Context context) {
        Intent dismissIntent = new Intent(context, ReminderBroadcastReceiver.class);
        dismissIntent.setAction(ReminderSetting.NOTIFY_ACTION_DISMISS);
        int pendingIntentFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context,0, dismissIntent, pendingIntentFlag);
        return dismissPendingIntent;
    }

    private void dismissNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(ReminderSetting.NOTIFY_ID);
    }
}
