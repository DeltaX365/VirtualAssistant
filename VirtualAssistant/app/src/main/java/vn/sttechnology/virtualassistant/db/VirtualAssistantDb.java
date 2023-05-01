package vn.sttechnology.virtualassistant.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import vn.sttechnology.virtualassistant.db.convertor.BooleanConverter;
import vn.sttechnology.virtualassistant.db.convertor.DateConverter;
import vn.sttechnology.virtualassistant.db.dao.PhoneCallDao;
import vn.sttechnology.virtualassistant.db.dao.DeviceDao;
import vn.sttechnology.virtualassistant.db.dao.GeolocationHistoryDao;
import vn.sttechnology.virtualassistant.db.dao.PhoneAppDao;
import vn.sttechnology.virtualassistant.db.dao.PhoneAppUsageDao;
import vn.sttechnology.virtualassistant.db.dao.PhoneContactDao;
import vn.sttechnology.virtualassistant.db.dao.PhoneSmsDao;
import vn.sttechnology.virtualassistant.db.dao.PlaceDao;
import vn.sttechnology.virtualassistant.db.dao.PlaceHistoryDao;
import vn.sttechnology.virtualassistant.db.dao.ReminderDao;
import vn.sttechnology.virtualassistant.db.dao.SettingDao;
import vn.sttechnology.virtualassistant.db.dao.WeatherDao;
import vn.sttechnology.virtualassistant.db.entity.PhoneCallEntity;
import vn.sttechnology.virtualassistant.db.entity.DeviceEntity;
import vn.sttechnology.virtualassistant.db.entity.GeolocationHistoryEntity;
import vn.sttechnology.virtualassistant.db.entity.PhoneAppEntity;
import vn.sttechnology.virtualassistant.db.entity.PhoneAppUsageEntity;
import vn.sttechnology.virtualassistant.db.entity.PhoneContactEntity;
import vn.sttechnology.virtualassistant.db.entity.PhoneSmsEntity;
import vn.sttechnology.virtualassistant.db.entity.PlaceEntity;
import vn.sttechnology.virtualassistant.db.entity.PlaceHistoryEntity;
import vn.sttechnology.virtualassistant.db.entity.ReminderEntity;
import vn.sttechnology.virtualassistant.db.entity.SettingEntity;
import vn.sttechnology.virtualassistant.db.entity.WeatherEntity;

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
        ReminderEntity.class,
        SettingEntity.class
    },
    version = 14,
    exportSchema = true
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
    public abstract ReminderDao reminderDao();
    public abstract SettingDao settingDao();
}
