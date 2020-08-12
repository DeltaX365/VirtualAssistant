package com.vnsolution.virtualassistant.ui.applications;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.ArrayMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.Application;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InstalledApplicationsViewModel extends ViewModel {
    private static final int DELAY_FETCH_DATA_IN_MILLISECONDS = 1 * 1000; // 1 second
    private Context context;
    private ArrayMap<String, UsageStats> appUsageStats;
    private MutableLiveData<List<Application>> installedApplicationsLiveData;

    public InstalledApplicationsViewModel(Context context) {
        this.context = context;
    }

    public MutableLiveData<List<Application>> getInstalledApplications(boolean shouldGetSystemPackages, boolean forceUpdate) {
        if (installedApplicationsLiveData == null || forceUpdate) {
            installedApplicationsLiveData = new MutableLiveData<>();
            getInstalledApplicationsAsync(shouldGetSystemPackages);
        }
        return installedApplicationsLiveData;
    }

    public void launchApplication(Application application) {
        if (context != null && application != null && !application.packageName.isEmpty()) {
            Intent launchAppIntent = context.getPackageManager().getLaunchIntentForPackage(application.packageName);
            context.startActivity(launchAppIntent);
        }
    }

    private void getInstalledApplicationsAsync(boolean shouldGetSystemPackages) {
        // Do async operation to fetch installed applications
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Application> applications = new ArrayList<>();
                if (context != null) {
                    List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
                    Collections.sort(packageInfos, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo a1, PackageInfo a2) {
                            return a1.applicationInfo.loadLabel(context.getPackageManager()).toString().compareTo(a2.applicationInfo.loadLabel(context.getPackageManager()).toString());
                        }
                    });
                    for (PackageInfo packageInfo : packageInfos) {
                        if (!shouldGetSystemPackages && isSystemPackage(packageInfo)) {
                            continue;
                        }
                        Application application = new Application();
                        application.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                        application.packageName = packageInfo.packageName;
                        application.versionCode = packageInfo.versionCode;
                        application.versionName = packageInfo.versionName;
                        application.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                        getUsageStats(application);
                        applications.add(application);
                    }
                }
                installedApplicationsLiveData.setValue(applications);
            }
        }, DELAY_FETCH_DATA_IN_MILLISECONDS);
    }

    private void getUsageStats(Application application) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && context != null && application != null && (appUsageStats == null || appUsageStats.isEmpty())) {

            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, calendar.getTimeInMillis(), System.currentTimeMillis());
            if (usageStats.isEmpty()) {
                context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }

            appUsageStats = new ArrayMap<>();
            for (UsageStats usageStat: usageStats) {
                UsageStats existingStat = appUsageStats.get(usageStat.getPackageName());
                if (existingStat == null) {
                    appUsageStats.put(usageStat.getPackageName(), usageStat);
                } else {
                    existingStat.add(usageStat);
                }
            }
        }
        UsageStats usageStat = appUsageStats.get(application.packageName);
        application.usageStats = usageStat;
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo != null) && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0));
    }
}