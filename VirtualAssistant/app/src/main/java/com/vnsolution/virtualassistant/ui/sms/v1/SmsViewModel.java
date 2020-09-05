package com.vnsolution.virtualassistant.ui.sms.v1;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.CallLog;
import com.vnsolution.virtualassistant.model.Sms;
import com.vnsolution.virtualassistant.service.devicemanagement.DeviceManager;

import java.util.List;

public class SmsViewModel extends ViewModel {
    private Context context;

    public SmsViewModel(Context context) {
        this.context = context;
    }

    public LiveData<List<Sms>> getSms(boolean forceUpdate) {
        return (DeviceManager.getInstance().getSms(context, forceUpdate));
    }
}