package com.vnsolution.virtualassistant.service.stoptracking;

import android.content.Context;
import android.content.Intent;

import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.vnsolution.virtualassistant.common.Constants;

import java.util.List;

public class ActivityTransitionsJobIntentService extends JobIntentService {
    private static final int JOB_ID = 1000;
    private static final String TAG = ActivityTransitionsJobIntentService.class.getName();

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, ActivityTransitionsJobIntentService.class, JOB_ID, intent);
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleWork(Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult activityTransitionResult = ActivityTransitionResult.extractResult(intent);
            handleActivityTransitionResult(activityTransitionResult);
        }
    }

    private void handleActivityTransitionResult(ActivityTransitionResult result) {
        List<ActivityTransitionEvent> events = result.getTransitionEvents();
        if (events != null && !events.isEmpty()) {
            ActivityTransitionEvent event = result.getTransitionEvents().get(0);
            if ((event.getActivityType() == DetectedActivity.STILL)
                    && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)) {
                //TODO
            }
        }
    }
}
