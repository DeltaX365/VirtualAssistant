package com.vnsolution.virtualassistant.service.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.ArrayMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.vnsolution.virtualassistant.db.dao.PhoneCallDao;
import com.vnsolution.virtualassistant.db.dao.PhoneAppDao;
import com.vnsolution.virtualassistant.db.dao.PhoneContactDao;
import com.vnsolution.virtualassistant.db.dao.PhoneSmsDao;
import com.vnsolution.virtualassistant.db.entity.PhoneCallEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneCallSummaryEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneContactEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneSmsEntity;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.model.PhoneCall;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.PhoneSms;
import com.vnsolution.virtualassistant.util.MappingUtils;
import com.vnsolution.virtualassistant.util.PermissionUtils;
import com.vnsolution.virtualassistant.util.PhoneUtils;
import com.vnsolution.virtualassistant.util.SerialUtils;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

public class DeviceManager {
    private static final String SMS_INBOX = "content://sms/inbox";
    private static final String SMS_SENT = "content://sms/sent";
    private static final String SMS_DRAFT = "content://sms/draft";
    private static DeviceManager instance;

    private ArrayMap<String, UsageStats> appUsageStats;
    private MutableLiveData<List<PhoneApp>> phoneAppsLiveData;
    private MutableLiveData<List<PhoneCall>> phoneCallsLiveData;
    private MutableLiveData<List<PhoneSms>> phoneSmsLiveData;
    private MutableLiveData<List<PhoneContact>> phoneContactsLiveData;

    @Inject
    public PhoneCallDao phoneCallDao;

    @Inject
    public PhoneSmsDao phoneSmsDao;

    @Inject
    public PhoneContactDao phoneContactDao;

    @Inject
    public PhoneAppDao phoneAppDao;

    @Inject
    public DeviceManager() {}

