package com.vnsolution.virtual_assistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtual_assistant.db.entity.PhoneCallEntity;

import java.util.List;

@Dao
public interface PhoneCallDao extends BaseDao<PhoneCallEntity> {
    @Query("SELECT * FROM PhoneCall")
    List<PhoneCallEntity> getAll();

    @Query("DELETE FROM PhoneCall")
    void reset();
}
