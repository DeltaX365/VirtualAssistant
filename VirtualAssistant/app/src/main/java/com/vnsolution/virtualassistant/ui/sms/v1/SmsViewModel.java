package com.vnsolution.virtualassistant.ui.sms.v1;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.CallLog;
import com.vnsolution.virtualassistant.model.Sms;
import com.vnsolution.virtualassistant.service.devicemanagement.DeviceManager;

import java.util.List;

import javax.inject.Inject;

public class SmsViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public SmsViewModel() {}

    public LiveData<List<Sms>> getSms(Context context, boolean forceUpdate) {
        return deviceManager.getSms(context, forceUpdate);
    }
}