package com.vnsolution.virtualassistant.service.activities;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.vnsolution.virtualassistant.common.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class ActivitiesHandlerService extends IntentService {
    protected static final String TAG = ActivitiesHandlerService.class.getSimpleName();

    public ActivitiesHandlerService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
        Collections.sort(detectedActivities, (a1, a2) -> a2.getConfidence() - a1.getConfidence());
        if (!detectedActivities.isEmpty()) {
            DetectedActivity activity = detectedActivities.get(0);
            Toast.makeText(getApplicationContext(),"Detected activity: " + activity.getType() + ", " + activity.getConfidence(), Toast.LENGTH_SHORT).show();
            broadcastActivity(activity);
        }
    }

    private void broadcastActivity(DetectedActivity activity) {
        Intent intent = new Intent(Constants.BROADCAST_ACTIVITY_DETECTED);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
