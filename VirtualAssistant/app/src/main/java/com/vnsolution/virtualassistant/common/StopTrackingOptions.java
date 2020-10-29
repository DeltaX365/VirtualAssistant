package com.vnsolution.virtualassistant.common;

import android.Manifest;

public class StopTrackingOptions {
    // Stop tracking broadcast.
    public static final String BROADCAST_PERMISSIONS_ALL_GRANTED = "com.vnsolution.virtualassistant.STOP_TRACKING_BROADCAST_PERMISSIONS_ALL_GRANTED";
    public static final String BROADCAST_PERMISSIONS_LOCATION_GRANTED = "com.vnsolution.virtualassistant.STOP_TRACKING_BROADCAST_LOCATION_PERMISSIONS_GRANTED";
    public static final String BROADCAST_PERMISSIONS_USER_ACTIVITY_GRANTED = "com.vnsolution.virtualassistant.STOP_TRACKING_BROADCAST_PERMISSIONS_USER_ACTIVITY_GRANTED";

    // Stop tracking intents.
    public static final String INTENT_ACTION_INITIAL = "com.vnsolution.virtualassistant.STOP_TRACKING_INTENT_ACTION_INITIAL";
    public static final String INTENT_ACTION_CHECK_IN = "com.vnsolution.virtualassistant.STOP_TRACKING_INTENT_ACTION_CHECK_IN";
    public static final String INTENT_ACTION_MARK_AS_VISITED = "com.vnsolution.virtualassistant.STOP_TRACKING_INTENT_ACTION_MARK_AS_VISITED";
    public static final String INTENT_ACTION_CHECK_OUT = "com.vnsolution.virtualassistant.STOP_TRACKING_INTENT_ACTION_CHECK_OUT";
    public static final String INTENT_CATEGORY_ACTIVITY_TRANSITION = "com.vnsolution.virtualassistant.STOP_TRACKING_INTENT_CATEGORY_ACTIVITY_TRANSITION";

    // Stop tracking permissions.
    public static final int PERMISSIONS_REQUEST_CODE = 1000;
    public static final String[] PERMISSIONS_STOP_TRACKING = {Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    public static final String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    public static final String[] PERMISSIONS_ACTIVITY_RECOGNITION = {Manifest.permission.ACTIVITY_RECOGNITION};

    // Stop tracking requests.
    public static final String REQUEST_START_ACTIVITY_TRANSACTION_MONITOR = "STOP_TRACKING_REQUEST_START_ACTIVITY_TRANSACTION_MONITOR";
    public static final String REQUEST_STOP_ACTIVITY_TRANSACTION_MONITOR = "STOP_TRACKING_REQUEST_STOP_ACTIVITY_TRANSACTION_MONITOR";
    public static final String REQUEST_START_GEOFENCE_CHECK_IN_MONITOR = "STOP_TRACKING_REQUEST_START_GEOFENCE_CHECK_IN_MONITOR";
    public static final String REQUEST_START_GEOFENCE_CHECK_OUT_MONITOR = "STOP_TRACKING_REQUEST_START_GEOFENCE_CHECK_OUT_MONITOR";
    public static final String REQUEST_STOP_GEOFENCE_MONITOR = "STOP_TRACKING_REQUEST_STOP_GEOFENCE_MONITOR";
    public static final String REQUEST_START_LOCATION_UPDATES = "STOP_TRACKING_REQUEST_START_LOCATION_UPDATES";
    public static final String REQUEST_STOP_LOCATION_UPDATES = "STOP_TRACKING_REQUEST_STOP_LOCATION_UPDATES";
    public static final String REQUEST_STOP_FOREGROUND_SERVICE = "STOP_TRACKING_REQUEST_STOP_FOREGROUND_SERVICE";
    public static final String REQUEST_MARK_AS_VISITED = "STOP_TRACKING_REQUEST_MARK_AS_VISITED";
    public static final String REQUEST_CHECK_OUT = "STOP_TRACKING_REQUEST_CHECK_OUT";

    // Stop tracking actions
    public static final String ACTION_CHECK_IN_MONITOR = "ACTION_CHECK_IN_MONITOR";
    public static final String ACTION_CHECK_OUT_MONITOR = "ACTION_CHECK_OUT_MONITOR";

    // Stop tracking configs.
    public static final int CONFIG_STOP_DETECTION_IN_MILLISECONDS = 10*60*1000;
    public static final int CONFIG_GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS = 2*60*1000;
    public static final int CONFIG_GEOLOCATION_FAST_INTERVAL_UPDATE_IN_MILLISECONDS = CONFIG_GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS;
    public static final int CONFIG_GEOFENCE_RADIUS_IN_METERS = 100;
    public static final int CONFIG_GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1*60*1000;
    public static final int CONFIG_GEOFENCE_DWELLING_IN_MILLISECONDS = 30*60*1000;

    // Notifications.
    public static final String NOTIFY_CHANNEL_ID = "com.vnsolution.virtualassistant.STOP_TRACKING_FOREGROUND_SERVICE";
    public static final String NOTIFY_CHANNEL_NAME = "Stop Detection";
    public static final String NOTIFY_FOREGROUND_SERVICE_TITLE = "Foreground Service";
    public static final String NOTIFY_FOREGROUND_SERVICE_TEXT = "Checking activity...";
}
