package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PlaceEntity;

import java.util.List;

@Dao
public interface PlaceDao extends BaseDao<PlaceEntity> {
    @Query("SELECT * FROM Place")
    List<PlaceEntity> getAll();

    @Query("SELECT * FROM Place WHERE Id=:id")
    PlaceEntity getById(String id);
}