    public static synchronized DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    public PhoneContactEntity getContactById(String id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<PhoneContactEntity> phoneContactFuture = executor.submit(new Callable<PhoneContactEntity>() {
            @Override
            public PhoneContactEntity call() throws Exception {
                return phoneContactDao.getById(id);
            }
        });
        PhoneContactEntity phoneContact = null;
        try {
            phoneContact = phoneContactFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneContact;
    }

    public List<PhoneCallSummaryEntity> getContactSummaryByNumbers(String phoneNumbers) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCallSummaryEntity>> phoneContactSummaryListFuture = executor.submit(new Callable<List<PhoneCallSummaryEntity>>() {
            @Override
            public List<PhoneCallSummaryEntity> call() throws Exception {
                return (phoneNumbers != null && phoneNumbers != "")? phoneCallDao.getCallStatByNumbers(phoneNumbers) : phoneCallDao.getCallStat();
            }
        });
        List<PhoneCallSummaryEntity> phoneContactSummary = new ArrayList<>();
        try {
            phoneContactSummary = phoneContactSummaryListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneContactSummary;
    }

    public List<PhoneContactEntity> getCallHistoryByNumbers(String phoneNumbers) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneContactEntity>> callHistoryFuture = executor.submit(new Callable<List<PhoneContactEntity>>() {
            @Override
            public List<PhoneContactEntity> call() throws Exception {
                List<PhoneCallEntity> callHistory = phoneCallDao.getCallHistoryByNumbers(phoneNumbers);
                List<PhoneContactEntity> phoneContacts = phoneContactDao.getAll();
                return filterPhoneContacts(phoneContacts, callHistory);
            }
        });
        List<PhoneContactEntity> callHistory = new ArrayList<>();
        try {
            callHistory = callHistoryFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return callHistory;
    }

    public List<PhoneContactEntity> getRecentContactsByType(int type) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneContactEntity>> phoneCallListFuture = executor.submit(new Callable<List<PhoneContactEntity>>() {
            @Override
            public List<PhoneContactEntity> call() throws Exception {
                List<PhoneCallEntity> recentPhoneCalls = (type == 0)? phoneCallDao.getRecentCalls() : phoneCallDao.getRecentCallsByType(type);
                List<PhoneContactEntity> phoneContacts = phoneContactDao.getAll();
                return filterPhoneContacts(phoneContacts, recentPhoneCalls);
            }
        });
        List<PhoneContactEntity> phoneCalls = new ArrayList<>();
        try {
            phoneCalls = phoneCallListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneCalls;
    }

    public List<PhoneContactEntity> getMostContactsByType(int type) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneContactEntity>> phoneCallListFuture = executor.submit(new Callable<List<PhoneContactEntity>>() {
            @Override
            public List<PhoneContactEntity> call() throws Exception {
                List<PhoneCallEntity> mostPhoneCalls = (type == 0) ? phoneCallDao.getMostCalls() : phoneCallDao.getMostCallsByType(type);
                List<PhoneContactEntity> phoneContacts = phoneContactDao.getAll();
                return filterPhoneContacts(phoneContacts, mostPhoneCalls);
            }
        });
        List<PhoneContactEntity> phoneCalls = new ArrayList<>();
        try {
            phoneCalls = phoneCallListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneCalls;
    }

    private List<PhoneContactEntity> filterPhoneContacts(List<PhoneContactEntity> phoneContacts, List<PhoneCallEntity> phoneCalls) {
        List<PhoneContactEntity> filteredResult = new ArrayList<>();
        if (phoneCalls != null) {
            for (PhoneCallEntity phoneCall : phoneCalls) {
                PhoneContactEntity existingPhoneContact = null;
                if (phoneContacts != null) {
                    for (PhoneContactEntity phoneContact : phoneContacts) {
                        if (phoneContact.phoneNumbers != null && phoneContact.phoneNumbers.contains(phoneCall.phoneNumber)) {
                            existingPhoneContact = (PhoneContactEntity) SerialUtils.cloneObject(phoneContact);
                            break;
                        }
                    }
                }
                existingPhoneContact = (existingPhoneContact == null) ? new PhoneContactEntity() : existingPhoneContact;
                existingPhoneContact.phoneNumbers = phoneCall.phoneNumber;
                filteredResult.add(existingPhoneContact);
            }
        }
        return filteredResult;
    }

    public MutableLiveData<List<PhoneApp>> getPhoneApps(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        if (phoneAppsLiveData == null) {
            phoneAppsLiveData = new MutableLiveData<>();
            getPhoneAppsAsync(context, shouldGetSystemPackages, forceUpdate);
        }
        return phoneAppsLiveData;
    }

    public MutableLiveData<List<PhoneCall>> getPhoneCalls(Context context, boolean forceUpdate) {
        if (phoneCallsLiveData == null) {
            phoneCallsLiveData = new MutableLiveData<>();
            getPhoneCallsAsync(context, forceUpdate);
        }
        return phoneCallsLiveData;
    }

    public LiveData<List<PhoneSms>> getPhoneSms(Context context, boolean forceUpdate) {
        if (phoneSmsLiveData == null) {
            phoneSmsLiveData = new MutableLiveData<>();
            getPhoneSmsAsync(context, forceUpdate);
        }
        return phoneSmsLiveData;
    }

    public LiveData<List<PhoneContact>> getPhoneContacts(Context context, boolean forceUpdate) {
        if (phoneContactsLiveData == null) {
            phoneContactsLiveData = new MutableLiveData<>();
            getPhoneContactsAsync(context, forceUpdate);
        }
        return phoneContactsLiveData;
    }

    public void getPhoneAppsAsync(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneApp>> phoneAppListFuture = executor.submit(new Callable<List<PhoneApp>>() {
            @Override
            public List<PhoneApp> call() throws Exception {
                List<PhoneApp> phoneApps = new ArrayList<>();
                if (context != null) {
                    getPhoneAppUsageStats(context);
                    List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
//                    Collections.sort(packageInfos, new Comparator<PackageInfo>() {
//                        @Override
//                        public int compare(PackageInfo a1, PackageInfo a2) {
//                            return a1.applicationInfo.loadLabel(context.getPackageManager()).toString().compareTo(a2.applicationInfo.loadLabel(context.getPackageManager()).toString());
//                        }
//                    });
                    for (PackageInfo packageInfo : packageInfos) {
                        if (!shouldGetSystemPackages && isSystemPackage(packageInfo)) {
                            continue;
                        }
                        PhoneApp application = new PhoneApp();
                        application.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                        application.packageName = packageInfo.packageName;
                        application.versionCode = packageInfo.versionCode;
                        application.versionName = packageInfo.versionName;
                        application.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                        application.usageStats = appUsageStats.get(application.packageName);
                        phoneApps.add(application);
                    }
                }
                return phoneApps;
            }
        });
        List<PhoneApp> phoneApps = new ArrayList<>();
        try {
            phoneApps = phoneAppListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        if (phoneAppsLiveData != null) {
            phoneAppsLiveData.setValue(phoneApps);
        }
    }

    public void getPhoneAppUsageStats(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && context != null && (appUsageStats == null || appUsageStats.isEmpty())) {

            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, calendar.getTimeInMillis(), System.currentTimeMillis());
            if (usageStats.isEmpty()) {
                context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }

            appUsageStats = new ArrayMap<>();
            for (UsageStats usageStat: usageStats) {
                UsageStats existingStat = appUsageStats.get(usageStat.getPackageName());
                if (existingStat == null) {
                    appUsageStats.put(usageStat.getPackageName(), usageStat);
                } else {
                    existingStat.add(usageStat);
                }
            }
        }
    }

