package vn.sttechnology.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import vn.sttechnology.virtualassistant.db.entity.PhoneSmsEntity;

import java.util.List;

@Dao
public interface PhoneSmsDao extends BaseDao<PhoneSmsEntity> {
    @Query("SELECT * FROM PhoneSms ORDER BY Date DESC")
    List<PhoneSmsEntity> getAll();

    @Query("DELETE FROM PhoneSms")
    void reset();
}
