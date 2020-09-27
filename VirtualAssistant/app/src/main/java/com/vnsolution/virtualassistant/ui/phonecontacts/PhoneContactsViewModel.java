package com.vnsolution.virtualassistant.ui.phonecontacts;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.service.device.DeviceManager;

import java.util.List;

import javax.inject.Inject;

public class PhoneContactsViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public PhoneContactsViewModel() {}

    public LiveData<List<PhoneContact>> getContacts(Context context, boolean forceUpdate) {
        return deviceManager.getPhoneContacts(context, forceUpdate);
    }
}