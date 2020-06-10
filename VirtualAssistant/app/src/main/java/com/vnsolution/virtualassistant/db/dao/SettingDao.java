package com.vnsolution.virtualassistant.db.dao;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.SettingEntity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Dao
public abstract class SettingDao implements BaseDao<SettingEntity> {
    @Query("SELECT * FROM Setting")
    abstract LiveData<List<SettingEntity>> getAll();
}
