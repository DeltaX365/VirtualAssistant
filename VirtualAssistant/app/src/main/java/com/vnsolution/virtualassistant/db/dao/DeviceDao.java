package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.Device;
import com.vnsolution.virtualassistant.vo.Place;

import java.util.List;

@Dao
public interface DeviceDao extends BaseDao<Device> {
    @Query("SELECT * FROM Device")
    List<Device> getAll();
}
