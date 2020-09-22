package com.vnsolution.virtual_assistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtual_assistant.db.entity.WeatherEntity;

import java.util.List;

@Dao
public interface WeatherDao extends BaseDao<WeatherEntity> {
    @Query("SELECT * FROM Weather")
    List<WeatherEntity> getAll();

    @Query("DELETE FROM Weather")
    void reset();
}
