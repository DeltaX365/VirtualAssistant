package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PhoneCallEntity;

import java.util.List;

@Dao
public interface PhoneCallDao extends BaseDao<PhoneCallEntity> {
    @Query("SELECT * FROM PhoneCall")
    List<PhoneCallEntity> getAll();
}
