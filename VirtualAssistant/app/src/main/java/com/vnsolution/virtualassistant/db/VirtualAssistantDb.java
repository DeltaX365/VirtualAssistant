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
import com.vnsolution.virtualassistant.vo.Device;
import com.vnsolution.virtualassistant.vo.GeolocationHistory;
import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.PlaceHistory;
import com.vnsolution.virtualassistant.vo.Setting;
import com.vnsolution.virtualassistant.vo.Weather;


@Database(
    entities = {
        Device.class,
        GeolocationHistory.class,
        Place.class,
        PlaceHistory.class,
        Weather.class,
        Setting.class
    },
    version = 1,
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
