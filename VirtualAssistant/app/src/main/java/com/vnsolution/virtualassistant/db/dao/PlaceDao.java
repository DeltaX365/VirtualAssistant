package com.vnsolution.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceSummaryEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceWithHistoryDetailEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface PlaceDao extends BaseDao<PlaceEntity> {
    @Query("SELECT * FROM Place ORDER BY Name ASC")
    LiveData<List<PlaceEntity>> getAll();

    @Transaction
    @Query("SELECT p.* " +
            "FROM Place as p " +
            "LEFT JOIN PlaceHistory as ph on ph.PlaceId = p.Id " +
            "GROUP BY p.Id " +
            "ORDER BY ph.EnterTime DESC")
    LiveData<List<PlaceWithHistoryDetailEntity>> getPlaceWithHistories();

    @Transaction
    @Query("SELECT Place.* " +
            "FROM Place " +
            "LEFT JOIN PlaceHistory ON PlaceHistory.PlaceId = Place.Id " +
            "WHERE PlaceHistory.EnterTime >= :startDate AND PlaceHistory.EnterTime < :endDate " +
            "GROUP BY Place.Id " +
            "ORDER BY PlaceHistory.EnterTime DESC")
    LiveData<List<PlaceWithHistoryDetailEntity>> getPlacesByDateRange(String startDate, String endDate);

    @Transaction
    @Query("SELECT * " +
            "FROM Place " +
            "WHERE Types LIKE '%' + :category + '%'")
    LiveData<List<PlaceWithHistoryDetailEntity>> getPlacesByCategory(String category);

    @Transaction
    @Query("SELECT * " +
            "FROM Place " +
            "WHERE AdministrativeAreaLevel1 == :city")
    LiveData<List<PlaceWithHistoryDetailEntity>> getPlacesByCity(String city);

    @Transaction
    @Query("SELECT p.* " +
            "FROM Place as p " +
            "LEFT JOIN PlaceHistory as ph on ph.PlaceId = p.Id " +
            "GROUP BY p.Id " +
            "ORDER BY ph.EnterTime DESC " +
            "LIMIT 0, 30")
    LiveData<List<PlaceWithHistoryDetailEntity>> getRecentVisitedPlaces();

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
            "LIMIT 0, 30")
    LiveData<List<PlaceWithHistoryDetailEntity>> getMostVisitedPlaces();

    @Query("SELECT DISTINCT AdministrativeAreaLevel1 " +
            "FROM Place " +
            "WHERE AdministrativeAreaLevel1 IS NOT NULL AND AdministrativeAreaLevel1 != ''")
    LiveData<List<String>> getVisitedCities();

    @Query("SELECT DISTINCT AdministrativeAreaLevel1 " +
            "FROM Place " +
            "WHERE Country == :country")
    LiveData<List<String>> getVisitedCitiesByCountry(String country);

    @Query("SELECT DISTINCT Country " +
            "FROM Place " +
            "WHERE Country IS NOT NULL AND Country != ''")
    LiveData<List<String>> getVisitedCountries();

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
    LiveData<PlaceWithHistoryDetailEntity> getPlaceWithHistoriesByIdAsync(String id);

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
