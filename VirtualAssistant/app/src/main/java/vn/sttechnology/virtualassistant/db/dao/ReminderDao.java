package vn.sttechnology.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import vn.sttechnology.virtualassistant.db.entity.ReminderEntity;

import java.util.List;

@Dao
public interface ReminderDao extends BaseDao<ReminderEntity> {
    @Query("SELECT * FROM Reminder ORDER BY Date ASC")
    List<ReminderEntity> getAll();

    @Query("SELECT * FROM Reminder WHERE Id=:id")
    ReminderEntity getById(String id);
}
