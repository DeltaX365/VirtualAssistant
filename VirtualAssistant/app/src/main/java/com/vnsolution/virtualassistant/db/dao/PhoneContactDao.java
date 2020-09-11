package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PhoneContactEntity;
import com.vnsolution.virtualassistant.db.entity.WeatherEntity;

import java.util.List;

@Dao
public interface PhoneContactDao extends BaseDao<PhoneContactEntity> {
    @Query("SELECT * FROM PhoneContact")
    List<PhoneContactEntity> getAll();
}
