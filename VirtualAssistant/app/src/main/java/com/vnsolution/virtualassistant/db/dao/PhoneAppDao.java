package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PhoneAppEntity;

import java.util.List;

@Dao
public interface PhoneAppDao extends BaseDao<PhoneAppEntity> {
    @Query("SELECT * FROM PhoneApp")
    List<PhoneAppEntity> getAll();

    @Query("DELETE FROM PhoneApp")
    void reset();
}
