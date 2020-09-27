package com.vnsolution.virtualassistant.ui.phonesms.v1;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.PhoneSms;
import com.vnsolution.virtualassistant.service.device.DeviceManager;

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