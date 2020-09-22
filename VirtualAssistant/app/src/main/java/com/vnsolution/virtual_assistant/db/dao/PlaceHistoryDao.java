package com.vnsolution.virtual_assistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.vnsolution.virtual_assistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtual_assistant.db.entity.PlaceHistoryWithDetailEntity;

import java.util.List;

@Dao
public interface PlaceHistoryDao extends BaseDao<PlaceHistoryEntity> {
    @Query("SELECT * FROM PlaceHistory")
    LiveData<List<PlaceHistoryEntity>> getAll();

    @Transaction
    @Query("SELECT * FROM PlaceHistory")
    LiveData<List<PlaceHistoryWithDetailEntity>> getPlaceHistoriesWithDetail();

    @Query("DELETE FROM PlaceHistory")
    void reset();
}
