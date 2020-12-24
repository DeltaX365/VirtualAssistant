package com.vnsolution.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.ReminderEntity;
import com.vnsolution.virtualassistant.db.entity.SettingEntity;

import java.util.List;

@Dao
public interface ReminderDao extends BaseDao<ReminderEntity> {
    @Query("SELECT * FROM Reminder ORDER BY Date ASC")
    List<ReminderEntity> getAll();

    @Query("SELECT * FROM Reminder WHERE Id=:id")
    ReminderEntity getById(String id);
}
