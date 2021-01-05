package com.vnsolution.virtualassistant.common;

import android.Manifest;

import com.vnsolution.virtualassistant.BuildConfig;

public class DeviceOptions {
    public static final String INTENT_ACTION_SYNC_PHONE_CONTACT = BuildConfig.APPLICATION_ID + "." + "ACTION_SYNC_PHONE_CONTACT";
    public static final String INTENT_ACTION_SYNC_PHONE_CALL = BuildConfig.APPLICATION_ID + "." + "ACTION_SYNC_PHONE_CALL";
    public static final String INTENT_ACTION_SYNC_PHONE_SMS = BuildConfig.APPLICATION_ID + "." + "ACTION_SYNC_PHONE_SMS";

    public static final int PERMISSIONS_REQUEST_CODE = 2000;
    public static final String[] PERMISSIONS_PHONE_CALL = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.PROCESS_OUTGOING_CALLS};
    public static final String[] PERMISSIONS_PHONE_SMS = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS};
    public static final String[] PERMISSIONS_PHONE_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    public static final String[] PERMISSIONS_MAKE_CALL = {Manifest.permission.CALL_PHONE};
    public static final String[] PERMISSIONS_EXPORT_CONTACT = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final String NOTIFY_FOREGROUND_SERVICE_CHANNEL_ID = BuildConfig.APPLICATION_ID + "." + "DEVICE_FOREGROUND_SERVICE";
    public static final String NOTIFY_FOREGROUND_SERVICE_CHANNEL_NAME = "Device Management";
    public static final String NOTIFY_FOREGROUND_SERVICE_TITLE = "Device Management";
    public static final String NOTIFY_FOREGROUND_SERVICE_TEXT = "Syncing...";
}
