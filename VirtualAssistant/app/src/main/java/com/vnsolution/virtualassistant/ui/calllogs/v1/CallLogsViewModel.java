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

public class CallLogsViewModel extends ViewModel {
    private Context context;

    public CallLogsViewModel(Context context) {
        this.context = context;
    }

    public MutableLiveData<List<CallLog>> getCallLogs(boolean forceUpdate) {
        return (DeviceManager.getInstance().getCallLogs(context, forceUpdate));
    }
}