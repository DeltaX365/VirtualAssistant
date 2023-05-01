package vn.sttechnology.virtualassistant.ui.common;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.ui.home.HomeV2Activity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class BaseActivity extends AppCompatActivity implements HasAndroidInjector {
    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    protected Toolbar toolbar;
    protected ProgressBar progressbar;
    protected View container;

    private static boolean isProcessing = false;
    private int processingTimeOut = 3 * 1000; //3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupStatusBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopProcessing();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.color_white));
        }
    }

    protected void setupActionBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            TextView toolbarTitle = findViewById(R.id.toolbar_title);
            setSupportActionBar(toolbar);
            toolbarTitle.setText(toolbar.getTitle());
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            boolean shouldDisplayBack = (this instanceof HomeV2Activity) ? false : true;
            getSupportActionBar().setDisplayHomeAsUpEnabled(shouldDisplayBack);
        }
    }

    protected void turnScreenOnThroughKeyguard() {
        userPowerManagerWakeup();
        useWindowFlags();
        useActivityScreenMethods();
    }

    protected void startProcessing() {
        if (isProcessing) return;

        isProcessing = true;
        progressbar = findViewById(R.id.progress_bar);
        container = findViewById(R.id.container);

        if (progressbar != null) {
            progressbar.setVisibility(View.VISIBLE);
        }
        if (container != null) {
            container.setVisibility(View.GONE);
        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                stopProcessing();
//            }
//        }, processingTimeOut);
    }

    protected void stopProcessing() {
        isProcessing = false;
        if (progressbar != null) {
            progressbar.setVisibility(View.GONE);
        }
        if (container != null) {
            container.setVisibility(View.VISIBLE);
        }
    }

    private void useActivityScreenMethods() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            try {
                // For newer than Android Oreo: call setShowWhenLocked, setTurnScreenOn
                setTurnScreenOn(true);
                setShowWhenLocked(true);

                // If you want to display the keyguard to prompt the user to unlock the phone:
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                keyguardManager.requestDismissKeyguard(this,null);

            } catch (NoSuchMethodError e) {
                Log.e(String.valueOf(e),"Enable setTurnScreenOn and setShowWhenLocked is not present on device!");
            }
        }
    }

    private void useWindowFlags() {
        // Set flags so activity is showed when phone is off (on lock screen)
        // Link: https://stackoverflow.com/questions/60535947/android-full-screen-notification-wont-show-over-the-lock-screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void userPowerManagerWakeup() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "VirtualAssistant:PowerManagerWakeLock");
        wakelock.acquire(TimeUnit.SECONDS.toMillis(5));
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
