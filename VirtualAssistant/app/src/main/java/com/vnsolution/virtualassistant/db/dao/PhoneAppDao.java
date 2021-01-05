package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PhoneAppEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneCallEntity;

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

    @Query("UPDATE PhoneApp SET Locked = 1 WHERE PackageName = :packageName")
    void lock(String packageName);

    @Query("UPDATE PhoneApp SET Locked = 0 WHERE PackageName = :packageName")
    void unlock(String packageName);

    @Query("DELETE FROM PhoneApp")
    void reset();
}
