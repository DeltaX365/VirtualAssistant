package com.vnsolution.virtualassistant.util;

import android.location.Address;

import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDetailDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceSearchDto;
import com.vnsolution.virtualassistant.db.entity.PhoneAppEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneCallEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneCallSummaryEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneContactEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneSmsEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.ReminderEntity;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.model.PhoneCall;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.PhoneContactSummary;
import com.vnsolution.virtualassistant.model.PhoneSms;
import com.vnsolution.virtualassistant.model.Reminder;
import com.vnsolution.virtualassistant.model.StopPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MappingUtils {
    public static PlaceEntity convertStopPointToPlaceEntity(StopPoint stopPoint) {
        PlaceEntity placeEntity = null;
        if (stopPoint != null) {
            placeEntity = new PlaceEntity();
            placeEntity.id = (stopPoint.placeId != null && stopPoint.placeId != "") ? stopPoint.placeId : UUID.randomUUID().toString();
            placeEntity.latitude = stopPoint.latitude;
            placeEntity.longitude = stopPoint.longitude;
            placeEntity.synced = 0;
            placeEntity.createdDate = new Date();
        }
        return placeEntity;
    }

    public static PlaceEntity convertAddressToPlaceEntity(Address address) {
        PlaceEntity placeEntity = null;
        if (address != null) {
            placeEntity = new PlaceEntity();
            placeEntity.id = UUID.randomUUID().toString();
            placeEntity.placeName = address.getFeatureName();
            placeEntity.formattedAddress = address.getAddressLine(0);
            double longitude = Math.round(address.getLongitude() * Math.pow(10, 7)) / Math.pow(10, 7);
            double latitude = Math.round(address.getLatitude() * Math.pow(10, 7)) / Math.pow(10, 7);
            placeEntity.latitude = latitude;
            placeEntity.longitude = longitude;
            placeEntity.synced = 0;
            placeEntity.createdDate = new Date();
        }
        return placeEntity;
    }

    public static PlaceEntity convertPlaceSearchDtoToPlaceEntity(GoogleMapPlaceSearchDto.PlaceSearch candidate) {
        PlaceEntity placeEntity = null;
        if (candidate != null) {
            placeEntity = new PlaceEntity();
            placeEntity.id = UUID.randomUUID().toString();
            placeEntity.ggPlaceId = candidate.placeId;
            placeEntity.placeName = candidate.name;
            placeEntity.formattedAddress = candidate.formattedAddress;
            double latitude = (candidate.geometry != null && candidate.geometry.location != null) ? candidate.geometry.location.lat : 0;
            double longitude = (candidate.geometry != null && candidate.geometry.location != null) ? candidate.geometry.location.lng : 0;
            latitude = Math.round(latitude * Math.pow(10, 7)) / Math.pow(10, 7);
            longitude = Math.round(longitude * Math.pow(10, 7)) / Math.pow(10, 7);
            placeEntity.latitude = latitude;
            placeEntity.longitude = longitude;
            placeEntity.plusCodeCompound = (candidate.plusCode != null) ? candidate.plusCode.compoundCode : "";
            placeEntity.plusCodeGlobal = (candidate.plusCode != null) ? candidate.plusCode.globalCode : "";
            placeEntity.types = StringUtils.join(", ", candidate.types);
            placeEntity.icon = candidate.icon;
            placeEntity.rating = (int) candidate.rating;
            placeEntity.photoReference = (candidate.photos != null && candidate.photos.size() > 0) ? candidate.photos.get(0).photoReference : "";

            if (candidate.formattedAddress != null && !candidate.formattedAddress.isEmpty()) {
                String[] splittedAddress = candidate.formattedAddress.split(",");
                if (splittedAddress.length == 5) {
                    placeEntity.route = splittedAddress[0];
                    placeEntity.administrativeAreaLevel3 = splittedAddress[1];
                    placeEntity.administrativeAreaLevel2 = splittedAddress[2];
                    placeEntity.administrativeAreaLevel1 = splittedAddress[3];
                    placeEntity.country = splittedAddress[4];
                }
            }

            placeEntity.synced = 1;
            placeEntity.createdDate = new Date();
        }
        return placeEntity;
    }

    public static PlaceEntity convertNearByDtoToPlaceEntity(GoogleMapNearByDto.NearBy nearBy) {
        PlaceEntity placeEntity = null;
        if (nearBy != null) {
            placeEntity = new PlaceEntity();
            placeEntity.id = UUID.randomUUID().toString();
            placeEntity.ggPlaceId = nearBy.placeId;
            placeEntity.placeName = nearBy.name;
            placeEntity.formattedAddress = nearBy.vicinity;
            double latitude = (nearBy.geometry != null && nearBy.geometry.location != null) ? nearBy.geometry.location.lat : 0;
            double longitude = (nearBy.geometry != null && nearBy.geometry.location != null) ? nearBy.geometry.location.lng : 0;
            latitude = Math.round(latitude * Math.pow(10, 7)) / Math.pow(10, 7);
            longitude = Math.round(longitude * Math.pow(10, 7)) / Math.pow(10, 7);
            placeEntity.latitude = latitude;
            placeEntity.longitude = longitude;
            placeEntity.plusCodeCompound = (nearBy.plusCode != null) ? nearBy.plusCode.compoundCode : "";
            placeEntity.plusCodeGlobal = (nearBy.plusCode != null) ? nearBy.plusCode.globalCode : "";
            placeEntity.types = StringUtils.join(", ", nearBy.types);
            placeEntity.icon = nearBy.icon;
            placeEntity.reference = nearBy.reference;
            placeEntity.scope = nearBy.scope;
            placeEntity.userRatingsTotal = (int) nearBy.userRatingsTotal;
            placeEntity.vicinity = nearBy.vicinity;
            placeEntity.photoReference = (nearBy.photos != null && nearBy.photos.size() > 0) ? nearBy.photos.get(0).photoReference : "";

            if (nearBy.vicinity != null && !nearBy.vicinity.isEmpty()) {
                String[] splittedAddress = nearBy.vicinity.split(",");
                if (splittedAddress.length == 5) {
                    placeEntity.route = splittedAddress[0];
                    placeEntity.administrativeAreaLevel3 = splittedAddress[1];
                    placeEntity.administrativeAreaLevel2 = splittedAddress[2];
                    placeEntity.administrativeAreaLevel1 = splittedAddress[3];
                    placeEntity.country = splittedAddress[4];
                }
            }

            placeEntity.createdDate = new Date();
        }
        return placeEntity;
    }

    public static PlaceEntity convertPlaceDetailToPlaceEntity(GoogleMapPlaceDetailDto.PlaceDetail placeDetail) {
        PlaceEntity placeEntity = null;
        if (placeDetail != null) {
            placeEntity = new PlaceEntity();
            placeEntity.id = UUID.randomUUID().toString();
            placeEntity.ggPlaceId = placeDetail.placeId;
            placeEntity.placeName = placeDetail.name;
            placeEntity.formattedAddress = placeDetail.formattedAddress;
            double latitude = (placeDetail.geometry != null && placeDetail.geometry.location != null) ? placeDetail.geometry.location.lat : 0;
            double longitude = (placeDetail.geometry != null && placeDetail.geometry.location != null) ? placeDetail.geometry.location.lng : 0;
            latitude = Math.round(latitude * Math.pow(10, 7)) / Math.pow(10, 7);
            longitude = Math.round(longitude * Math.pow(10, 7)) / Math.pow(10, 7);
            placeEntity.latitude = latitude;
            placeEntity.longitude = longitude;
            placeEntity.plusCodeCompound = (placeDetail.plusCode != null) ? placeDetail.plusCode.compoundCode : "";
            placeEntity.plusCodeGlobal = (placeDetail.plusCode != null) ? placeDetail.plusCode.globalCode : "";
            placeEntity.types = StringUtils.join(", ", placeDetail.types);
            placeEntity.icon = placeDetail.icon;
            placeEntity.rating = (int) placeDetail.rating;
            placeEntity.reference = placeDetail.reference;
            placeEntity.scope = placeDetail.scope;
            placeEntity.url = placeDetail.url;
            placeEntity.userRatingsTotal = (int) placeDetail.userRatingsTotal;
            placeEntity.utcOffset = String.valueOf(placeDetail.utcOffset);
            placeEntity.vicinity = placeDetail.vicinity;
            placeEntity.photoReference = (placeDetail.photos != null && placeDetail.photos.size() > 0) ? placeDetail.photos.get(0).photoReference : "";

            if (placeDetail.formattedAddress != null && !placeDetail.formattedAddress.isEmpty()) {
                String[] splittedAddress = placeDetail.formattedAddress.split(",");
                if (splittedAddress.length == 5) {
                    placeEntity.route = splittedAddress[0];
                    placeEntity.administrativeAreaLevel3 = splittedAddress[1];
                    placeEntity.administrativeAreaLevel2 = splittedAddress[2];
                    placeEntity.administrativeAreaLevel1 = splittedAddress[3];
                    placeEntity.country = splittedAddress[4];
                }
            }

            placeEntity.createdDate = new Date();
        }
        return placeEntity;
    }

    public static List<PhoneContact> convertPhoneContactEntitiesToPhoneContacts(List<PhoneContactEntity> phoneContactEntities) {
        List<PhoneContact> phoneContacts = new ArrayList<>();
        for (PhoneContactEntity phoneContactEntity : phoneContactEntities) {
            PhoneContact phoneContact = convertPhoneContactEntityToPhoneContact(phoneContactEntity);
            phoneContacts.add(phoneContact);
        }
        return phoneContacts;
    }

    public static PhoneContact convertPhoneContactEntityToPhoneContact(PhoneContactEntity phoneContactEntity) {
        PhoneContact phoneContact = null;
        if (phoneContactEntity != null) {
            phoneContact = new PhoneContact();
            phoneContact.id = phoneContactEntity.id;
            phoneContact.name = phoneContactEntity.displayName;
            phoneContact.phoneNumbers = phoneContactEntity.phoneNumbers;
            phoneContact.starred = phoneContactEntity.starred;
            phoneContact.lookup = phoneContactEntity.lookup;
            phoneContact.lastUpdatedTime = phoneContactEntity.contactLastUpdatedTimestamp;
        }
        return phoneContact;
    }

    public static List<PhoneCall> convertPhoneCallEntitiesToPhoneCalls(List<PhoneCallEntity> phoneCallEntities) {
        List<PhoneCall> phoneCalls = new ArrayList<>();
        for (PhoneCallEntity phoneCallEntity : phoneCallEntities) {
            PhoneCall phoneCall = convertPhoneCallEntityToPhoneCall(phoneCallEntity);
            phoneCalls.add(phoneCall);
        }
        return phoneCalls;
    }

    public static PhoneCall convertPhoneCallEntityToPhoneCall(PhoneCallEntity phoneCallEntity) {
        PhoneCall phoneCall = null;
        if (phoneCallEntity != null) {
            phoneCall = new PhoneCall();
            phoneCall.id = phoneCallEntity.id;
            phoneCall.phoneNumber = phoneCallEntity.phoneNumber;
            phoneCall.duration = phoneCallEntity.duration;
            phoneCall.type = phoneCallEntity.type;
            phoneCall.date = phoneCallEntity.date;
        }
        return phoneCall;
    }

    public static List<PhoneContactSummary> convertPhoneCallSummaryEntitiesToPhoneContacts(List<PhoneCallSummaryEntity> phoneCallSummaryEntityList) {
        List<PhoneContactSummary> phoneContactSummaryList = new ArrayList<>();
        for (PhoneCallSummaryEntity phoneCallSummaryEntity : phoneCallSummaryEntityList) {
            PhoneContactSummary phoneContactSummary = convertPhoneCallSummaryEntityToPhoneContactSummary(phoneCallSummaryEntity);
            phoneContactSummaryList.add(phoneContactSummary);
        }
        return phoneContactSummaryList;
    }

    public static PhoneContactSummary convertPhoneCallSummaryEntityToPhoneContactSummary(PhoneCallSummaryEntity phoneCallSummaryEntity) {
        PhoneContactSummary phoneContactSummary = null;
        if (phoneCallSummaryEntity != null) {
            phoneContactSummary = new PhoneContactSummary();
            phoneContactSummary.type = phoneCallSummaryEntity.type;
            phoneContactSummary.total = phoneCallSummaryEntity.total;
            phoneContactSummary.duration = phoneCallSummaryEntity.duration;
        }
        return phoneContactSummary;
    }

    public static List<PhoneSms> convertPhoneSmsEntitiesToPhoneSmses(List<PhoneSmsEntity> phoneSmsEntities) {
        List<PhoneSms> phoneSmsList = new ArrayList<>();
        for (PhoneSmsEntity phoneSmsEntity : phoneSmsEntities) {
            PhoneSms phoneSms = convertPhoneSmsEntityToPhoneSms(phoneSmsEntity);
            phoneSmsList.add(phoneSms);
        }
        return phoneSmsList;
    }

    public static PhoneSms convertPhoneSmsEntityToPhoneSms(PhoneSmsEntity phoneSmsEntity) {
        PhoneSms phoneSms = null;
        if (phoneSmsEntity != null) {
            phoneSms = new PhoneSms();
            phoneSms.smsId = phoneSmsEntity.smsId;
            phoneSms.address = phoneSmsEntity.address;
            phoneSms.body = phoneSmsEntity.body;
            phoneSms.dateSent = phoneSmsEntity.dateSent;
        }
        return phoneSms;
    }

    public static List<PhoneApp> convertPhoneAppEntitiesToPhoneApps(List<PhoneAppEntity> phoneAppEntities) {
        List<PhoneApp> phoneApps = new ArrayList<>();
        for (PhoneAppEntity phoneAppEntity : phoneAppEntities) {
            PhoneApp phoneApp = convertPhoneAppEntityToPhoneApp(phoneAppEntity);
            phoneApps.add(phoneApp);
        }
        return phoneApps;
    }

    public static PhoneApp convertPhoneAppEntityToPhoneApp(PhoneAppEntity phoneAppEntity) {
        PhoneApp phoneApp = null;
        if (phoneAppEntity != null) {
            phoneApp = new PhoneApp();
            phoneApp.appName = phoneAppEntity.appName;
            phoneApp.packageName = phoneAppEntity.packageName;
            phoneApp.versionName = phoneAppEntity.versionName;
            phoneApp.versionCode = phoneAppEntity.versionCode;
            phoneApp.type = phoneAppEntity.type;
            phoneApp.locked = phoneAppEntity.locked;
        }
        return phoneApp;
    }

    public static List<Reminder> convertReminderEntitiesToReminders(List<ReminderEntity> reminderEntities) {
        List<Reminder> reminders = new ArrayList<>();
        for (ReminderEntity reminderEntity : reminderEntities) {
            Reminder reminder = convertReminderEntityToReminder(reminderEntity);
            reminders.add(reminder);
        }
        return reminders;
    }

    public static Reminder convertReminderEntityToReminder(ReminderEntity reminderEntity) {
        Reminder reminder = null;
        if (reminderEntity != null) {
            reminder = new Reminder();
            reminder.id = reminderEntity.id;
            reminder.name = reminderEntity.name;
            reminder.date = reminderEntity.date;
            reminder.repeat = reminderEntity.repeat;
            reminder.action = reminderEntity.action;
            reminder.extendedData = reminderEntity.extendedData;
            reminder.alarmRequestCode = reminderEntity.alarmRequestCode;
        }
        return reminder;
    }

    public static ReminderEntity convertReminderToReminderEntity(Reminder reminder) {
        ReminderEntity reminderEntity = null;
        if (reminder != null) {
            reminderEntity = new ReminderEntity();
            reminderEntity.id = reminder.id;
            reminderEntity.name = reminder.name;
            reminderEntity.date = reminder.date;
            reminderEntity.repeat = reminder.repeat;
            reminderEntity.action = reminder.action;
            reminderEntity.extendedData = reminder.extendedData;
            reminderEntity.alarmRequestCode = reminder.alarmRequestCode;
        }
        return reminderEntity;
    }
}
