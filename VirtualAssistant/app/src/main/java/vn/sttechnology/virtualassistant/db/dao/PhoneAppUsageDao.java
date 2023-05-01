package vn.sttechnology.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import vn.sttechnology.virtualassistant.db.entity.PhoneAppUsageEntity;

import java.util.List;

@Dao
public interface PhoneAppUsageDao extends BaseDao<PhoneAppUsageEntity> {
    @Query("SELECT * FROM PhoneAppUsage")
    List<PhoneAppUsageEntity> getAll();

    @Query("DELETE FROM PhoneAppUsage")
    void reset();
}
