package com.vnsolution.virtualassistant.ui.contact;

import android.content.Context;
import android.provider.CallLog;

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

import java.util.List;

import javax.inject.Inject;

public class ContactViewModel extends ViewModel {
    @Inject
    public DeviceManager deviceManager;

    @Inject
    public ContactViewModel() {}

    public LiveData<List<PhoneContact>> getContacts(Context context, boolean forceUpdate) {
        return deviceManager.getPhoneContactsAsync(context, forceUpdate);
    }

    public List<PhoneContact> getPhoneContacts(Context context, boolean forceUpdate, boolean includeStat) {
        List<PhoneContactEntity> phoneContactEntityList = deviceManager.getPhoneContacts(context, forceUpdate);
        List<PhoneContact> phoneContactList = MappingUtils.convertPhoneContactEntitiesToPhoneContacts(phoneContactEntityList);
        if (includeStat) {
            for (PhoneContact phoneContact : phoneContactList) {
                List<PhoneContactSummary> phoneContactSummaryList = getContactSummaryByNumbers(phoneContact.phoneNumbers);
                for (PhoneContactSummary phoneContactSummary : phoneContactSummaryList) {
                    switch (phoneContactSummary.type) {
                        case CallLog.Calls.INCOMING_TYPE:
                            phoneContact.totalIncomingNumber = phoneContactSummary.total;
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            phoneContact.totalOutgoingNumber = phoneContactSummary.total;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            phoneContact.totalMissedNumber = phoneContactSummary.total;
                            break;
                    }
                }
            }
        }
        return phoneContactList;
    }

    public PhoneContact getPhoneContactById(String id) {
        PhoneContactEntity contactEntity = deviceManager.getPhoneContactById(id);
        return MappingUtils.convertPhoneContactEntityToPhoneContact(contactEntity);
    }

    public PhoneCall getPhoneCallById(String id) {
        PhoneCallEntity phoneCallEntity = deviceManager.getPhoneCallById(id);
        return MappingUtils.convertPhoneCallEntityToPhoneCall(phoneCallEntity);
    }

    public List<PhoneContactSummary> getContactSummaryByNumbers(String phoneNumbers) {
        List<PhoneCallSummaryEntity> contactSummary = deviceManager.getPhoneCallSummaryByNumbers(phoneNumbers);
        return MappingUtils.convertPhoneCallSummaryEntitiesToPhoneContacts(contactSummary);
    }

    public List<PhoneCall> getRecentCalls(int type, boolean includeStranger) {
        List<PhoneCallEntity> recentPhoneCalls = deviceManager.getRecentCalls(type, includeStranger);
        return MappingUtils.convertPhoneCallEntitiesToPhoneCalls(recentPhoneCalls);
    }

    public List<PhoneCall> getMostCalls(int type, boolean includeStranger) {
        List<PhoneCallEntity> mostPhoneCalls = deviceManager.getMostCalls(type, includeStranger);
        return MappingUtils.convertPhoneCallEntitiesToPhoneCalls(mostPhoneCalls);
    }

    public List<PhoneCall> getCallHistoryByNumbers(String phoneNumbers) {
        List<PhoneCallEntity> phoneCalls = deviceManager.getPhoneCallHistoryByNumbers(phoneNumbers);
        return MappingUtils.convertPhoneCallEntitiesToPhoneCalls(phoneCalls);
    }

    public String exportPhoneContacts(Context context, List<String> phoneContactIds) {
        return deviceManager.exportPhoneContacts(context, phoneContactIds);
    }

    public boolean deletePhoneContacts(Context context, List<String> phoneContactIds) {
        return deviceManager.deletePhoneContacts(context, phoneContactIds);
    }

    public boolean isExternalStorageWritable() {
        return deviceManager.isExternalStorageWritable();
    }

    public boolean isExternalStorageReadable() {
        return deviceManager.isExternalStorageReadable();
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