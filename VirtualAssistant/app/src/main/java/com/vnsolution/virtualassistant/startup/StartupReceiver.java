package com.vnsolution.virtualassistant.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vnsolution.virtualassistant.service.BackgroundDetectedActivitiesService;
import com.vnsolution.virtualassistant.ui.dashboard.DashboardActivity;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Intent intent1 = new Intent(context, BackgroundDetectedActivitiesService.class);
            context.startService(intent1);
        }
    }
}
