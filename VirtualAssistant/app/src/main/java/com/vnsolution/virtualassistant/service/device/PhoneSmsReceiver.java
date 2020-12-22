package com.vnsolution.virtualassistant.service.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.vnsolution.virtualassistant.common.DeviceOptions;

public class PhoneSmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        syncPhoneSms(context);
    }

    private void syncPhoneSms(Context context) {
        Intent serviceIntent = new Intent(context, DeviceService.class);
        serviceIntent.setAction(DeviceOptions.INTENT_ACTION_SYNC_PHONE_SMS);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
