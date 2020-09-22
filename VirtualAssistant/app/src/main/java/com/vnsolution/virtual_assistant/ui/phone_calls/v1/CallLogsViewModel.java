package com.vnsolution.virtual_assistant.ui.phone_calls.v1;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtual_assistant.model.PhoneCall;
import com.vnsolution.virtual_assistant.service.device.DeviceManager;

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