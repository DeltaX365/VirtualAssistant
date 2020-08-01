package com.vnsolution.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryWithDetailEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceWithHistoryDetailEntity;

import java.util.List;

@Dao
public interface PlaceDao extends BaseDao<PlaceEntity> {
    @Query("SELECT * FROM Place")
    LiveData<List<PlaceEntity>> getAll();

    @Transaction
    @Query("SELECT * FROM Place")
    LiveData<List<PlaceWithHistoryDetailEntity>> getPlaceWithHistories();

    @Query("SELECT * FROM Place WHERE Id=:id")
    LiveData<PlaceEntity> getById(String id);

    @Query("SELECT * FROM Place WHERE Latitude=:latitude AND Longitude=:longitude")
    PlaceEntity getByLocation(double latitude, double longitude);

    @Query("SELECT * FROM Place WHERE FormattedAddress=:formattedAddress")
    PlaceEntity getByAddress(String formattedAddress);

    @Query("SELECT * FROM Place WHERE Name=:name")
    PlaceEntity getByName(String name);

    @Query("SELECT * FROM Place WHERE GGPlaceId=:ggPlaceId")
    PlaceEntity getByGGPlaceId(String ggPlaceId);

    @Query("DELETE FROM Place")
    void reset();
}
