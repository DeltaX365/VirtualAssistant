package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.CallHistoryEntity;
import com.vnsolution.virtualassistant.db.entity.WeatherEntity;

import java.util.List;

@Dao
public interface CallHistoryDao extends BaseDao<CallHistoryEntity> {
    @Query("SELECT * FROM CallHistory")
    List<CallHistoryEntity> getAll();
}
