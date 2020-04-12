package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.GeolocationHistory;
import com.vnsolution.virtualassistant.vo.Place;

import java.util.List;

@Dao
public abstract class GeolocationHistoryDao implements BaseDao<GeolocationHistory> {
    @Query("SELECT * FROM GeolocationHistory")
    abstract List<GeolocationHistory> getAll();
}
