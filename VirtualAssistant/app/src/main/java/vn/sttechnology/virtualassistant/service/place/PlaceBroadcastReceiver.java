package vn.sttechnology.virtualassistant.service.place;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import vn.sttechnology.virtualassistant.common.StopTrackingSetting;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class PlaceBroadcastReceiver extends BroadcastReceiver {
    @Inject
    PlaceManager placeManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
        handle(context, intent);
    }

    private void handle(Context context, Intent intent) {
        if (intent != null) {
            int notifyId = intent.getIntExtra(StopTrackingSetting.NOTIFY_ID, 0);
            if (notifyId > 0) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String placeId = intent.getStringExtra(StopTrackingSetting.NOTIFY_DETECTED_PLACE_DATA_PLACE_ID);
            if (placeManager != null && placeId != null && !placeId.isEmpty()) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        placeManager.confirmDetectedPlace(placeId);
                        Toast.makeText(context, "Place is confirmed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
