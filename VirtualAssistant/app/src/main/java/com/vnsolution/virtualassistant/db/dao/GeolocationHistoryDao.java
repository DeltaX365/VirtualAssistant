package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.GeolocationHistory;
import com.vnsolution.virtualassistant.vo.Place;

import java.util.List;

@Dao
public interface GeolocationHistoryDao extends BaseDao<GeolocationHistory> {
    @Query("SELECT * FROM GeolocationHistory")
    List<GeolocationHistory> getAll();
}
