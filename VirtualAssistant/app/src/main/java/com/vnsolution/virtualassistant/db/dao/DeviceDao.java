package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.DeviceEntity;

import java.util.List;

@Dao
public interface DeviceDao extends BaseDao<DeviceEntity> {
    @Query("SELECT * FROM Device")
    List<DeviceEntity> getAll();
}
