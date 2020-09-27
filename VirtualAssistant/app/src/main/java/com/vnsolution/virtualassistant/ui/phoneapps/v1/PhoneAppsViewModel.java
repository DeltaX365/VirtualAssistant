package com.vnsolution.virtualassistant.ui.phoneapps.v1;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.service.device.DeviceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class PhoneAppsViewModel extends ViewModel {
    private Integer index = 1;

    @Inject
    public DeviceManager deviceManager;

    @Inject
    public PhoneAppsViewModel() {}

    public void setIndex(int index) {
        this.index = index;
    }

    public LiveData<List<PhoneApp>> getPhoneApps(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        LiveData<List<PhoneApp>> applicationsLiveData = deviceManager.getPhoneApps(context, shouldGetSystemPackages, forceUpdate);
        return Transformations.map(applicationsLiveData, applicationEntities -> {
            List<PhoneApp> applications = copy(applicationEntities);
            switch (index) {
                case 1: //Top 10 recent visited places
                    Collections.sort(applications, new Comparator<PhoneApp>() {
                        @Override
                        public int compare(PhoneApp o1, PhoneApp o2) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                long lastTimeUsed1 = (o1 != null && o1.usageStats != null)? o1.usageStats.getLastTimeUsed() : 0;
                                long lastTimeUsed2 = (o2 != null && o2.usageStats != null)? o2.usageStats.getLastTimeUsed() : 0;
                                return (int) (lastTimeUsed2 - lastTimeUsed1);
                            }
                            return 0;
                        }
                    });
                    applications = applications.subList(0, Math.min(applications.size(), 10));
                    break;
                case 2: //Top 10 most visited places
                    Collections.sort(applications, new Comparator<PhoneApp>() {
                        @Override
                        public int compare(PhoneApp o1, PhoneApp o2) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                long totalTimeInForeground1 = (o1 != null && o1.usageStats != null)? o1.usageStats.getTotalTimeInForeground() : 0;
                                long totalTimeInForeground2 = (o2 != null && o2.usageStats != null)? o2.usageStats.getTotalTimeInForeground() : 0;
                                return (int) (totalTimeInForeground2 - totalTimeInForeground1);
                            }
                            return 0;
                        }
                    });
                    applications = applications.subList(0, Math.min(applications.size(), 10));
                    break;
                case 3: //Top 10 favourite visited places
                    applications = new ArrayList<>();
                    break;
                case 4: //Top 10 unvisited places
                    Collections.sort(applications, new Comparator<PhoneApp>() {
                        @Override
                        public int compare(PhoneApp o1, PhoneApp o2) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                long totalTimeInForeground1 = (o1 != null && o1.usageStats != null)? o1.usageStats.getTotalTimeInForeground() : 0;
                                long totalTimeInForeground2 = (o2 != null && o2.usageStats != null)? o2.usageStats.getTotalTimeInForeground() : 0;
                                return (int) (totalTimeInForeground1 - totalTimeInForeground2);
                            }
                            return 0;
                        }
                    });
                    applications = applications.subList(0, Math.min(applications.size(), 10));
                    break;
                case 5: //All visited places
                    Collections.sort(applications, new Comparator<PhoneApp>() {
                        @Override
                        public int compare(PhoneApp o1, PhoneApp o2) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                long lastTimeUsed1 = (o1 != null && o1.usageStats != null)? o1.usageStats.getLastTimeUsed() : 0;
                                long lastTimeUsed2 = (o2 != null && o2.usageStats != null)? o2.usageStats.getLastTimeUsed() : 0;
                                return (int) (lastTimeUsed2 - lastTimeUsed1);
                            }
                            return 0;
                        }
                    });
                    break;
            }
            return applications;
        });
    }

    public void launchApplication(Context context, PhoneApp application) {
        if (context != null && application != null && !application.packageName.isEmpty()) {
            Intent launchAppIntent = context.getPackageManager().getLaunchIntentForPackage(application.packageName);
            context.startActivity(launchAppIntent);
        }
    }

    private List<PhoneApp> copy(List<PhoneApp> applications) {
        List<PhoneApp> cloned = new ArrayList<>();
        for (PhoneApp application: applications) {
            cloned.add(application);
        }
        return cloned;
    }
}