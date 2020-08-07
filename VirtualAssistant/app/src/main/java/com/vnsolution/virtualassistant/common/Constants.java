package com.vnsolution.virtualassistant.common;

import android.Manifest;

public class Constants {
    public static final String GM_API_KEY = "AIzaSyBQr8YzGqe0PWQiEMx9JbsnXmbpRZvQURo";
    public static final String OPEN_WEATHER_API_KEY = "071dcc0541ea2d9dfaef941b9a414685";

    public static final String BROADCAST_ACTIVITY_RECOGNITION_PERMISSION_CHANGED = "BROADCAST_ACTIVITY_RECOGNITION_PERMISSION_CHANGED";
    public static final String BROADCAST_LOCATION_PERMISSION_CHANGED = "BROADCAST_LOCATION_PERMISSION_CHANGED";
    public static final String BROADCAST_ACTIVITY_DETECTED = "BROADCAST_ACTIVITY_DETECTED";
    public static final String BROADCAST_STOP_DETECTED = "BROADCAST_STOP_DETECTED";
    public static final String BROADCAST_GEOFENCE_EXIST_DETECTED = "BROADCAST_GEOFENCE_EXIST_DETECTED";
    public static final String BROADCAST_PLACE_ADDED = "BROADCAST_PLACE_ADDED";

    public static final String[] STOP_TRACKING_PERMISSIONS = { Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION };
    public static final String INTENT_ACTION_INITIAL = "com.vnsolution.virtualassistant.ACTION_INITIAL";
    public static final String INTENT_ACTION_CHECK_IN = "com.vnsolution.virtualassistant.ACTION_CHECK_IN";
    public static final String INTENT_ACTION_LOCATION_UPDATED = "com.vnsolution.virtualassistant.INTENT_ACTION_LOCATION_UPDATED";
    public static final String INTENT_ACTION_CHECK_OUT = "com.vnsolution.virtualassistant.ACTION_CHECK_OUT";
    public static final String INTENT_CATEGORY_ACTIVITY_TRANSITION_MONITOR = "com.vnsolution.virtualassistant.INTENT_CATEGORY_ACTIVITY_TRANSITION_MONITOR";

    public static final String STOP_TRACKING_CHANNEL_ID = "com.vnsolution.virtualassistant.STOP_TRACKING_FOREGROUND_SERVICE";
    public static final String STOP_TRACKING_CHANNEL_NAME = "Stop Detection";
    public static final String STOP_TRACKING_NOTIFY_TITLE = "Foreground Service";
    public static final String STOP_TRACKING_NOTIFY_TEXT = "Checking activity...";

    public static final String START_ACTIVITY_TRANSACTION_MONITOR = "START_ACTIVITY_TRANSACTION_MONITOR";
    public static final String STOP_ACTIVITY_TRANSACTION_MONITOR = "STOP_ACTIVITY_TRANSACTION_MONITOR";
    public static final String START_GEOFENCE_MONITOR = "START_GEOFENCE_MONITOR";
    public static final String STOP_GEOFENCE_MONITOR = "STOP_GEOFENCE_MONITOR";
    public static final String START_REQUEST_LOCATION_UPDATES = "START_LOCATION_UPDATES";
    public static final String STOP_REQUEST_LOCATION_UPDATES = "STOP_LOCATION_UPDATES";
    public static final String STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE";
    public static final String MARK_AS_VISITED = "MARK_AS_VISITED";

    public static final int STOP_DETECTION_IN_MILLISECONDS = 10*60*1000;
    public static final int GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS = 2*60*1000;
    public static final int GEOLOCATION_FAST_INTERVAL_UPDATE_IN_MILLISECONDS = GEOLOCATION_INTERVAL_UPDATE_IN_MILLISECONDS / 2;
    public static final int GEOFENCE_RADIUS_IN_METERS = 50;
    public static final int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1*60*1000;
    public static final int GEOFENCE_DWELLING_IN_MILLISECONDS = 2*60*1000;

    public static final int UNKNOWN_CODE = -1;
}
