package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.Setting;

import java.util.List;

@Dao
public interface PlaceDao extends BaseDao<Place> {
    @Query("SELECT * FROM Place")
    List<Place> getAll();

    @Query("SELECT * FROM Place WHERE Id=:id")
    Place getById(String id);
}
