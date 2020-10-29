package com.vnsolution.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceSummaryEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceWithHistoryDetailEntity;

import java.util.List;

@Dao
public interface PlaceDao extends BaseDao<PlaceEntity> {
    @Query("SELECT * FROM Place")
    LiveData<List<PlaceEntity>> getAll();

    @Transaction
    @Query("SELECT p.* " +
            "FROM Place as p " +
            "LEFT JOIN PlaceHistory as ph on ph.PlaceId = p.Id " +
            "GROUP BY p.Id " +
            "ORDER BY ph.EnterTime DESC")
    LiveData<List<PlaceWithHistoryDetailEntity>> getPlaceWithHistories();

    @Transaction
    @Query("SELECT p.* " +
            "FROM Place as p " +
            "LEFT JOIN PlaceHistory as ph on ph.PlaceId = p.Id " +
            "GROUP BY p.Id " +
            "ORDER BY ph.EnterTime DESC " +
            "LIMIT 0, 10")
    LiveData<List<PlaceWithHistoryDetailEntity>> getRecentlyVisitedPlaces();

    @Transaction
    @Query("SELECT p.* " +
            "FROM Place as p, " +
            "(" +
            "SELECT PlaceId, sum(strftime('%s', ExistTime) - strftime('%s', EnterTime)) as TotalVisitedTime " +
            "FROM PlaceHistory " +
            "GROUP BY PlaceId " +
            ") AS ph " +
            "WHERE ph.PlaceId = p.Id " +
            "ORDER BY ph.TotalVisitedTime DESC " +
            "LIMIT 0, 10")
    LiveData<List<PlaceWithHistoryDetailEntity>> getMostVisitedPlaces();

    @Query("SELECT * FROM Place WHERE Synced = 0")
    List<PlaceEntity> getUnsyncedPlaces();

    @Query("SELECT count(*) AS TotalPlace, count(DISTINCT AdministrativeAreaLevel1) AS TotalCity, count(DISTINCT Country) AS TotalCountry, " +
            "( " +
            "SELECT count(*) " +
            "FROM Place " +
            "LEFT JOIN PlaceHistory ON PlaceHistory.PlaceId = Place.Id " +
            "WHERE PlaceHistory.EnterTime >= strftime('%Y-%m-%d 00:00:00', 'now', '-1 day') AND PlaceHistory.EnterTime < strftime('%Y-%m-%d 00:00:00', 'now') " +
            ") as TotalPlaceYesterday " +
            "FROM Place")
    LiveData<PlaceSummaryEntity> getPlaceSummary();

    @Query("SELECT * FROM Place WHERE Id=:id")
    PlaceEntity getById(String id);

    @Transaction
    @Query("SELECT * FROM Place WHERE Id=:id")
    LiveData<PlaceWithHistoryDetailEntity> getPlaceWithHistoriesByIdASync(String id);

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
