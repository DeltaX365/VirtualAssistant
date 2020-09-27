package com.vnsolution.virtualassistant.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vnsolution.virtualassistant.service.AppService;

public class StartupBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            new AppService(context).run();
        }
    }
}
