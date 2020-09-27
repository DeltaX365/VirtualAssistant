package com.vnsolution.virtualassistant.service.stop_tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationResult;
import com.vnsolution.virtualassistant.common.StopTracking;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "com.vnsolution.virtualassistant.ACTION_UPDATE_LOCATIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null &&
            intent.getAction().equals(INTENT_ACTION) &&
            intent.getCategories().contains(StopTracking.INTENT_CATEGORY_ACTIVITY_TRANSITION)) {
            // Enqueues a JobIntentService passing the context and intent as parameters
            // ActivityTransitionsJobIntentService.enqueueWork(context, intent);
            LocationResult result = LocationResult.extractResult(intent);
            if (result != null) {
                Location location = result.getLastLocation();
                Intent serviceIntent = new Intent(context, StopTrackingService.class);

                serviceIntent.setAction(StopTracking.INTENT_ACTION_LOCATION_UPDATED);
                serviceIntent.putExtra("Provider", location.getProvider());
                serviceIntent.putExtra("Longitude", location.getLongitude());
                serviceIntent.putExtra("Latitude", location.getLatitude());

                ContextCompat.startForegroundService(context, serviceIntent);
            }
        }
    }
}
