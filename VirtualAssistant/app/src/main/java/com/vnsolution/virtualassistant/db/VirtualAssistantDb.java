package com.vnsolution.virtualassistant.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.vnsolution.virtualassistant.db.convertor.BooleanConverter;
import com.vnsolution.virtualassistant.db.convertor.DateConverter;
import com.vnsolution.virtualassistant.db.dao.DeviceDao;
import com.vnsolution.virtualassistant.db.dao.GeolocationHistoryDao;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.db.dao.SettingDao;
import com.vnsolution.virtualassistant.db.dao.WeatherDao;
import com.vnsolution.virtualassistant.db.entity.DeviceEntity;
import com.vnsolution.virtualassistant.db.entity.GeolocationHistoryEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceEntity;
import com.vnsolution.virtualassistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtualassistant.db.entity.SettingEntity;
import com.vnsolution.virtualassistant.db.entity.WeatherEntity;


@Database(
    entities = {
        DeviceEntity.class,
        GeolocationHistoryEntity.class,
        PlaceEntity.class,
        PlaceHistoryEntity.class,
        WeatherEntity.class,
        SettingEntity.class
    },
    version = 2,
    exportSchema = false
)
@TypeConverters({
    BooleanConverter.class,
    DateConverter.class
})
public abstract class VirtualAssistantDb extends RoomDatabase {
    public abstract DeviceDao deviceDao();
    public abstract GeolocationHistoryDao geolocationHistoryDao();
    public abstract PlaceDao placeDao();
    public abstract PlaceHistoryDao placeHistoryDao();
    public abstract WeatherDao weatherDao();
    public abstract SettingDao settingDao();
}
