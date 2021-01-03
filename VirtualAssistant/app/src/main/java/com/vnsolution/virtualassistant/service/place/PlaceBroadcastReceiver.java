package com.vnsolution.virtualassistant.service.place;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class PlaceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notifyId = intent.getIntExtra("NotifyId",0);
        if (notifyId > 0) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notifyId);
        }
        String placeId = intent.getStringExtra("PlaceId");
        if (placeId != null && !placeId.isEmpty()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    PlaceManager placeManager = new PlaceManager();
                    placeManager.confirmDetectedPlace(placeId);
                    Toast.makeText(context, "Place is confirmed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
