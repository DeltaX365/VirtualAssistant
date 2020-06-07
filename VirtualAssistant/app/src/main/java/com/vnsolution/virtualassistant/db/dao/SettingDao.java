package com.vnsolution.virtualassistant.db.dao;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.SettingEntity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Dao
public abstract class SettingDao implements BaseDao<SettingEntity> {
    @Query("SELECT * FROM Setting")
    abstract List<SettingEntity> getAll();

    @RequiresApi(api = Build.VERSION_CODES.N)
    Optional<SettingEntity> findByName(final String name) {
        return getAll().stream().filter(new Predicate<SettingEntity>() {
            @Override
            public boolean test(SettingEntity s) {
                return s.name.equals(name);
            }
        }).findFirst();
    }
}
