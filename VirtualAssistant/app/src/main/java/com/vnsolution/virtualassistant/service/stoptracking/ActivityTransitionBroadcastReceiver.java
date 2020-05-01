package com.vnsolution.virtualassistant.service.stoptracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.vnsolution.virtualassistant.common.Constants;

import java.util.List;

public class ActivityTransitionBroadcastReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "com.vnsolution.virtualassistant.ACTION_PROCESS_ACTIVITY_TRANSITIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && INTENT_ACTION.equals(intent.getAction())) {
            if (ActivityTransitionResult.hasResult(intent)) {
                ActivityTransitionResult activityTransitionResult = ActivityTransitionResult
                        .extractResult(intent);
                handleActivityTransitionResult(context, activityTransitionResult);
            }
        }
    }

    private void handleActivityTransitionResult(Context context, ActivityTransitionResult result) {
        List<ActivityTransitionEvent> events = result.getTransitionEvents();
        if (events != null && !events.isEmpty()) {
            ActivityTransitionEvent event = result.getTransitionEvents().get(0);
            if ((event.getActivityType() == DetectedActivity.STILL)
                && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)) {
                Intent intent = new Intent(Constants.BROADCAST_STOP_DETECTED);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
    }
}
