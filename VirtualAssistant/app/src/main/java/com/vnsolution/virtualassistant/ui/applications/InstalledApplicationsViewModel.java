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
import com.vnsolution.virtualassistant.service.devicemanagement.DeviceManager;

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
        return (DeviceManager.getInstance().getInstalledApplications(context, shouldGetSystemPackages, forceUpdate));
    }

    public void launchApplication(Application application) {
        if (context != null && application != null && !application.packageName.isEmpty()) {
            Intent launchAppIntent = context.getPackageManager().getLaunchIntentForPackage(application.packageName);
            context.startActivity(launchAppIntent);
        }
    }
}