package vn.sttechnology.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import vn.sttechnology.virtualassistant.db.entity.PlaceHistoryEntity;
import vn.sttechnology.virtualassistant.db.entity.PlaceHistoryWithDetailEntity;

import java.util.List;

@Dao
public interface PlaceHistoryDao extends BaseDao<PlaceHistoryEntity> {
    @Query("SELECT * FROM PlaceHistory")
    LiveData<List<PlaceHistoryEntity>> getAll();

    @Transaction
    @Query("SELECT * FROM PlaceHistory")
    LiveData<List<PlaceHistoryWithDetailEntity>> getPlaceHistoriesWithDetail();

    @Query("UPDATE PlaceHistory SET PlaceId = :toPlaceId WHERE PlaceId = :fromPlaceId")
    void transfer(String fromPlaceId, String toPlaceId);

    @Query("DELETE FROM PlaceHistory WHERE PlaceId = :placeId")
    void delete(String placeId);

    @Query("DELETE FROM PlaceHistory")
    void reset();
}
