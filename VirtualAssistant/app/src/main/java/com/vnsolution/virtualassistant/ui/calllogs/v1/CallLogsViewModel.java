package com.vnsolution.virtualassistant.ui.calllogs.v1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.CallLog;
import com.vnsolution.virtualassistant.service.devicemanagement.DeviceManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CallLogsViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public CallLogsViewModel() {}

    public MutableLiveData<List<CallLog>> getCallLogs(Context context, boolean forceUpdate) {
        return deviceManager.getCallLogs(context, forceUpdate);
    }
}