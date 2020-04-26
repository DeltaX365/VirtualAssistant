package com.vnsolution.virtualassistant.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vnsolution.virtualassistant.service.activities.ActivityTrackingService;

public class StartupBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Intent activityTrackingIntent = new Intent(context, ActivityTrackingService.class);
            context.startService(activityTrackingIntent);
        }
    }
}
