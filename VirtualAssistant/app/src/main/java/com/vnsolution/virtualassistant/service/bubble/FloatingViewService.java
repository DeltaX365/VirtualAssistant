package com.vnsolution.virtualassistant.service.bubble;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.vnsolution.virtualassistant.BuildConfig;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.service.device.CheckAppLaunchThread;
import com.vnsolution.virtualassistant.ui.StartupActivity;

public class FloatingViewService extends Service implements View.OnTouchListener {
    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams layoutParams;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    private CheckAppLaunchThread launchChecker;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create notification channel
        String chanelId = "com.vnsolution.virtualassistant.BUBBLE_WIDGET_FOREGROUND_SERVICE";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(chanelId, "Bubble Widget", NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, chanelId)
                .setContentTitle("Bubble Widget")
                .setContentText("Prevent your bubble widget from destroying by System")
                .setSmallIcon(R.drawable.ic_baseline_hearing_24)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .build();
        startForeground((int) (Math.random() * 999999), notification);

        // Check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                stopSelf();
                return START_NOT_STICKY;
            }
        }

        // Request to stop foreground service
        if (intent != null) {
            try {
                if (intent.getAction().equals("StopFloatingService")) {
                    stopSelf();
                    return START_NOT_STICKY;
                }
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }

        // Inflate the floating view layout we created
        floatingView = LayoutInflater.from(this).inflate(R.layout.screen_floating_widget,null);

        // Add the view to the window.
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Specify the view position
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        ViewPosition floatingViewPosition = getViewPositionFromCache();
        if (floatingViewPosition != null) {
            layoutParams.x = floatingViewPosition.x;
            layoutParams.y = floatingViewPosition.y;
        } else {
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            layoutParams.x = 0;
            layoutParams.y = displayMetrics.heightPixels / 2;
        }

        // Add the view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, layoutParams);

        // Drag and move floating view using user's touch action.
        floatingView.findViewById(R.id.hearing_icon).setOnTouchListener(this);

        // Start animation
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        floatingView.findViewById(R.id.hearing_icon).startAnimation(shake);
        enableOpacity();

        // Watching the running app
//        Handler handler = new Handler(getMainLooper());
//        launchChecker = new CheckAppLaunchThread(handler,getApplicationContext());
//        watchingRunningApp();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Remember the initial position.
                initialX = layoutParams.x;
                initialY = layoutParams.y;

                // Get the touch location
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                // Calculate the X and Y coordinates of the view.
                layoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                // Update the layout with new X & Y coordinate
                windowManager.updateViewLayout(floatingView, layoutParams);
                return true;
            case MotionEvent.ACTION_UP:
                // The check for Xdiff < 10 && YDiff < 10 because sometime elements moves a little while clicking. So that is click event.
                int Xdiff = (int) (event.getRawX() - initialTouchX);
                int Ydiff = (int) (event.getRawY() - initialTouchY);
                if (Math.abs(Xdiff) < 10 && Math.abs(Ydiff) < 10) {
                    launchApp();
                } else {
                    DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
                    if (event.getRawY() < 200) {
                        layoutParams.y = 0;
                    } else if (event.getRawY() >= displayMetrics.heightPixels - 200) {
                        layoutParams.y = displayMetrics.heightPixels;
                    } else if (event.getRawX() < displayMetrics.widthPixels / 2) {
                        layoutParams.x = 0;
                    } else if (event.getRawX() >= displayMetrics.widthPixels / 2) {
                        layoutParams.x = displayMetrics.widthPixels;
                    }
                    windowManager.updateViewLayout(floatingView, layoutParams);
                    saveViewPositionToCache(new ViewPosition(layoutParams.x, layoutParams.y));
                    enableOpacity();
                }
                return true;
        }
        return false;
    }

    private void enableOpacity() {
        floatingView.findViewById(R.id.hearing_icon).setAlpha(1.0f);
        floatingView.setAlpha(1.0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                floatingView.findViewById(R.id.hearing_icon).setAlpha(0.6f);
                floatingView.setAlpha(0.6f);
            }
        },3 * 1000);
    }

    private void launchApp() {
        // Open the application  click.
        Intent intent = new Intent(this, StartupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Close the service and remove view from the view hierarchy
        stopSelf();
    }

    private void saveViewPositionToCache(ViewPosition viewPosition) {
        SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String viewPositionString = gson.toJson(viewPosition);
        editor.putString("FloatingViewService.ViewPosition", viewPositionString);
        editor.commit();
    }

    private ViewPosition getViewPositionFromCache() {
        SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        String viewPositionString = sharedPref.getString("FloatingViewService.ViewPosition", "");
        Gson gson = new Gson();
        return !viewPositionString.isEmpty()? gson.fromJson(viewPositionString, ViewPosition.class) : null;
    }

    private void watchingRunningApp() {
        while (true) {
            if (!launchChecker.isAlive()) {
                launchChecker.start();
            }
        }
    }

    private class ViewPosition {
        public int x;
        public int y;
        public ViewPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
