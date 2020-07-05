package com.vnsolution.virtualassistant.ui.calllogs;

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

import java.util.ArrayList;
import java.util.List;

public class CallLogsViewModel extends ViewModel {
    private static final int DELAY_FETCH_DATA_IN_MILLISECONDS = 1 * 1000; // 1 second
    private Context context;
    private MutableLiveData<List<CallLog>> callLogsLiveData;

    public CallLogsViewModel(Context context) {
        this.context = context;
    }

    public MutableLiveData<List<CallLog>> getCallLogs(boolean forceUpdate) {
        if (callLogsLiveData == null || forceUpdate) {
            callLogsLiveData = new MutableLiveData<>();
            getCallLogsAsync();
        }
        return callLogsLiveData;
    }

    private void getCallLogsAsync() {
        // Do async operation to fetch installed applications
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<CallLog> callLogs = new ArrayList<>();
                if (context != null && checkPermission()) {
                    Cursor cursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
                    while (cursor.moveToNext()) {
                        String phoneNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                        String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                        int callDirectionCode = Integer.parseInt(callType);
                        String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
                        //Date callDateTime = new Date(Long.valueOf(callDate));
                        String callDuration = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION));

                        CallLog callLog = new CallLog();
                        callLog.phoneNumber = phoneNumber;
                        callLog.callDirectionCode = callDirectionCode;
                        callLog.callDate = callDate;
                        callLog.callDuration = callDuration;
                        callLogs.add(callLog);
                    }
                    cursor.close();
                    callLogsLiveData.setValue(callLogs);
                }
            }
        }, DELAY_FETCH_DATA_IN_MILLISECONDS);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}