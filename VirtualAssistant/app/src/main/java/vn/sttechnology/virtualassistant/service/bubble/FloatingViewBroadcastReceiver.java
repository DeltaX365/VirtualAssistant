package vn.sttechnology.virtualassistant.service.bubble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FloatingViewBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Receive broadcast: " + intent.getAction(), Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //TODO
        }
    }
}