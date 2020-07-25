package com.vnsolution.virtualassistant.common;

public class Constants {
    public static final String GM_API_KEY = "AIzaSyBQr8YzGqe0PWQiEMx9JbsnXmbpRZvQURo";
    public static final String OPEN_WEATHER_API_KEY = "071dcc0541ea2d9dfaef941b9a414685";

    public static final String BROADCAST_ACTIVITY_RECOGNITION_PERMISSION_CHANGED = "BROADCAST_ACTIVITY_RECOGNITION_PERMISSION_CHANGED";
    public static final String BROADCAST_LOCATION_PERMISSION_CHANGED = "BROADCAST_LOCATION_PERMISSION_CHANGED";
    public static final String BROADCAST_ACTIVITY_DETECTED = "BROADCAST_ACTIVITY_DETECTED";
    public static final String BROADCAST_STOP_DETECTED = "BROADCAST_STOP_DETECTED";
    public static final String BROADCAST_GEOFENCE_EXIST_DETECTED = "BROADCAST_GEOFENCE_EXIST_DETECTED";
    public static final String BROADCAST_PLACE_ADDED = "BROADCAST_PLACE_ADDED";

    public static final int GEOFENCE_RADIUS_IN_METERS = 100;
    public static final int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1*60*1000;
    public static final int GEOFENCE_DWELLING_IN_MILLISECONDS = 2*60*1000;
    public static final int STOP_DETECTION_IN_MILLISECONDS = 5*60*1000; //stop at least 5 minutes
    public static final int GEOLOCATION_INTERVAL_UPDATE = 2*60*1000;
    public static final int GEOLOCATION_FAST_INTERVAL_UPDATE = GEOLOCATION_INTERVAL_UPDATE/2;

    public static final int UNKNOWN_CODE = -1;
}
