package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.Weather;

import java.util.List;

@Dao
public interface WeatherDao extends BaseDao<Weather> {
    @Query("SELECT * FROM Weather")
    List<Weather> getAll();
}
