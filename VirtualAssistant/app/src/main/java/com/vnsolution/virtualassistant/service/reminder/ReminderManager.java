package com.vnsolution.virtualassistant.service.reminder;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.vnsolution.virtualassistant.db.dao.ReminderDao;
import com.vnsolution.virtualassistant.db.entity.ReminderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

public class ReminderManager {
    @Inject
    public ReminderDao reminderDao;

    @Inject
    public ReminderManager() {}

    public List<ReminderEntity> getReminders() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<ReminderEntity>> reminderListFuture = executor.submit(new Callable<List<ReminderEntity>>() {
            @Override
            public List<ReminderEntity> call() throws Exception {
                return reminderDao.getAll();
            }
        });
        List<ReminderEntity> reminderList = new ArrayList<>();
        try {
            reminderList = reminderListFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return reminderList;
    }

    public ReminderEntity getReminderById(String reminderId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ReminderEntity> reminderFuture = executor.submit(new Callable<ReminderEntity>() {
            @Override
            public ReminderEntity call() throws Exception {
                return reminderDao.getById(reminderId);
            }
        });
        ReminderEntity reminder = null;
        try {
            reminder = reminderFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return reminder;
    }

    public boolean addReminder(ReminderEntity reminderEntity) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> addReminderFuture = executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                reminderDao.insert(reminderEntity);
                return Boolean.TRUE;
            }
        });
        Boolean result = null;
        try {
            result = addReminderFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return (result == null)? false : result.booleanValue();
    }

    public boolean removeReminder(String reminderId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> removeReminderFuture = executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ReminderEntity reminderEntity = reminderDao.getById(reminderId);
                if (reminderEntity != null) {
                    reminderDao.delete(reminderEntity);
                }
                return Boolean.TRUE;
            }
        });
        Boolean result = null;
        try {
            result = removeReminderFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return (result == null)? false : result.booleanValue();
    }

    public boolean removeReminder(ReminderEntity reminderEntity) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> removeReminderFuture = executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                reminderDao.delete(reminderEntity);
                return Boolean.TRUE;
            }
        });
        Boolean result = null;
        try {
            result = removeReminderFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        executor.shutdown();
        return (result == null)? false : result.booleanValue();
    }
}
