package com.vnsolution.virtual_assistant.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.vnsolution.virtual_assistant.db.convertor.BooleanConverter;
import com.vnsolution.virtual_assistant.db.convertor.DateConverter;
import com.vnsolution.virtual_assistant.db.dao.PhoneCallDao;
import com.vnsolution.virtual_assistant.db.dao.DeviceDao;
import com.vnsolution.virtual_assistant.db.dao.GeolocationHistoryDao;
import com.vnsolution.virtual_assistant.db.dao.PhoneAppDao;
import com.vnsolution.virtual_assistant.db.dao.PhoneAppUsageDao;
import com.vnsolution.virtual_assistant.db.dao.PhoneContactDao;
import com.vnsolution.virtual_assistant.db.dao.PhoneSmsDao;
import com.vnsolution.virtual_assistant.db.dao.PlaceDao;
import com.vnsolution.virtual_assistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtual_assistant.db.dao.SettingDao;
import com.vnsolution.virtual_assistant.db.dao.WeatherDao;
import com.vnsolution.virtual_assistant.db.entity.PhoneCallEntity;
import com.vnsolution.virtual_assistant.db.entity.DeviceEntity;
import com.vnsolution.virtual_assistant.db.entity.GeolocationHistoryEntity;
import com.vnsolution.virtual_assistant.db.entity.PhoneAppEntity;
import com.vnsolution.virtual_assistant.db.entity.PhoneAppUsageEntity;
import com.vnsolution.virtual_assistant.db.entity.PhoneContactEntity;
import com.vnsolution.virtual_assistant.db.entity.PhoneSmsEntity;
import com.vnsolution.virtual_assistant.db.entity.PlaceEntity;
import com.vnsolution.virtual_assistant.db.entity.PlaceHistoryEntity;
import com.vnsolution.virtual_assistant.db.entity.SettingEntity;
import com.vnsolution.virtual_assistant.db.entity.WeatherEntity;


@Database(
    entities = {
        DeviceEntity.class,
        GeolocationHistoryEntity.class,
        PlaceEntity.class,
        PlaceHistoryEntity.class,
        WeatherEntity.class,
        PhoneCallEntity.class,
        PhoneSmsEntity.class,
        PhoneContactEntity.class,
        PhoneAppEntity.class,
        PhoneAppUsageEntity.class,
        SettingEntity.class
    },
    version = 4,
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
    public abstract PhoneCallDao phoneCallDao();
    public abstract PhoneSmsDao phoneSmsDao();
    public abstract PhoneContactDao phoneContactDao();
    public abstract PhoneAppDao phoneAppDao();
    public abstract PhoneAppUsageDao phoneAppUsageDao();
    public abstract SettingDao settingDao();
}
