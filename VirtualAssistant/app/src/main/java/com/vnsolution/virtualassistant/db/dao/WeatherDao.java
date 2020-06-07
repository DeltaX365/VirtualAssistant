package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.WeatherEntity;

import java.util.List;

@Dao
public interface WeatherDao extends BaseDao<WeatherEntity> {
    @Query("SELECT * FROM Weather")
    List<WeatherEntity> getAll();
}
