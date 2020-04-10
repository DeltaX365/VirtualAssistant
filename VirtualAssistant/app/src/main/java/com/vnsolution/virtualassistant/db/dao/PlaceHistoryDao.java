package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.PlaceHistory;

import java.util.List;

public abstract class PlaceHistoryDao implements BaseDao<PlaceHistory> {
    @Query("SELECT * FROM PlaceHistory")
    abstract List<PlaceHistory> getAll();
}
