package com.vnsolution.virtualassistant.ui.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InstalledApplicationsViewModel extends ViewModel {
    private static final int delayFetchDataInMilisecond = 1 * 1000; // 1 second
    private Context context;
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
                        applications.add(application);
                    }
                }
                Collections.sort(applications, new Comparator<Application>() {
                    @Override
                    public int compare(Application a1, Application a2) {
                        return a1.appName.compareTo(a2.appName);
                    }
                });
                installedApplicationsLiveData.setValue(applications);
            }
        }, delayFetchDataInMilisecond);
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo != null) && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0));
    }
}