package com.vnsolution.virtualassistant.service.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vnsolution.virtualassistant.common.Constants;

import java.util.ArrayList;
import java.util.List;

public class ActivityTrackingService extends Service {
    public static final String[] REQUEST_PERMISSIONS = { Manifest.permission.ACTIVITY_RECOGNITION };

    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    public ActivityTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityRecognitionClient = new ActivityRecognitionClient(this);

//        Intent intent = new Intent(this, ActivitiesHandlerService.class);
//        mPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        requestActivityUpdates();

        Intent intent = new Intent(this, ActivityTransitionBroadcastReceiver.class);
        intent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityTransitionUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelActivityTransitionUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

//    private void requestActivityUpdates() {
//        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
//                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
//                mPendingIntent);
//
//        task.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void result) {
//                Toast.makeText(getApplicationContext(),
//                        "Successfully requested activity updates",
//                        Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
//
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(),
//                        "Requesting activity updates failed to start",
//                        Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
//    }

    private void requestActivityTransitionUpdates() {
        ActivityTransitionRequest request = buildActivityTransitionRequest();
        Task<Void> task = mActivityRecognitionClient
                .requestActivityTransitionUpdates(request, mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ActivityTrackingService.this,
                        "Successfully requested activity updates",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityTrackingService.this,
                        "Failed to request activity updates",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void cancelActivityTransitionUpdates() {
        Task<Void> task = mActivityRecognitionClient
                .removeActivityTransitionUpdates(mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mPendingIntent.cancel();
                Toast.makeText(ActivityTrackingService.this,
                        "Successfully cancel activity transition updates",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityTrackingService.this,
                        "Failed to cancel activity transition updates",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private ActivityTransitionRequest buildActivityTransitionRequest() {
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

//        transitions.add(
//                new ActivityTransition.Builder()
//                        .setActivityType(DetectedActivity.TILTING)
//                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//                        .build());

        return new ActivityTransitionRequest(transitions);
    }
}
