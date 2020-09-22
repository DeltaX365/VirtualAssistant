package com.vnsolution.virtual_assistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtual_assistant.db.entity.PhoneSmsEntity;

import java.util.List;

@Dao
public interface PhoneSmsDao extends BaseDao<PhoneSmsEntity> {
    @Query("SELECT * FROM PhoneSms")
    List<PhoneSmsEntity> getAll();

    @Query("DELETE FROM PhoneSms")
    void reset();
}
