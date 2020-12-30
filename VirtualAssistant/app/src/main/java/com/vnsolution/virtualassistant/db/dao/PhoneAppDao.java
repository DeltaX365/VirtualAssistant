package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PhoneAppEntity;

import java.util.List;

@Dao
public interface PhoneAppDao extends BaseDao<PhoneAppEntity> {
    @Query("SELECT * FROM PhoneApp ORDER BY AppName ASC")
    List<PhoneAppEntity> getAll();

    @Query("SELECT * " +
            "FROM PhoneApp " +
            "WHERE Type = :type " +
            "ORDER BY AppName ASC")
    List<PhoneAppEntity> getPhoneApps(int type);

    @Query("DELETE FROM PhoneApp")
    void reset();
}
