package com.vnsolution.virtual_assistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtual_assistant.db.entity.PhoneContactEntity;

import java.util.List;

@Dao
public interface PhoneContactDao extends BaseDao<PhoneContactEntity> {
    @Query("SELECT * FROM PhoneContact")
    List<PhoneContactEntity> getAll();

    @Query("DELETE FROM PhoneContact")
    void reset();
}
