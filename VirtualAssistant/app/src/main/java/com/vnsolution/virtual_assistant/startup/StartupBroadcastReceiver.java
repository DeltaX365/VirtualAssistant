package com.vnsolution.virtual_assistant.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vnsolution.virtual_assistant.service.stop_tracking.StopTrackingService;

public class StartupBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Intent activityTrackingIntent = new Intent(context, StopTrackingService.class);
            context.startService(activityTrackingIntent);
        }
    }
}
