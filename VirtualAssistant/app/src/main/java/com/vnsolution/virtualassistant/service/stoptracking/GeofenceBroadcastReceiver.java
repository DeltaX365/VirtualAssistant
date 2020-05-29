package com.vnsolution.virtualassistant.service.stoptracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.service.AppService;

import java.util.ArrayList;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = GeofenceBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        // GeofenceTransitionsJobIntentService.enqueueWork(context, intent);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            AppService.sendNotification(context, errorMessage);
            return;
        }
        handleEvent(context, geofencingEvent);
    }

    private void handleEvent(Context context, GeofencingEvent geofencingEvent) {
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        String geofencingStatus = "";
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
//            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(geoFenceTransition, triggeringGeofences );
//            Toast.makeText(context, geofenceTransitionDetails, Toast.LENGTH_LONG).show();
            geofencingStatus = "GEOFENCE_TRANSITION_EXIT";

            Intent serviceIntent = new Intent(context, StopTrackingService.class);
            serviceIntent.setAction(StopTrackingService.INTENT_ACTION_CHECK_OUT);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            geofencingStatus = "GEOFENCE_TRANSITION_ENTER";
        }
        if (!geofencingStatus.isEmpty()) {
            AppService.sendNotification(context, geofencingStatus);
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }
        String status = null;
        if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "Entering ";
        else if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Exiting ";
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }

    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
