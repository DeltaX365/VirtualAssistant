package com.vnsolution.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.SettingEntity;

import java.util.List;

@Dao
public abstract class SettingDao implements BaseDao<SettingEntity> {
    @Query("SELECT * FROM Setting")
    abstract LiveData<List<SettingEntity>> getAll();
}
