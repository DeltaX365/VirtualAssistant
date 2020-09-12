package com.vnsolution.virtualassistant.ui.phonecalls.v1;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.PhoneCall;
import com.vnsolution.virtualassistant.service.devicemanagement.DeviceManager;

import java.util.List;

import javax.inject.Inject;

public class CallLogsViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public CallLogsViewModel() {}

    public MutableLiveData<List<PhoneCall>> getCallLogs(Context context, boolean forceUpdate) {
        return deviceManager.getPhoneCalls(context, forceUpdate);
    }
}