package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.SmsHistoryEntity;
import com.vnsolution.virtualassistant.db.entity.WeatherEntity;

import java.util.List;

@Dao
public interface SmsHistoryDao extends BaseDao<SmsHistoryEntity> {
    @Query("SELECT * FROM SmsHistory")
    List<SmsHistoryEntity> getAll();
}
