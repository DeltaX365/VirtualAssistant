package com.vnsolution.virtualassistant.ui.reminder;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.ReminderOptions;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ReminderShowFullScreenActivity extends BaseActivity implements HasAndroidInjector {
    private TextView reminderTitleTextView;
    private TextView reminderTimeTextView;
    private TextView reminderDateTextView;
    private MaterialButton executeReminderButton;
    private MaterialButton dismissReminderButton;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Inject
    public ReminderViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_reminder_show_full_screen);
        turnScreenOnThroughKeyguard();

        reminderTitleTextView = findViewById(R.id.reminder_title);
        String title = "";
        if (getIntent().hasExtra(ReminderOptions.NOTIFY_CONTENT_TEXT)) {
            title = getIntent().getStringExtra(ReminderOptions.NOTIFY_CONTENT_TEXT);
        }
        title = (title == null || title.isEmpty())? "Reminder" : title;
        reminderTitleTextView.setText(title);

        Date today = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        reminderTimeTextView = findViewById(R.id.reminder_time);
        reminderTimeTextView.setText(timeFormat.format(today));

        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM d");
        reminderDateTextView = findViewById(R.id.reminder_date);
        reminderDateTextView.setText(dateFormat.format(today));

        executeReminderButton = findViewById(R.id.execute_reminder);
        executeReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReminderShowFullScreenActivity.this, "Launch app", Toast.LENGTH_SHORT).show();
            }
        });

        dismissReminderButton = findViewById(R.id.dismiss_reminder);
        dismissReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(ReminderOptions.NOTIFY_ID);
                finish();
            }
        });

        playSoundAndVibrate();
    }

    @SuppressLint("MissingPermission")
    private void playSoundAndVibrate() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        try {
            mediaPlayer = MediaPlayer.create(this, alarmSound);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long vibratePattern[] = { 0, 800, 200, 1200, 300, 2000, 400, 4000 };
        try {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(vibratePattern, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    //    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        getWindow().addFlags(
//                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        getWindow().clearFlags(
//                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//    }
}
