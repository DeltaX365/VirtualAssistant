package vn.sttechnology.virtualassistant.service.device;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.takwolf.android.lock9.Lock9View;
import vn.sttechnology.virtualassistant.BuildConfig;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.util.StringUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class AppLockService extends Service {
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture scheduledFuture;
    private ImageView imageView;
    private WindowManager windowManager;
    private Dialog dialog;
    private static String currentAppPackageName = "";
    private static String previousAppPackageName = "";

    @Inject
    public DeviceManager deviceManager;

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
        AndroidInjection.inject(this);

        // Create notification channel
        String chanelId = BuildConfig.APPLICATION_ID + "." + "BUBBLE_WIDGET_FOREGROUND_SERVICE";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(chanelId, "Bubble Widget", NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, chanelId)
                .setContentTitle("App Lock")
                .setContentText("Protect your apps")
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

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        imageView = new ImageView(this);
        imageView.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = displayMetrics.widthPixels / 2;
        params.y = displayMetrics.heightPixels / 2;
        windowManager.addView(imageView, params);

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(updateTask, 1000L, 1000L, TimeUnit.MILLISECONDS);

        return START_STICKY;
    }

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            currentAppPackageName = getRunningAppPackageName();
            if (currentAppPackageName != null && !currentAppPackageName.isEmpty()) {
                if (isAppLocked(currentAppPackageName)) {
                    if (imageView != null) {
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!currentAppPackageName.matches(previousAppPackageName)) {
                                    showUnlockDialog();
                                    previousAppPackageName = currentAppPackageName;
                                }
                            }
                        });
                    }
                } else {
                    if (imageView != null) {
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                hideUnlockDialog();
                            }
                        });
                    }
                }
            }
        }
    };

    private void showUnlockDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.screen_unlock, null);
        Lock9View lock9View = (Lock9View) promptsView.findViewById(R.id.lock_9_view);
        lock9View.setGestureCallback(new Lock9View.GestureCallback() {
            @Override
            public void onNodeConnected(@NonNull int[] numbers) {
            }

            @Override
            public void onGestureFinished(@NonNull int[] numbers) {
                String passwordConfirmed = StringUtils.join("", numbers);
                if (deviceManager.hasPhoneLockPasscode(AppLockService.this)) {
                    String passwordSaved = deviceManager.getPhoneLockPasscode(AppLockService.this);
                    if (passwordSaved.matches(passwordConfirmed)) {
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return true;
            }
        });
        dialog.show();
    }

    private void hideUnlockDialog() {
        previousAppPackageName = "";
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRunningAppPackageName() {
        String topPackageName = "";
        if (Build.VERSION.SDK_INT >= 21) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*5, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> sortedMap = new TreeMap<Long, UsageStats>();
                for(UsageStats usageStats : stats) {
                    sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (sortedMap != null && !sortedMap.isEmpty()) {
                    topPackageName = sortedMap.get(sortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            topPackageName = activityManager.getRunningAppProcesses().get(0).processName;
        }
        return topPackageName;
    }

    private boolean isAppLocked(String packageName) {
        List<String> lockedPackages = deviceManager.getLockedPhoneApps(AppLockService.this);
        if (lockedPackages.contains(packageName)) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduledFuture != null) {
            if (!scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(false);
                scheduledFuture = null;
            }
        }
        if (scheduledExecutorService != null) {
            if (!scheduledExecutorService.isShutdown()) {
                scheduledExecutorService.shutdown();
                scheduledExecutorService = null;
            }
        }
        if (imageView != null) {
            windowManager.removeView(imageView);
        }
        /**** added to fix the bug of view not attached to window manager ****/
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
