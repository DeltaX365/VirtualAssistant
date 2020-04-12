package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.Weather;

import java.util.List;

@Dao
public abstract class WeatherDao implements BaseDao<Weather> {
    @Query("SELECT * FROM Weather")
    abstract List<Weather> getAll();
}
