package com.vnsolution.virtualassistant.service;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.vnsolution.virtualassistant.service.stop_tracking.StopTrackingWorker;

import java.util.concurrent.TimeUnit;

public class AppWorkManager {
    private Context context;

    public AppWorkManager(Context context) {
        this.context = context;
    }

    public void run() {
        final PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(StopTrackingWorker.class, 3, TimeUnit.HOURS).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("StopTrackingWorker", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }
}
