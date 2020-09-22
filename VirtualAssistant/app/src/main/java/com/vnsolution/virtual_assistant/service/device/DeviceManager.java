package com.vnsolution.virtual_assistant.service.device;

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
import com.vnsolution.virtual_assistant.db.dao.PhoneCallDao;
import com.vnsolution.virtual_assistant.db.dao.PhoneAppDao;
import com.vnsolution.virtual_assistant.db.dao.PhoneContactDao;
import com.vnsolution.virtual_assistant.db.dao.PhoneSmsDao;
import com.vnsolution.virtual_assistant.db.entity.PhoneCallEntity;
import com.vnsolution.virtual_assistant.db.entity.PhoneContactEntity;
import com.vnsolution.virtual_assistant.db.entity.PhoneSmsEntity;
import com.vnsolution.virtual_assistant.model.PhoneApp;
import com.vnsolution.virtual_assistant.model.PhoneCall;
import com.vnsolution.virtual_assistant.model.PhoneContact;
import com.vnsolution.virtual_assistant.model.PhoneSms;
import com.vnsolution.virtual_assistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.Calendar;
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

    public void getTotalInstalledApps() {}

    public void getTop10RecentUsedApps() {}

    public void getTop10MostUsedApps() {}

    public void getTop10FavouriteUsedApps() {}

    public void getTop10UnusedApps() {}

    public void favouriteApp(PhoneApp application) {}

    public void categorizeApp(PhoneApp application) {}

    public void getTotalCalls() {}

    public void getTotalPhones() {}

    public void getTotalTime() {}

    public void getTop10RecentCalls() {}

    public void getTop10MostCalls() {}

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

    private void getPhoneAppsAsync(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
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
        phoneAppsLiveData.setValue(phoneApps);
    }

    private void getPhoneAppUsageStats(Context context) {
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

    private void getPhoneCallsAsync(Context context, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCall>> phoneCallListFuture =  executor.submit(new Callable<List<PhoneCall>>() {
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
        phoneCallsLiveData.setValue(phoneCalls);
    }

    private void syncPhoneCalls(Context context) {
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

    private void getPhoneSmsAsync(Context context, boolean forceUpdate) {
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
        phoneSmsLiveData.setValue(phoneSmsList);
    }

    private void syncPhoneSms(Context context) {
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

    private void getPhoneContactsAsync(Context context, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneContact>> phoneContactListFuture = executor.submit(new Callable<List<PhoneContact>>() {
            @Override
            public List<PhoneContact> call() throws Exception {
                if (forceUpdate) {
                    syncPhoneContacts(context);
                }
                List<PhoneContactEntity> phoneContactEntities = phoneContactDao.getAll();
                return transformToPhoneContacts(phoneContactEntities);
            }
        });
        List<PhoneContact> phoneContactList = new ArrayList<>();
        try {
            phoneContactList = phoneContactListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        phoneContactsLiveData.setValue(phoneContactList);
    }

    private void syncPhoneContacts(Context context) {
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
                        while (cursor1.moveToNext()) {
                            String phoneNo = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneContact.phoneNumbers += phoneNo + ", ";
                        }
                        cursor1.close();
                    }
                    phoneContacts.add(phoneContact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        phoneContactDao.insert(phoneContacts);
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

    private List<PhoneContact> transformToPhoneContacts(List<PhoneContactEntity> phoneContactEntities) {
        List<PhoneContact> phoneContacts = new ArrayList<>();
        if (phoneContactEntities != null && phoneContactEntities.size() > 0) {
            for (PhoneContactEntity phoneContactEntity : phoneContactEntities) {
                PhoneContact phoneContact = new PhoneContact();
                phoneContact.contactId = phoneContactEntity.contactId;
                phoneContact.name = phoneContactEntity.displayName;
                phoneContact.phoneNumbers = phoneContactEntity.phoneNumbers;
                phoneContacts.add(phoneContact);
            }
        }
        return phoneContacts;
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo != null) && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0));
    }
}
