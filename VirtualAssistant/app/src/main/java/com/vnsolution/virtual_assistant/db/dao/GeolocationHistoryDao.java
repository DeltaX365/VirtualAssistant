package com.vnsolution.virtual_assistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtual_assistant.db.entity.GeolocationHistoryEntity;

import java.util.List;

@Dao
public interface GeolocationHistoryDao extends BaseDao<GeolocationHistoryEntity> {
    @Query("SELECT * FROM GeolocationHistory")
    LiveData<List<GeolocationHistoryEntity>> getAll();

    @Query("DELETE FROM GeolocationHistory")
    void reset();
}
