package com.vnsolution.virtualassistant.ui.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.common.ReminderSetting;
import com.vnsolution.virtualassistant.db.entity.PhoneAppEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneContactEntity;
import com.vnsolution.virtualassistant.db.entity.ReminderEntity;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.Reminder;
import com.vnsolution.virtualassistant.service.device.DeviceManager;
import com.vnsolution.virtualassistant.service.reminder.ReminderBroadcastReceiver;
import com.vnsolution.virtualassistant.service.reminder.ReminderManager;
import com.vnsolution.virtualassistant.util.MappingUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class ReminderViewModel extends ViewModel {
    @Inject
    ReminderManager reminderManager;

    @Inject
    DeviceManager deviceManager;

    @Inject
    public ReminderViewModel() {}

    public List<Reminder> getReminders() {
        List<ReminderEntity> reminderEntityList = reminderManager.getReminders();
        return MappingUtils.convertReminderEntitiesToReminders(reminderEntityList);
    }

    public Reminder getReminderById(String reminderId) {
        ReminderEntity reminderEntity = reminderManager.getReminderById(reminderId);
        return MappingUtils.convertReminderEntityToReminder(reminderEntity);
    }

    public boolean saveReminder(Context context, Reminder reminder) {
        ReminderEntity reminderEntity = MappingUtils.convertReminderToReminderEntity(reminder);
        if ((reminderEntity.id == null || reminder.id.isEmpty()) && reminderEntity.repeat != null && !reminderEntity.repeat.isEmpty()) {
            reminderEntity.alarmRequestCode = generateRequestCode();
            if(!setReminderAlarm(context, reminderEntity.alarmRequestCode, reminderEntity.repeat, reminderEntity.date, reminderEntity.name)) {
                return false;
            }
        }
        return reminderManager.saveReminder(reminderEntity);
    }

    public boolean deleteReminder(Context context, String reminderId) {
        ReminderEntity reminder = reminderManager.getReminderById(reminderId);
        if (reminder != null) {
            boolean result = reminderManager.removeReminder(reminder);
            if (result) {
                return deleteReminderAlarm(context, reminder.alarmRequestCode);
            }
        }
        return false;
    }

    public List<PhoneContact> getPhoneContacts(Context context, boolean forceUpdate) {
        List<PhoneContactEntity> phoneContactEntityList = deviceManager.getPhoneContacts(context, forceUpdate);
        return MappingUtils.convertPhoneContactEntitiesToPhoneContacts(phoneContactEntityList);
    }

    public List<PhoneApp> getPhoneApps(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        List<PhoneAppEntity> phoneAppEntityList =  deviceManager.getPhoneApps(context, shouldGetSystemPackages, forceUpdate);
        return MappingUtils.convertPhoneAppEntitiesToPhoneApps(phoneAppEntityList);
    }

    private int generateRequestCode() {
        int requestCode = 1;
        List<ReminderEntity> reminders = reminderManager.getReminders();
        if (reminders != null && reminders.size() > 0) {
            int lastIndex = reminders.size() - 1;
            requestCode = reminders.get(lastIndex).alarmRequestCode + 1;
        }
        return requestCode;
    }

    private boolean setReminderAlarm(Context context, int requestCode, String repeat, Date reminderDate, String reminderTitle) {
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra(ReminderSetting.NOTIFY_CONTENT_TEXT, reminderTitle);
        intent.setAction(ReminderSetting.NOTIFY_ACTION_SHOW_FULL_SCREEN);
        int pendingIntentFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, pendingIntentFlag);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null|| pendingIntent == null) return false;

        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.setTime(reminderDate);
        long triggerAtMillis = reminderCalendar.getTimeInMillis();

        switch (repeat) {
            case ReminderSetting.REPEAT_NONE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                }
                break;
            case ReminderSetting.REPEAT_DAILY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
                break;
            case ReminderSetting.REPEAT_WEEKLY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                break;
            case ReminderSetting.REPEAT_MONTHLY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * reminderCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), pendingIntent);
                break;
            case ReminderSetting.REPEAT_YEARLY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * reminderCalendar.getActualMaximum(Calendar.DAY_OF_YEAR), pendingIntent);
                break;
        }
        return true;
    }

    private boolean deleteReminderAlarm(Context context, int requestCode) {
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        int pendingIntentFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, pendingIntentFlag);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null|| pendingIntent == null) return false;

        alarmManager.cancel(pendingIntent);
        return true;
    }
}
