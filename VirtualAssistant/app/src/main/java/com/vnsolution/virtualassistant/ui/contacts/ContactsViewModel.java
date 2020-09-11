package com.vnsolution.virtualassistant.ui.contacts;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.app.LoaderManager;

import com.vnsolution.virtualassistant.model.Application;
import com.vnsolution.virtualassistant.model.Contact;
import com.vnsolution.virtualassistant.service.devicemanagement.DeviceManager;

import java.util.List;

import javax.inject.Inject;

public class ContactsViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public ContactsViewModel() {}

    public LiveData<List<Contact>> getContacts(Context context, boolean forceUpdate) {
        return deviceManager.getContacts(context, forceUpdate);
    }
}