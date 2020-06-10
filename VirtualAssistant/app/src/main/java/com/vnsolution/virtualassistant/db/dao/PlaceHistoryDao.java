package com.vnsolution.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;

import java.util.List;

@Dao
public interface PlaceHistoryDao extends BaseDao<PlaceHistoryEntity> {
    @Query("SELECT * FROM PlaceHistory")
    LiveData<List<PlaceHistoryEntity>> getAll();
}
