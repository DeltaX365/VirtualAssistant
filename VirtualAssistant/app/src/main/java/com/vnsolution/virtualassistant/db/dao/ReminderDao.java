package com.vnsolution.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.ReminderEntity;
import com.vnsolution.virtualassistant.db.entity.SettingEntity;

import java.util.List;

@Dao
public abstract class ReminderDao implements BaseDao<ReminderEntity> {
    @Query("SELECT * FROM Reminder")
    abstract LiveData<List<ReminderEntity>> getAll();
}
