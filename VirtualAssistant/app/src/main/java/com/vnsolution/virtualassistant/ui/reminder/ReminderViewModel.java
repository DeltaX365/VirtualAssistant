package com.vnsolution.virtualassistant.ui.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.common.ReminderOptions;
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
import java.util.UUID;

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

    public boolean createReminder(Context context, String title, Date date, String repeat, String action, String data) {
        if (context == null || title == null || title.isEmpty() || date == null) {
            return false;
        }
        int requestCode = generateRequestCode();
        if (requestCode > 0 && date != null) {
            ReminderEntity reminderEntity = new ReminderEntity();
            reminderEntity.id = UUID.randomUUID().toString();
            reminderEntity.name = title;
            reminderEntity.date = date;
            reminderEntity.repeat = repeat;
            reminderEntity.action = action;
            reminderEntity.extendedData = data;
            reminderEntity.alarmRequestCode = requestCode;
            reminderEntity.createdDate = new Date();
            boolean result = reminderManager.addReminder(reminderEntity);
            if (result && repeat != null && !repeat.isEmpty()) {
                return setReminderAlarm(context, requestCode, repeat, date, title);
            }
        }
        return false;
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
            requestCode = reminders.get(0).alarmRequestCode + 1;
        }
        return requestCode;
    }

    private boolean setReminderAlarm(Context context, int requestCode, String repeat, Date reminderDate, String title) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("ReminderContent", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager == null|| pendingIntent == null) return false;

        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.setTime(reminderDate);
        long triggerAtMillis = reminderCalendar.getTimeInMillis();

        switch (repeat) {
            case ReminderOptions.REPEAT_NONE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                }
                break;
            case ReminderOptions.REPEAT_DAILY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
                break;
            case ReminderOptions.REPEAT_WEEKLY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                break;
            case ReminderOptions.REPEAT_MONTHLY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * reminderCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), pendingIntent);
                break;
            case ReminderOptions.REPEAT_YEARLY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * reminderCalendar.getActualMaximum(Calendar.DAY_OF_YEAR), pendingIntent);
                break;
        }
        return true;
    }

    private boolean deleteReminderAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager == null|| pendingIntent == null) return false;

        alarmManager.cancel(pendingIntent);
        return true;
    }
}
