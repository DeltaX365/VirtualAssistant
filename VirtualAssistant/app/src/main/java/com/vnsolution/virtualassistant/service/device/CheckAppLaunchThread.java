package com.vnsolution.virtualassistant.service.device;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CheckAppLaunchThread extends Thread {
    private Context context;
    private Handler handler;
    private ActivityManager activityManager;
    private int timer = 0;

    public CheckAppLaunchThread(Handler mainHandler, Context context) {
        this.handler = mainHandler;
        this.context = context;
        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        this.setPriority(MAX_PRIORITY);
    }

    @Override
    public void run() {
        Looper.prepare();
        String prevTasks;
        String recentTasks = "";

        prevTasks = recentTasks;
        while (true) {
            try {
                String topPackageName = "";
                if (Build.VERSION.SDK_INT >= 21) {
                    UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
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
                    topPackageName = activityManager.getRunningAppProcesses().get(0).processName;
                }
                recentTasks = topPackageName;
                Thread.sleep(timer);
                if (recentTasks.length() > 0 && !recentTasks.equals(prevTasks)) {
                    if (isAppLocked(recentTasks)) {
                        handler.post(new RequestPasswordRunnable(context, recentTasks));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isAppLocked(String packageName) {
        if (packageName.equals("com.mic.bluezone")) {
            return true;
        }
        return false;
    }
}
