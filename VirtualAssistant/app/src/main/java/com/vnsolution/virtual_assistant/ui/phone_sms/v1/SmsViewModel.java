package com.vnsolution.virtual_assistant.ui.phone_sms.v1;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtual_assistant.model.PhoneSms;
import com.vnsolution.virtual_assistant.service.device.DeviceManager;

import java.util.List;

import javax.inject.Inject;

public class SmsViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public SmsViewModel() {}

    public LiveData<List<PhoneSms>> getSms(Context context, boolean forceUpdate) {
        return deviceManager.getPhoneSms(context, forceUpdate);
    }
}