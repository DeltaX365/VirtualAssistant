package com.vnsolution.virtual_assistant.ui.phone_contacts;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtual_assistant.model.PhoneContact;
import com.vnsolution.virtual_assistant.service.device.DeviceManager;

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