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

public class ContactsViewModel extends ViewModel {
    private Context context;

    public ContactsViewModel(Context context) {
        this.context = context;
    }

    public LiveData<List<Contact>> getContacts(boolean forceUpdate) {
        return ((DeviceManager.getInstance()).getContacts(context, forceUpdate));
    }
}