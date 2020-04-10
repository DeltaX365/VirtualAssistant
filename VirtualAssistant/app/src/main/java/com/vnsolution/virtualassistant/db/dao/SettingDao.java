package com.vnsolution.virtualassistant.db.dao;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.vo.Setting;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Dao
public abstract class SettingDao implements BaseDao<Setting> {
    @Query("SELECT * FROM Setting")
    abstract List<Setting> getAll();

    @RequiresApi(api = Build.VERSION_CODES.N)
    Optional<Setting> findByName(final String name) {
        return getAll().stream().filter(new Predicate<Setting>() {
            @Override
            public boolean test(Setting s) {
                return s.name.equals(name);
            }
        }).findFirst();
    }
}
