package com.vnsolution.virtualassistant.ui.contacts;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.app.LoaderManager;

import com.vnsolution.virtualassistant.model.Application;

import java.util.List;

public class ContactsViewModel extends ViewModel {
    private static final int DELAY_FETCH_DATA_IN_MILLISECONDS = 1 * 1000; // 1 second
    private static final String[] PROJECTION = {
            ContactsContract.Data._ID,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.DATA2,
            ContactsContract.Data.DATA3,
            ContactsContract.Data.DATA4,
            ContactsContract.Data.DATA5,
            ContactsContract.Data.DATA6,
            ContactsContract.Data.DATA7,
            ContactsContract.Data.DATA8,
            ContactsContract.Data.DATA9,
            ContactsContract.Data.DATA10,
            ContactsContract.Data.DATA11,
            ContactsContract.Data.DATA12,
            ContactsContract.Data.DATA13,
            ContactsContract.Data.DATA14,
            ContactsContract.Data.DATA15
    };
    private static final String SORT_ORDER = ContactsContract.Data.MIMETYPE;
    private static final String SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?";
    private static final int DETAILS_QUERY_ID = 0;
    private Context context;
    private String[] selectionArgs = { "" };
    private String lookupKey;
    private MutableLiveData<List<Application>> contactsLiveData;

    public ContactsViewModel(Context context) {
        this.context = context;
    }

    public MutableLiveData<List<Application>> getContacts(boolean forceUpdate) {
        if (contactsLiveData == null || forceUpdate) {
            contactsLiveData = new MutableLiveData<>();
            getContactsAsync();
        }
        return contactsLiveData;
    }

    public List<Application> getContactsAsync() {
        return null;
    }
}