package com.vnsolution.virtualassistant.ui.contact;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.db.entity.PhoneCallSummaryEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneContactEntity;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.PhoneContactSummary;
import com.vnsolution.virtualassistant.service.device.DeviceManager;
import com.vnsolution.virtualassistant.util.MappingUtils;

import java.util.List;

import javax.inject.Inject;

public class ContactViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public ContactViewModel() {}

    public LiveData<List<PhoneContact>> getContacts(Context context, boolean forceUpdate) {
        return deviceManager.getPhoneContacts(context, forceUpdate);
    }

    public List<PhoneContactSummary> getContactSummary() {
        List<PhoneCallSummaryEntity> contactSummary = deviceManager.getContactSummary();
        return MappingUtils.convertPhoneCallSummaryEntitiesToPhoneContacts(contactSummary);
    }

    public List<PhoneContact> getRecentContactsByType(int type) {
        List<PhoneContactEntity> recentPhoneCalls = deviceManager.getRecentContactsByType(type);
        return MappingUtils.convertPhoneContactEntitiesToPhoneContacts(recentPhoneCalls);
    }

    public List<PhoneContact> getMostContactsByType(int type) {
        List<PhoneContactEntity> mostPhoneCalls = deviceManager.getMostContactsByType(type);
        return MappingUtils.convertPhoneContactEntitiesToPhoneContacts(mostPhoneCalls);
    }
}