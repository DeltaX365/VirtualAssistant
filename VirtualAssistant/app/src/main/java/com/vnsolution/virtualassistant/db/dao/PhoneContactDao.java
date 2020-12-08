package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PhoneContactEntity;

import java.util.List;

@Dao
public interface PhoneContactDao extends BaseDao<PhoneContactEntity> {
    @Query("SELECT * FROM PhoneContact ORDER BY DisplayName ASC")
    List<PhoneContactEntity> getAll();

    @Query("SELECT * FROM PhoneContact WHERE Id = :id")
    PhoneContactEntity getById(String id);

    @Query("DELETE FROM PhoneContact")
    void reset();
}
