package vn.sttechnology.virtualassistant.service.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import vn.sttechnology.virtualassistant.common.DeviceSetting;

public class PhoneAppBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        syncPhoneSms(context);
    }

    private void syncPhoneSms(Context context) {
        Intent serviceIntent = new Intent(context, DeviceService.class);
        serviceIntent.setAction(DeviceSetting.INTENT_ACTION_SYNC_PHONE_APP);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
