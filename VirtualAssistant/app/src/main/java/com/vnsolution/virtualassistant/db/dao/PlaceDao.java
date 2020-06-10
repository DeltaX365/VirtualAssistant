package com.vnsolution.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PlaceEntity;

import java.util.List;

@Dao
public interface PlaceDao extends BaseDao<PlaceEntity> {
    @Query("SELECT * FROM Place")
    LiveData<List<PlaceEntity>> getAll();

    @Query("SELECT * FROM Place WHERE Id=:id")
    LiveData<PlaceEntity> getById(String id);
}
