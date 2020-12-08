package com.vnsolution.virtualassistant.ui.contact;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.db.entity.PhoneCallEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneCallSummaryEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneContactEntity;
import com.vnsolution.virtualassistant.model.PhoneCall;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.PhoneContactSummary;
import com.vnsolution.virtualassistant.service.device.DeviceManager;
import com.vnsolution.virtualassistant.util.MappingUtils;
import com.vnsolution.virtualassistant.util.SerialUtils;

import java.util.ArrayList;
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

    public List<PhoneContact> getPhoneContacts() {
        List<PhoneContactEntity> phoneContacts = deviceManager.getPhoneContacts();
        return MappingUtils.convertPhoneContactEntitiesToPhoneContacts(phoneContacts);
    }

    public PhoneContact getPhoneContactById(String id) {
        PhoneContactEntity contactEntity = deviceManager.getContactById(id);
        return MappingUtils.convertPhoneContactEntityToPhoneContact(contactEntity);
    }

    public PhoneCall getPhoneCallById(String id) {
        PhoneCallEntity phoneCallEntity = deviceManager.getPhoneCallById(id);
        return MappingUtils.convertPhoneCallEntityToPhoneCall(phoneCallEntity);
    }

    public List<PhoneContactSummary> getContactSummaryByNumbers(String phoneNumbers) {
        List<PhoneCallSummaryEntity> contactSummary = deviceManager.getContactSummaryByNumbers(phoneNumbers);
        return MappingUtils.convertPhoneCallSummaryEntitiesToPhoneContacts(contactSummary);
    }

    public List<PhoneCall> getRecentCallsByType(int type) {
        List<PhoneCallEntity> recentPhoneCalls = deviceManager.getRecentCallsByType(type);
        return MappingUtils.convertPhoneCallEntitiesToPhoneCalls(recentPhoneCalls);
    }

    public List<PhoneCall> getMostCallsByType(int type) {
        List<PhoneCallEntity> mostPhoneCalls = deviceManager.getMostCallsByType(type);
        return MappingUtils.convertPhoneCallEntitiesToPhoneCalls(mostPhoneCalls);
    }

    public List<PhoneCall> getCallHistoryByNumbers(String phoneNumbers) {
        List<PhoneCallEntity> phoneCalls = deviceManager.getCallHistoryByNumbers(phoneNumbers);
        return MappingUtils.convertPhoneCallEntitiesToPhoneCalls(phoneCalls);
    }

    public void assignContactToPhoneCalls(List<PhoneCall> phoneCalls, List<PhoneContact> phoneContacts) {
        if (phoneCalls != null) {
            for (PhoneCall phoneCall : phoneCalls) {
                PhoneContact existingPhoneContact = null;
                if (phoneContacts != null) {
                    for (PhoneContact phoneContact : phoneContacts) {
                        if (phoneContact.phoneNumbers != null && phoneContact.phoneNumbers.contains(phoneCall.phoneNumber)) {
                            existingPhoneContact = (PhoneContact) SerialUtils.cloneObject(phoneContact);
                            break;
                        }
                    }
                }
                existingPhoneContact = (existingPhoneContact == null) ? new PhoneContact() : existingPhoneContact;
                existingPhoneContact.phoneNumbers = phoneCall.phoneNumber;
                phoneCall.contactRef = existingPhoneContact;
            }
        }
    }
}