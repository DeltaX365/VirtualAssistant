package com.vnsolution.virtual_assistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtual_assistant.db.entity.PhoneAppUsageEntity;

import java.util.List;

@Dao
public interface PhoneAppUsageDao extends BaseDao<PhoneAppUsageEntity> {
    @Query("SELECT * FROM PhoneAppUsage")
    List<PhoneAppUsageEntity> getAll();

    @Query("DELETE FROM PhoneAppUsage")
    void reset();
}
