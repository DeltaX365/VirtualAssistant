package vn.sttechnology.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import vn.sttechnology.virtualassistant.db.entity.GeolocationHistoryEntity;

import java.util.List;

@Dao
public interface GeolocationHistoryDao extends BaseDao<GeolocationHistoryEntity> {
    @Query("SELECT * FROM GeolocationHistory")
    LiveData<List<GeolocationHistoryEntity>> getAll();

    @Query("DELETE FROM GeolocationHistory")
    void reset();
}
