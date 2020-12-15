package com.vnsolution.virtualassistant.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.home.HomeV2Activity;

public class FloatingViewService extends Service implements View.OnTouchListener {
    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams layoutParams;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

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
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        layoutParams.x = 0;
        layoutParams.y = displayMetrics.heightPixels / 2;

        // Add the view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, layoutParams);

        // Drag and move floating view using user's touch action.
        floatingView.findViewById(R.id.hearing_icon).setOnTouchListener(this);

        // Start animation
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        floatingView.findViewById(R.id.hearing_icon).startAnimation(shake);

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
                }
                return true;
        }
        return false;
    }

    public void launchApp() {
        // Open the application  click.
        Intent intent = new Intent(this, HomeV2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Close the service and remove view from the view hierarchy
        stopSelf();
    }
}
