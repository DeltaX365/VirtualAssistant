package com.vnsolution.virtualassistant.service.devicemanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.ArrayMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vnsolution.virtualassistant.model.Application;
import com.vnsolution.virtualassistant.model.CallLog;
import com.vnsolution.virtualassistant.model.Contact;
import com.vnsolution.virtualassistant.model.Sms;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceManager {
    private static final int DELAY_FETCH_DATA_IN_MILLISECONDS = 1 * 1000; // 1 second
    private static final String SMS_INBOX = "content://sms/inbox";
    private static final String SMS_SENT = "content://sms/sent";
    private static final String SMS_DRAFT = "content://sms/draft";
    private static DeviceManager instance;

    private ArrayMap<String, UsageStats> appUsageStats;
    private MutableLiveData<List<Application>> installedApplicationsLiveData;
    private MutableLiveData<List<CallLog>> callLogsLiveData;
    private MutableLiveData<List<Sms>> smsLiveData;
    private MutableLiveData<List<Contact>> contactsLiveData;

    private DeviceManager() {}

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

    public void favouriteApp(Application application) {}

    public void categorizeApp(Application application) {}

    public void getTotalCalls() {}

    public void getTotalPhones() {}

    public void getTotalTime() {}

    public void getTop10RecentCalls() {}

    public void getTop10MostCalls() {}

    public MutableLiveData<List<Application>> getInstalledApplications(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        if (installedApplicationsLiveData == null || forceUpdate) {
            installedApplicationsLiveData = new MutableLiveData<>();
            getInstalledApplicationsAsync(context, shouldGetSystemPackages);
        }
        return installedApplicationsLiveData;
    }

    public MutableLiveData<List<CallLog>> getCallLogs(Context context, boolean forceUpdate) {
        if (callLogsLiveData == null || forceUpdate) {
            callLogsLiveData = new MutableLiveData<>();
            getCallLogsAsync(context);
        }
        return callLogsLiveData;
    }

    public LiveData<List<Sms>> getSms(Context context, boolean forceUpdate) {
        if (smsLiveData == null || forceUpdate) {
            smsLiveData = new MutableLiveData<>();
            getSmsAsync(context);
        }
        return smsLiveData;
    }

    public LiveData<List<Contact>> getContacts(Context context, boolean forceUpdate) {
        if (contactsLiveData == null || forceUpdate) {
            contactsLiveData = new MutableLiveData<>();
            getContactsAsync(context);
        }
        return contactsLiveData;
    }

    private void getInstalledApplicationsAsync(Context context, boolean shouldGetSystemPackages) {
        // Do async operation to fetch installed applications
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Application> applications = new ArrayList<>();
                if (context != null) {
                    getUsageStats(context);
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
                        Application application = new Application();
                        application.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                        application.packageName = packageInfo.packageName;
                        application.versionCode = packageInfo.versionCode;
                        application.versionName = packageInfo.versionName;
                        application.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                        application.usageStats = appUsageStats.get(application.packageName);
                        applications.add(application);
                    }
                }
                installedApplicationsLiveData.setValue(applications);
            }
        }, DELAY_FETCH_DATA_IN_MILLISECONDS);
    }

    private void getUsageStats(Context context) {
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

    private void getCallLogsAsync(Context context) {
        // Do async operation to fetch installed applications
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<CallLog> callLogs = new ArrayList<>();
                if (context != null && PermissionUtils.hasPermission(context, Manifest.permission.READ_CALL_LOG)) {
                    @SuppressLint("MissingPermission") Cursor cursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
                    while (cursor.moveToNext()) {
                        String phoneNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                        String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                        int callDirectionCode = Integer.parseInt(callType);
                        long callDate = cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
                        //Date callDateTime = new Date(Long.valueOf(callDate));
                        long callDuration = cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION));

                        CallLog callLog = new CallLog();
                        callLog.phoneNumber = phoneNumber;
                        callLog.callDirectionCode = callDirectionCode;
                        callLog.callDate = callDate;
                        callLog.callDuration = callDuration;
                        callLogs.add(callLog);
                        callLogsLiveData.setValue(callLogs);
                    }
                    cursor.close();
                }
            }
        }, DELAY_FETCH_DATA_IN_MILLISECONDS);
    }

    private void getSmsAsync(Context context) {
        // Do async operation to fetch installed applications
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Sms> smsList = new ArrayList<>();
                if (context != null && PermissionUtils.hasPermission(context, Manifest.permission.READ_SMS)) {
                    Cursor cursor = context.getContentResolver().query(Uri.parse(SMS_INBOX), null, null, null, null);
                    if (cursor.moveToNext()) {
                        do {
                            Sms sms = new Sms();
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                switch (cursor.getColumnName(i)) {
                                    case "_id":
                                        sms.id = cursor.getInt(i);
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
                                        sms.isRead = cursor.getInt(i);
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
                                        sms.isLocked = cursor.getInt(i);
                                        break;
                                    case "sub_id":
                                        sms.subId = cursor.getInt(i);
                                        break;
                                    case "creator":
                                        sms.creator = cursor.getString(i);
                                        break;
                                    case "seen":
                                        sms.isSeen = cursor.getInt(i);
                                        break;
                                    case "deletable":
                                        sms.isDeletable = cursor.getInt(i);
                                        break;
                                }
                            }
                            smsList.add(sms);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                smsLiveData.setValue(smsList);
            }
        }, DELAY_FETCH_DATA_IN_MILLISECONDS);
    }

    private void getContactsAsync(Context context) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Contact> contacts = new ArrayList<>();
                if (context != null) {
                    ContentResolver contentResolver = context.getContentResolver();
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                    if (cursor.moveToNext()) {
                        do {
                            Contact contact = new Contact();
                            contact.id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            contact.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            contact.mobileNumbers = new ArrayList<>();
                            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                Cursor cursor1 = contentResolver.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contact.id},
                                        null);
                                while (cursor1.moveToNext()) {
                                    String phoneNo = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    contact.mobileNumbers.add(phoneNo);
                                }
                                cursor1.close();
                            }
                            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contact.id));
                            contact.photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
                            contacts.add(contact);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                contactsLiveData.setValue(contacts);
            }
        }, DELAY_FETCH_DATA_IN_MILLISECONDS);
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo != null) && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0));
    }
}
