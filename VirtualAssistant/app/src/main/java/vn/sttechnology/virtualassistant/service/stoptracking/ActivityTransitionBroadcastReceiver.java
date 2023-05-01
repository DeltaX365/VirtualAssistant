package vn.sttechnology.virtualassistant.service.stoptracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import vn.sttechnology.virtualassistant.common.StopTrackingSetting;

import java.util.List;

public class ActivityTransitionBroadcastReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "vn.sttechnology.virtualassistant.ACTION_PROCESS_ACTIVITY_TRANSITIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && INTENT_ACTION.equals(intent.getAction())) {
            // Enqueues a JobIntentService passing the context and intent as parameters
            // ActivityTransitionsJobIntentService.enqueueWork(context, intent);
            if (ActivityTransitionResult.hasResult(intent)) {
                ActivityTransitionResult activityTransitionResult = ActivityTransitionResult.extractResult(intent);
                handleActivityTransitionResult(context, activityTransitionResult);
            }
        }
    }

    private void handleActivityTransitionResult(Context context, ActivityTransitionResult result) {
        List<ActivityTransitionEvent> events = result.getTransitionEvents();
        if (!events.isEmpty()) {
            StringBuilder activityStatus = new StringBuilder();
            for(ActivityTransitionEvent event: events) {
                if ((event.getActivityType() == DetectedActivity.STILL) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)) {
                    activityStatus.append("Enter still." + "\n");
                    // Whenever user enters still, then we indicate that they are stopping at a new position, so check-in event will be invoked
                    Intent serviceIntent = new Intent(context, StopTrackingService.class);
                    serviceIntent.setAction(StopTrackingSetting.INTENT_ACTION_CHECK_IN);
                    ContextCompat.startForegroundService(context, serviceIntent);
                    break;
                }
                if ((event.getActivityType() == DetectedActivity.STILL) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT)) {
                    activityStatus.append("Exist still." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.WALKING) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)) {
                    activityStatus.append("Enter walking." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.WALKING) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT)) {
                    activityStatus.append("Exist walking." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.ON_FOOT) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)) {
                    activityStatus.append("Enter on foot." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.ON_FOOT) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT)) {
                    activityStatus.append("Exist on foot." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.RUNNING) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)) {
                    activityStatus.append("Enter running." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.RUNNING) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT)) {
                    activityStatus.append("Exist running." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.ON_BICYCLE) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)) {
                    activityStatus.append("Enter on bicycle." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.ON_BICYCLE) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT)) {
                    activityStatus.append("Exist on bicycle.");
                }
                if ((event.getActivityType() == DetectedActivity.IN_VEHICLE) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)) {
                    activityStatus.append("Enter in vehicle." + "\n");
                }
                if ((event.getActivityType() == DetectedActivity.IN_VEHICLE) && (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT)) {
                    activityStatus.append("Exist in vehicle." + "\n");
                }
            }
//            if (!activityStatus.isEmpty()) {
//                NotificationUtils.sendNotification(context, "ACTIVITY TRANSITIONS BROADCAST", activityStatus);
//            }
        }
    }
}
