package com.vnsolution.virtualassistant.service.devicemanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.Settings;
import android.util.ArrayMap;

import androidx.lifecycle.MutableLiveData;

import com.vnsolution.virtualassistant.model.Application;
import com.vnsolution.virtualassistant.model.CallLog;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceManager {
    private static final int DELAY_FETCH_DATA_IN_MILLISECONDS = 1 * 1000; // 1 second
    private static DeviceManager instance;

    private ArrayMap<String, UsageStats> appUsageStats;
    private MutableLiveData<List<Application>> installedApplicationsLiveData;
    private MutableLiveData<List<CallLog>> callLogsLiveData;

    private DeviceManager() {}

    public static synchronized DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    public MutableLiveData<List<Application>> getInstalledApplications(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        if (installedApplicationsLiveData == null || forceUpdate) {
            installedApplicationsLiveData = new MutableLiveData<>();
            getInstalledApplicationsAsync(context, shouldGetSystemPackages);
        }
        return installedApplicationsLiveData;
    }

    public MutableLiveData<List<CallLog>> getCallLogs(Context context, boolean forceUpdate) {
        if (callLogsLiveData == null || forceUpdate) {
            callLogsLiveData = new MutableLiveData<>();
            getCallLogsAsync(context);
        }
        return callLogsLiveData;
    }

    private void getInstalledApplicationsAsync(Context context, boolean shouldGetSystemPackages) {
        // Do async operation to fetch installed applications
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Application> applications = new ArrayList<>();
                if (context != null) {
                    getUsageStats(context);
                    List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
//                    Collections.sort(packageInfos, new Comparator<PackageInfo>() {
//                        @Override
//                        public int compare(PackageInfo a1, PackageInfo a2) {
//                            return a1.applicationInfo.loadLabel(context.getPackageManager()).toString().compareTo(a2.applicationInfo.loadLabel(context.getPackageManager()).toString());
//                        }
//                    });
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
                        application.usageStats = appUsageStats.get(application.packageName);
                        applications.add(application);
                    }
                }
                installedApplicationsLiveData.setValue(applications);
            }
        }, DELAY_FETCH_DATA_IN_MILLISECONDS);
    }

    private void getUsageStats(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && context != null && (appUsageStats == null || appUsageStats.isEmpty())) {

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
    }

    private void getCallLogsAsync(Context context) {
        // Do async operation to fetch installed applications
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<CallLog> callLogs = new ArrayList<>();
                if (context != null && PermissionUtils.hasPermission(context, Manifest.permission.READ_CALL_LOG)) {
                    @SuppressLint("MissingPermission") Cursor cursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
                    while (cursor.moveToNext()) {
                        String phoneNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                        String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                        int callDirectionCode = Integer.parseInt(callType);
                        String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
                        //Date callDateTime = new Date(Long.valueOf(callDate));
                        String callDuration = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION));

                        CallLog callLog = new CallLog();
                        callLog.phoneNumber = phoneNumber;
                        callLog.callDirectionCode = callDirectionCode;
                        callLog.callDate = callDate;
                        callLog.callDuration = callDuration;
                        callLogs.add(callLog);
                        callLogsLiveData.setValue(callLogs);
                    }
                    cursor.close();
                }
            }
        }, DELAY_FETCH_DATA_IN_MILLISECONDS);
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo != null) && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0));
    }
}
