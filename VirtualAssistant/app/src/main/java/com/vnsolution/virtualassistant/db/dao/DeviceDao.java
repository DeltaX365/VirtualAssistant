package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.Device;
import com.vnsolution.virtualassistant.vo.Place;

import java.util.List;

public abstract class DeviceDao implements BaseDao<Device> {
    @Query("SELECT * FROM Device")
    abstract List<Device> getAll();
}
