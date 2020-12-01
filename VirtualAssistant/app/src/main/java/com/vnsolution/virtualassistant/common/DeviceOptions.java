package com.vnsolution.virtualassistant.common;

import android.Manifest;

public class DeviceOptions {
    // Intents.
    public static final String INTENT_ACTION_SYNC_PHONE_CONTACT = "com.vnsolution.virtualassistant.ACTION_SYNC_PHONE_CONTACT";
    public static final String INTENT_ACTION_SYNC_PHONE_CALL = "com.vnsolution.virtualassistant.ACTION_SYNC_PHONE_CALL";
    public static final String INTENT_ACTION_SYNC_PHONE_SMS = "com.vnsolution.virtualassistant.ACTION_SYNC_PHONE_SMS";

    // Permissions.
    public static final int PERMISSIONS_REQUEST_CODE = 2000;
    public static final String[] PERMISSIONS_PHONE_CALL = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.PROCESS_OUTGOING_CALLS};
    public static final String[] PERMISSIONS_PHONE_SMS = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS};

    // Notifications.
    public static final String NOTIFY_FOREGROUND_SERVICE_CHANNEL_ID = "com.vnsolution.virtualassistant.DEVICE_FOREGROUND_SERVICE";
    public static final String NOTIFY_FOREGROUND_SERVICE_CHANNEL_NAME = "Device Management";
    public static final String NOTIFY_FOREGROUND_SERVICE_TITLE = "Device Management";
    public static final String NOTIFY_FOREGROUND_SERVICE_TEXT = "Syncing...";
}
