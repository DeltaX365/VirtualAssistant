package vn.sttechnology.virtualassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import vn.sttechnology.virtualassistant.db.entity.DeviceEntity;

import java.util.List;

@Dao
public interface DeviceDao extends BaseDao<DeviceEntity> {
    @Query("SELECT * FROM Device")
    LiveData<List<DeviceEntity>> getAll();

    @Query("DELETE FROM Device")
    void reset();
}
