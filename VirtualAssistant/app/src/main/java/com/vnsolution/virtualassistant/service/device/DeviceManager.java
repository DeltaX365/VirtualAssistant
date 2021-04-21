package com.vnsolution.virtualassistant.service.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BlockedNumberContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.ArrayMap;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.vnsolution.virtualassistant.BuildConfig;
import com.vnsolution.virtualassistant.db.dao.PhoneCallDao;
import com.vnsolution.virtualassistant.db.dao.PhoneAppDao;
import com.vnsolution.virtualassistant.db.dao.PhoneContactDao;
import com.vnsolution.virtualassistant.db.dao.PhoneSmsDao;
import com.vnsolution.virtualassistant.db.entity.PhoneAppEntity;
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
import com.vnsolution.virtualassistant.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.FormattedName;

import static android.content.Context.MODE_PRIVATE;

public class DeviceManager {
    private static final String SMS_INBOX = "content://sms/inbox";
    private static final String SMS_SENT = "content://sms/sent";
    private static final String SMS_DRAFT = "content://sms/draft";
    private static DeviceManager instance;

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
    public DeviceManager() {
    }

    public static synchronized DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    public List<PhoneCallSummaryEntity> getPhoneCallSummary() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCallSummaryEntity>> phoneContactSummaryListFuture = executor.submit(new Callable<List<PhoneCallSummaryEntity>>() {
            @Override
            public List<PhoneCallSummaryEntity> call() throws Exception {
                return phoneCallDao.getCallStat();
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

    public List<PhoneCallSummaryEntity> getPhoneCallSummaryByNumbers(String phoneNumbers) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCallSummaryEntity>> phoneContactSummaryListFuture = executor.submit(new Callable<List<PhoneCallSummaryEntity>>() {
            @Override
            public List<PhoneCallSummaryEntity> call() throws Exception {
                return phoneCallDao.getCallStatByNumbers(phoneNumbers);
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

    public List<PhoneCallEntity> getPhoneCallHistoryByNumbers(String phoneNumbers) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCallEntity>> callHistoryFuture = executor.submit(new Callable<List<PhoneCallEntity>>() {
            @Override
            public List<PhoneCallEntity> call() throws Exception {
                return phoneCallDao.getCallHistoryByNumbers(phoneNumbers);
            }
        });
        List<PhoneCallEntity> callHistory = new ArrayList<>();
        try {
            callHistory = callHistoryFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return callHistory;
    }

    public List<PhoneCallEntity> getRecentCalls(int type, boolean includeStranger) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCallEntity>> phoneCallListFuture = executor.submit(new Callable<List<PhoneCallEntity>>() {
            @Override
            public List<PhoneCallEntity> call() throws Exception {
                List<PhoneCallEntity> recentPhoneCalls = new ArrayList<>();
                if (type == 0 && includeStranger) {
                    recentPhoneCalls = phoneCallDao.getRecentCalls();
                } else if (type == 0 && !includeStranger) {
                    recentPhoneCalls = phoneCallDao.getRecentAuthorizedCalls();
                } else if (type > 0 && includeStranger) {
                    recentPhoneCalls = phoneCallDao.getRecentCalls(type);
                } else {
                    recentPhoneCalls = phoneCallDao.getRecentAuthorizedCalls(type);
                }
                return recentPhoneCalls;
            }
        });
        List<PhoneCallEntity> phoneCalls = new ArrayList<>();
        try {
            phoneCalls = phoneCallListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneCalls;
    }

    public List<PhoneCallEntity> getMostCalls(int type, boolean includeStranger) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCallEntity>> phoneCallListFuture = executor.submit(new Callable<List<PhoneCallEntity>>() {
            @Override
            public List<PhoneCallEntity> call() throws Exception {
                List<PhoneCallEntity> mostPhoneCalls = new ArrayList<>();
                if (type == 0 && includeStranger) {
                    mostPhoneCalls = phoneCallDao.getMostCalls();
                } else if (type == 0 && !includeStranger) {
                    mostPhoneCalls = phoneCallDao.getMostAuthorizedCalls();
                } else if (type > 0 && includeStranger) {
                    mostPhoneCalls = phoneCallDao.getMostCalls(type);
                } else {
                    mostPhoneCalls = phoneCallDao.getMostAuthorizedCalls(type);
                }
                return mostPhoneCalls;
            }
        });
        List<PhoneCallEntity> phoneCalls = new ArrayList<>();
        try {
            phoneCalls = phoneCallListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneCalls;
    }

    public PhoneCallEntity getPhoneCallById(String id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<PhoneCallEntity> phoneContactFuture = executor.submit(new Callable<PhoneCallEntity>() {
            @Override
            public PhoneCallEntity call() throws Exception {
                return phoneCallDao.getById(id);
            }
        });
        PhoneCallEntity phoneCall = null;
        try {
            phoneCall = phoneContactFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneCall;
    }

    public MutableLiveData<List<PhoneCall>> getPhoneCallsAsync(Context context, boolean forceUpdate) {
        if (phoneCallsLiveData == null) {
            phoneCallsLiveData = new MutableLiveData<>();
            getPhoneCalls(context, forceUpdate);
        }
        return phoneCallsLiveData;
    }

    public List<PhoneCall> getPhoneCalls(Context context, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneCall>> phoneCallListFuture = executor.submit(new Callable<List<PhoneCall>>() {
            @Override
            public List<PhoneCall> call() throws Exception {
                List<PhoneCallEntity> phoneCallEntities = phoneCallDao.getAll();
                if (forceUpdate || phoneCallEntities == null || phoneCallEntities.size() == 0) {
                    syncPhoneCalls(context);
                    phoneCallEntities = phoneCallDao.getAll();
                }
                return MappingUtils.convertPhoneCallEntitiesToPhoneCalls(phoneCallEntities);
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

    public MutableLiveData<List<PhoneSms>> getPhoneSmsListAsync(Context context, boolean forceUpdate) {
        if (phoneSmsLiveData == null) {
            phoneSmsLiveData = new MutableLiveData<>();
            getPhoneSmsList(context, forceUpdate);
        }
        return phoneSmsLiveData;
    }

    public List<PhoneSms> getPhoneSmsList(Context context, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneSms>> phoneSmsListFuture = executor.submit(new Callable<List<PhoneSms>>() {
            @Override
            public List<PhoneSms> call() throws Exception {
                List<PhoneSmsEntity> phoneSmsEntities = phoneSmsDao.getAll();
                if (forceUpdate || phoneSmsEntities == null || phoneSmsEntities.size() == 0) {
                    syncPhoneSms(context);
                    phoneSmsEntities = phoneSmsDao.getAll();
                }
                return MappingUtils.convertPhoneSmsEntitiesToPhoneSmses(phoneSmsEntities);
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

    public MutableLiveData<List<PhoneContact>> getPhoneContactsAsync(Context context, boolean forceUpdate) {
        if (phoneContactsLiveData == null) {
            phoneContactsLiveData = new MutableLiveData<>();
            getPhoneContacts(context, forceUpdate);
        }
        return phoneContactsLiveData;
    }

    public List<PhoneContactEntity> getPhoneContacts(Context context, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneContactEntity>> phoneContactListFuture = executor.submit(new Callable<List<PhoneContactEntity>>() {
            @Override
            public List<PhoneContactEntity> call() throws Exception {
                List<PhoneContactEntity> phoneContactEntities = phoneContactDao.getAll();
                if (forceUpdate || phoneContactEntities == null || phoneContactEntities.size() == 0) {
                    syncPhoneContacts(context);
                    phoneContactEntities = phoneContactDao.getAll();
                }
                return phoneContactEntities;
            }
        });
        List<PhoneContactEntity> phoneContactList = new ArrayList<>();
        try {
            phoneContactList = phoneContactListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneContactList;
    }

    public PhoneContactEntity getPhoneContactById(String id) {
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

    public boolean updatePhoneContact(Context context, int contactId, int starred) {
        // Update contact on phone
        int numberOfUpdatedRows = 0;
        if (context != null && PermissionUtils.hasPermission(context, Manifest.permission.WRITE_CONTACTS)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactsContract.Contacts.STARRED, starred);
            ContentResolver contentResolver = context.getContentResolver();
            numberOfUpdatedRows = contentResolver.update(ContactsContract.Contacts.CONTENT_URI, contentValues, ContactsContract.Contacts._ID + "= ?", new String[] { String.valueOf(contactId) });
        }
        // Update contact on database/cache
        if (numberOfUpdatedRows > 0) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    PhoneContactEntity phoneContactEntity = phoneContactDao.getByContactId(contactId);
                    if (phoneContactEntity != null) {
                        phoneContactEntity.starred = starred;
                        phoneContactDao.update(phoneContactEntity);
                    }
                }
            });
            executor.shutdown();
            return true;
        }
        return false;
    }

    public void syncPhoneContacts(Context context) {
        List<PhoneContactEntity> phoneContacts = new ArrayList<>();
        if (context != null && PermissionUtils.hasPermission(context, Manifest.permission.READ_CONTACTS)) {
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
                            phoneNo = phoneNo.replaceAll("\\s+", "");
                            if (!phoneNumbers.contains(phoneNo)) {
                                phoneNumbers.add(phoneNo);
                            }
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

    public void lockPhoneNumber(Context context, String phoneNumber) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ContentValues values = new ContentValues();
            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber);
            context.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);
        }
    }

    public void unblockPhoneNumber(Context context, String phoneNumber) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ContentValues values = new ContentValues();
            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber);
            Uri uri = context.getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);
            context.getContentResolver().delete(uri,null,null);
        }
    }

    public boolean isLockedPhoneNumber(Context context, String phoneNumber) {
        return true;
    }

    public void getLockedPhoneNumbers(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Cursor c = context.getContentResolver().query(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                    new String[]{BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                            BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                            BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER}, null, null, null);
        }
    }

    public String exportPhoneContacts(Context context, List<String> phoneContactIds) {
        if (phoneContactIds != null && phoneContactIds.size() > 0) {
            List<PhoneContactEntity> phoneContacts = getPhoneContacts(context,false);
            List<PhoneContactEntity> exportedContacts = filterPhoneContactsById(phoneContacts, phoneContactIds);
            return writePhoneContactsToExternal(context, exportedContacts);
        }
        return null;
    }

    public boolean deletePhoneContacts(Context context, List<String> phoneContactIds) {
        if (phoneContactIds != null && phoneContactIds.size() > 0) {
            List<PhoneContactEntity> phoneContacts = getPhoneContacts(context,false);
            List<PhoneContactEntity> deletedContacts = filterPhoneContactsById(phoneContacts, phoneContactIds);
            for (PhoneContactEntity deletedContact : deletedContacts) {
                try {
                    deletePhoneContact(context, deletedContact);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    public boolean deletePhoneContact(Context context, PhoneContactEntity phoneContactEntity) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> deletePhoneContactFuture = executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (context != null && PermissionUtils.hasPermission(context, Manifest.permission.WRITE_CONTACTS)) {
                    Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, phoneContactEntity.lookup);
                    context.getContentResolver().delete(contactUri, null, null);
                }
                phoneContactDao.delete(phoneContactEntity);
                return Boolean.TRUE;
            }
        });
        Boolean result = null;
        try {
            result = deletePhoneContactFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return (result != null)? result.booleanValue() : false;
    }

    private List<PhoneContactEntity> filterPhoneContactsById(List<PhoneContactEntity> phoneContactEntities, List<String> phoneContactIds) {
        List<PhoneContactEntity> filteredPhoneContacts = new ArrayList<>();
        if (phoneContactEntities != null && phoneContactIds != null && phoneContactIds.size() > 0) {
            for (PhoneContactEntity phoneContactEntity : phoneContactEntities) {
                if (phoneContactIds.contains(phoneContactEntity.id)) {
                    filteredPhoneContacts.add(phoneContactEntity);
                }
                if (filteredPhoneContacts.size() >= phoneContactIds.size()) break;
            }
        }
        return filteredPhoneContacts;
    }

    private String writePhoneContactsToExternal(Context context, List<PhoneContactEntity> phoneContactEntities) {
        try {
//            File contactExportDir = new File(context.getExternalFilesDir(null) + File.separator + "Virtual Assistant");
//            if (!contactExportDir.exists()) {
//                contactExportDir.mkdir();
//            }
            File contactExportFile = new File(context.getExternalFilesDir(null), "Export_contacts_" + Calendar.getInstance().getTimeInMillis() + ".vcf");
            FileWriter writer = new FileWriter(contactExportFile);
            List<VCard> phoneContactCards = createVCards(phoneContactEntities);
            String exportContactContent = Ezvcard.write(phoneContactCards).version(VCardVersion.V4_0).go();
            writer.write(exportContactContent);
            writer.close();
            return contactExportFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<VCard> createVCards(List<PhoneContactEntity> contactEntities) {
        List<VCard> vCards = new ArrayList<>();
        if (contactEntities != null) {
            for (PhoneContactEntity contactEntity : contactEntities) {
                VCard vCard = createVCard(contactEntity);
                vCards.add(vCard);
            }
        }
        return vCards;
    }

    private VCard createVCard(PhoneContactEntity contactEntity) {
        VCard vCard = new VCard();

        vCard.setFormattedName(contactEntity.displayName);
        vCard.setFormattedNameAlt(new FormattedName(contactEntity.displayNameAlt));

        if (contactEntity.phoneNumbers != null && !contactEntity.phoneNumbers.isEmpty()) {
            String[] splitedNumbers = contactEntity.phoneNumbers.split(",");
            if (splitedNumbers == null || splitedNumbers.length == 0) {
                vCard.addTelephoneNumber(contactEntity.phoneNumbers, TelephoneType.WORK);
            } else {
                for (int i = 0; i < splitedNumbers.length; i++) {
                    vCard.addTelephoneNumber(splitedNumbers[i], TelephoneType.WORK);
                }
            }
        }
        return vCard;
    }

    public boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    public MutableLiveData<List<PhoneApp>> getPhoneAppsAsync(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        if (phoneAppsLiveData == null) {
            phoneAppsLiveData = new MutableLiveData<>();
            getPhoneApps(context, shouldGetSystemPackages, forceUpdate);
        }
        return phoneAppsLiveData;
    }

    public List<PhoneAppEntity> getPhoneApps(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PhoneAppEntity>> phoneAppListFuture = executor.submit(new Callable<List<PhoneAppEntity>>() {
            @Override
            public List<PhoneAppEntity> call() throws Exception {
                List<PhoneAppEntity> phoneApps = shouldGetSystemPackages? phoneAppDao.getAll() : phoneAppDao.getPhoneApps(2); //2 normal apps, 1 system apps
                if (forceUpdate || phoneApps == null || phoneApps.size() == 0) {
                    syncPhoneApps(context);
                    phoneApps = shouldGetSystemPackages? phoneAppDao.getAll() : phoneAppDao.getPhoneApps(2);
                }
                return phoneApps;
            }
        });
        List<PhoneAppEntity> phoneApps = new ArrayList<>();
        try {
            phoneApps = phoneAppListFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return phoneApps;
    }

    public void syncPhoneApps(Context context) {
        List<PhoneAppEntity> phoneApps = new ArrayList<>();
        if (context != null) {
            List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
            for (PackageInfo packageInfo : packageInfoList) {
                PhoneAppEntity phoneAppEntity = getPhoneAppEntity(context, packageInfo);
                phoneApps.add(phoneAppEntity);
            }
        }
        phoneAppDao.insert(phoneApps);
    }

    private PhoneAppEntity getPhoneAppEntity(Context context, PackageInfo packageInfo) {
        PhoneAppEntity phoneAppEntity = new PhoneAppEntity();
        phoneAppEntity.id = UUID.randomUUID().toString();
        phoneAppEntity.appId = packageInfo.applicationInfo.uid;
        phoneAppEntity.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        phoneAppEntity.packageName = packageInfo.packageName;
        phoneAppEntity.versionCode = packageInfo.versionCode;
        phoneAppEntity.versionName = packageInfo.versionName;
        phoneAppEntity.type = isSystemPackage(packageInfo)? 1 : 2; //1 system app, 2 normal app
        phoneAppEntity.createdDate = new Date();
        return phoneAppEntity;
    }

    public ArrayMap<String, UsageStats> getPhoneAppUsageStats(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, calendar.getTimeInMillis(), System.currentTimeMillis());
            if (usageStats.isEmpty()) {
                context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }

            ArrayMap<String, UsageStats> appUsageStats = new ArrayMap<>();
            for (UsageStats usageStat: usageStats) {
                UsageStats existingStat = appUsageStats.get(usageStat.getPackageName());
                if (existingStat == null) {
                    appUsageStats.put(usageStat.getPackageName(), usageStat);
                } else {
                    existingStat.add(usageStat);
                }
            }
            return appUsageStats;
        }
        return null;
    }

    public boolean hasPhoneLockPasscode(Context context) {
        String passcode = getPhoneLockPasscode(context);
        return (passcode == null || passcode.isEmpty()) ? false : true;
    }

    public String getPhoneLockPasscode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        String passcode = sharedPref.getString("AppLockService.PhoneLockPasscode", "");
        return passcode;
    }

    public void savePhoneLockPasscode(Context context, String passwordNumber) {
        SharedPreferences sharedPref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("AppLockService.PhoneLockPasscode", passwordNumber);
        editor.commit();
    }

    public void lockPhoneApp(Context context, PhoneApp phoneApp) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                phoneAppDao.lock(phoneApp.packageName);
                addLocked(context, phoneApp.packageName);
            }
        });
        executor.shutdown();
    }

    public void unlockPhoneApp(Context context, PhoneApp phoneApp) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                phoneAppDao.unlock(phoneApp.packageName);
                removeLocked(context, phoneApp.packageName);
            }
        });
        executor.shutdown();
    }

    public List<String> getLockedPhoneApps(Context context) {
        return getLockedAppFromCache(context);
    }

    private void addLocked(Context context, String lockedApp) {
        ArrayList<String> lockedApps = getLockedAppFromCache(context);
        if (lockedApps == null) {
            lockedApps = new ArrayList<>();
        }
        lockedApps.add(lockedApp);
        saveLockedAppInCache(context, lockedApps);
    }

    private void removeLocked(Context context, String lockedApp) {
        ArrayList<String> lockedApps = getLockedAppFromCache(context);
        if (lockedApps != null) {
            lockedApps.remove(lockedApp);
            saveLockedAppInCache(context, lockedApps);
        }
    }

    private ArrayList<String> getLockedAppFromCache(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        String lockedAppString = sharedPref.getString("AppLockService.LockedApp", "");
        Gson gson = new Gson();
        String[] lockedApp = !lockedAppString.isEmpty()? gson.fromJson(lockedAppString, String[].class) : new String[] {};
        return new ArrayList<>(Arrays.asList(lockedApp));
    }

    private void saveLockedAppInCache(Context context, List<String> lockedApp) {
        SharedPreferences sharedPref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String lockedAppString = gson.toJson(lockedApp);
        editor.putString("AppLockService.LockedApp", lockedAppString);
        editor.commit();
    }

    public boolean deletePhoneApp(Context context, PhoneApp phoneApp) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                PhoneAppEntity phoneAppEntity = phoneAppDao.getByPackageName(phoneApp.packageName);
                if (phoneAppEntity != null) {
                    phoneAppDao.delete(phoneAppEntity);
                }
            }
        });
        executor.shutdown();
        return true;
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