    public List<PhoneCall> getPhoneCallsAsync(Context context, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCall>> phoneCallListFuture = executor.submit(new Callable<List<PhoneCall>>() {
            @Override
            public List<PhoneCall> call() throws Exception {
                if (forceUpdate) {
                    syncPhoneCalls(context);
                }
                List<PhoneCallEntity> phoneCallEntities = phoneCallDao.getAll();
                return transformToPhoneCall(phoneCallEntities);
            }
        });
        List<PhoneCall> phoneCalls = new ArrayList<>();
        try {
            phoneCalls = phoneCallListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        if (phoneCallsLiveData != null) {
            phoneCallsLiveData.setValue(phoneCalls);
        }
        return phoneCalls;
    }

    public void syncPhoneCalls(Context context) {
        List<PhoneCallEntity> phoneCalls = new ArrayList<>();
        if (context != null && PermissionUtils.hasPermission(context, Manifest.permission.READ_CALL_LOG)) {
            @SuppressLint("MissingPermission") Cursor cursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
            while (cursor.moveToNext()) {
                PhoneCallEntity phoneCallEntity = getPhoneCallEntity(cursor);
                phoneCalls.add(phoneCallEntity);
            }
            cursor.close();
        }
        phoneCallDao.insert(phoneCalls);
    }

    public List<PhoneSms> getPhoneSmsAsync(Context context, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneSms>> phoneSmsListFuture = executor.submit(new Callable<List<PhoneSms>>() {
            @Override
            public List<PhoneSms> call() throws Exception {
                if (forceUpdate) {
                    syncPhoneSms(context);
                }
                List<PhoneSmsEntity> phoneSmsEntities = phoneSmsDao.getAll();
                return transformToPhoneSms(phoneSmsEntities);
            }
        });
        List<PhoneSms> phoneSmsList = new ArrayList<>();
        try {
            phoneSmsList = phoneSmsListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        if (phoneSmsLiveData != null) {
            phoneSmsLiveData.setValue(phoneSmsList);
        }
        return phoneSmsList;
    }

    public void syncPhoneSms(Context context) {
        List<PhoneSmsEntity> phoneSmsList = new ArrayList<>();
        if (context != null && PermissionUtils.hasPermission(context, Manifest.permission.READ_SMS)) {
            Cursor cursor = context.getContentResolver().query(Uri.parse(SMS_INBOX), null, null, null, null);
            if (cursor.moveToNext()) {
                do {
                    PhoneSmsEntity phoneSmsEntity = getSmsHistoryEntity(cursor);
                    phoneSmsList.add(phoneSmsEntity);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        phoneSmsDao.insert(phoneSmsList);
    }

    public void getPhoneContactsAsync(Context context, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneContact>> phoneContactListFuture = executor.submit(new Callable<List<PhoneContact>>() {
            @Override
            public List<PhoneContact> call() throws Exception {
                if (forceUpdate) {
                    syncPhoneContacts(context);
                }
                List<PhoneContactEntity> phoneContactEntities = phoneContactDao.getAll();
                return MappingUtils.convertPhoneContactEntitiesToPhoneContacts(phoneContactEntities);
            }
        });
        List<PhoneContact> phoneContactList = new ArrayList<>();
        try {
            phoneContactList = phoneContactListFuture.get();
            applyPhoneContactStat(context, phoneContactList);
            Collections.sort(phoneContactList, new Comparator<PhoneContact>() {
                @Override
                public int compare(PhoneContact o1, PhoneContact o2) {
                    return o1.name.compareTo(o2.name);
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        if (phoneContactsLiveData != null) {
            phoneContactsLiveData.setValue(phoneContactList);
        }
    }

    public void syncPhoneContacts(Context context) {
        List<PhoneContactEntity> phoneContacts = new ArrayList<>();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.moveToNext()) {
                do {
                    PhoneContactEntity phoneContact = getPhoneContactEntity(cursor);
                    if (phoneContact.hasPhoneNumber > 0) {
                        phoneContact.phoneNumbers = "";
                        Cursor cursor1 = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{ String.valueOf(phoneContact.contactId) },
                                null);
                        List<String> phoneNumbers = new ArrayList<>();
                        while (cursor1.moveToNext()) {
                            String phoneNo = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNumbers.add(phoneNo);
                        }
                        phoneContact.phoneNumbers = StringUtils.join(",", phoneNumbers);
                        cursor1.close();
                    }
                    phoneContacts.add(phoneContact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        phoneContactDao.insert(phoneContacts);
    }

    private void applyPhoneContactStat(Context context, List<PhoneContact> contactList) {
        List<PhoneCall> callList = getPhoneCallsAsync(context, true);
        List<PhoneSms> smsList = getPhoneSmsAsync(context, true);
        for (PhoneContact phoneContact : contactList) {
            int totalIncomeCall = 0;
            int totalOutcomeCall = 0;
            int totalSms = 0;
            for (PhoneCall phoneCall : callList) {
                if (phoneContact.phoneNumbers.contains(phoneCall.phoneNumber)) {
                    if (phoneCall.type == android.provider.CallLog.Calls.INCOMING_TYPE) {
                        totalIncomeCall++;
                    } else if (phoneCall.type == CallLog.Calls.OUTGOING_TYPE) {
                        totalOutcomeCall++;
                    }
                }
            }
            for (PhoneSms phoneSms : smsList) {
                String phoneNumber = PhoneUtils.convertInternationalToNational(phoneSms.address);
                if (phoneContact.phoneNumbers.contains(phoneNumber)) {
                    totalSms++;
                }
            }
            phoneContact.totalIncomingNumber = totalIncomeCall;
            phoneContact.totalOutgoingNumber = totalOutcomeCall;
            phoneContact.totalSms = totalSms;
        }
    }

    private PhoneCallEntity getPhoneCallEntity(Cursor cursor) {
        PhoneCallEntity call = new PhoneCallEntity();
        call.id = UUID.randomUUID().toString();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getColumnName(i)) {
                case CallLog.Calls._ID:
                    call.callId = cursor.getInt(i);
                    break;
                case CallLog.Calls.NUMBER:
                    call.phoneNumber = cursor.getString(i);
                    break;
                case CallLog.Calls.TYPE:
                    call.type = cursor.getInt(i);
                    break;
                case CallLog.Calls.GEOCODED_LOCATION:
                    call.geocodedLocation = cursor.getString(i);
                    break;
                case CallLog.Calls.NUMBER_PRESENTATION:
                    call.presentation = cursor.getInt(i);
                    break;
                case CallLog.Calls.DURATION:
                    call.duration = cursor.getLong(i);
                    break;
                case CallLog.Calls.IS_READ:
                    call.isRead = cursor.getInt(i);
                    break;
                case CallLog.Calls.DATE:
                    call.date = cursor.getLong(i);
                    break;
                case CallLog.Calls.FEATURES:
                    call.features = cursor.getInt(i);
                    break;
                case CallLog.Calls.CACHED_NORMALIZED_NUMBER:
                    call.normalizedNumber = cursor.getString(i);
                    break;
                case CallLog.Calls.LAST_MODIFIED:
                    call.lastModified = cursor.getLong(i);
                    break;
                case CallLog.Calls.NEW:
                    call.isNew = cursor.getInt(i);
                    break;
                case CallLog.Calls.CACHED_FORMATTED_NUMBER:
                    call.formattedNumber = cursor.getString(i);
                    break;
                case CallLog.Calls.BLOCK_REASON:
                    call.blockReason = cursor.getInt(i);
                    break;
                case CallLog.Calls.COUNTRY_ISO:
                    call.countryIso = cursor.getString(i);
                    break;
            }
        }
        return call;
    }

    private List<PhoneCall> transformToPhoneCall(List<PhoneCallEntity> phoneCallEntities) {
        List<PhoneCall> phoneCalls = new ArrayList<>();
        if (phoneCallEntities != null && phoneCallEntities.size() > 0) {
            for (PhoneCallEntity phoneCallEntity : phoneCallEntities) {
                PhoneCall phoneCall = new PhoneCall();
                phoneCall.phoneNumber = phoneCallEntity.phoneNumber;
                phoneCall.type = phoneCallEntity.type;
                phoneCall.duration = phoneCallEntity.duration;
                phoneCall.date = phoneCallEntity.date;
                phoneCalls.add(phoneCall);
            }
        }
        return phoneCalls;
    }

    private PhoneSmsEntity getSmsHistoryEntity(Cursor cursor) {
        PhoneSmsEntity sms = new PhoneSmsEntity();
        sms.id = UUID.randomUUID().toString();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getColumnName(i)) {
                case "_id":
                    sms.smsId = cursor.getInt(i);
                    break;
                case "thread_id":
                    sms.threadId = cursor.getInt(i);
                    break;
                case "address":
                    sms.address = cursor.getString(i);
                    break;
                case "date":
                    sms.date = cursor.getLong(i);
                    break;
                case "date_sent":
                    sms.dateSent = cursor.getLong(i);
                    break;
                case "read":
                    sms.read = cursor.getInt(i);
                    break;
                case "type":
                    sms.type = cursor.getInt(i);
                    break;
                case "subject":
                    sms.subject = cursor.getString(i);
                    break;
                case "body":
                    sms.body = cursor.getString(i);
                    break;
                case "service_center":
                    sms.serviceCenter = cursor.getString(i);
                    break;
                case "locked":
                    sms.locked = cursor.getInt(i);
                    break;
                case "sub_id":
                    sms.subId = cursor.getInt(i);
                    break;
                case "creator":
                    sms.creator = cursor.getString(i);
                    break;
                case "seen":
                    sms.seen = cursor.getInt(i);
                    break;
                case "deletable":
                    sms.deletable = cursor.getInt(i);
                    break;
            }
        }
        return sms;
    }

    private List<PhoneSms> transformToPhoneSms(List<PhoneSmsEntity> smsHistoryEntities) {
        List<PhoneSms> phoneSmsHistories = new ArrayList<>();
        if (smsHistoryEntities != null && smsHistoryEntities.size() > 0) {
            for (PhoneSmsEntity phoneSmsEntity : smsHistoryEntities) {
                PhoneSms phoneSms = new PhoneSms();
                phoneSms.smsId = phoneSmsEntity.smsId;
                phoneSms.address = phoneSmsEntity.address;
                phoneSms.body = phoneSmsEntity.body;
                phoneSms.dateSent = phoneSmsEntity.dateSent;
                phoneSmsHistories.add(phoneSms);
            }
        }
        return phoneSmsHistories;
    }

    private PhoneContactEntity getPhoneContactEntity(Cursor cursor) {
        PhoneContactEntity contact = new PhoneContactEntity();
        contact.id = UUID.randomUUID().toString();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getColumnName(i)) {
                case ContactsContract.Contacts._ID:
                    contact.contactId = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.DISPLAY_NAME:
                    contact.displayName = cursor.getString(i);
                    break;
                case ContactsContract.Contacts.PHONETIC_NAME:
                    contact.phoneticName = cursor.getString(i);
                    break;
                case ContactsContract.Contacts.PINNED:
                    contact.pinned = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE:
                    contact.displayNameAlt = cursor.getString(i);
                    break;
                case ContactsContract.Contacts.SORT_KEY_ALTERNATIVE:
                    contact.sortKeyAlt = cursor.getString(i);
                    break;
                case ContactsContract.Contacts.IN_VISIBLE_GROUP:
                    contact.inVisibleGroup = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.STARRED:
                    contact.starred = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.CONTACT_STATUS_LABEL:
                    contact.contactStatusLabel = cursor.getString(i);
                    break;
                case ContactsContract.Contacts.IS_USER_PROFILE:
                    contact.isUserProfile = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.HAS_PHONE_NUMBER:
                    contact.hasPhoneNumber = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.DISPLAY_NAME_SOURCE:
                    contact.displayNameSource = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.PHONETIC_NAME_STYLE:
                    contact.phoneticNameStyle = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.LOOKUP_KEY:
                    contact.lookup = cursor.getString(i);
                    break;
                case ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP:
                    contact.contactLastUpdatedTimestamp = cursor.getLong(i);
                    break;
                case ContactsContract.Contacts.PHOTO_ID:
                    contact.photoUri = cursor.getString(i);
                    break;
                case ContactsContract.Contacts.SORT_KEY_PRIMARY:
                    contact.sortKey = cursor.getString(i);
                    break;
                case ContactsContract.Contacts.IN_DEFAULT_DIRECTORY:
                    contact.inDefaultDirectory = cursor.getInt(i);
                    break;
                case ContactsContract.Contacts.NAME_RAW_CONTACT_ID:
                    contact.nameRawContactId = cursor.getInt(i);
                    break;
            }
        }
//        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contact.id));
//        contact.photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        return contact;
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo != null) && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0));
    }
}
