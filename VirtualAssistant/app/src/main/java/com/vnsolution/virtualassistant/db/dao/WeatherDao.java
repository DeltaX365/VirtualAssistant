package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.Setting;

import java.util.List;

public abstract class PlaceDao implements BaseDao<Place> {
    @Query("SELECT * FROM Place")
    abstract List<Place> getAll();
}
