package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.GeolocationHistoryEntity;

import java.util.List;

@Dao
public interface GeolocationHistoryDao extends BaseDao<GeolocationHistoryEntity> {
    @Query("SELECT * FROM GeolocationHistory")
    List<GeolocationHistoryEntity> getAll();
}
