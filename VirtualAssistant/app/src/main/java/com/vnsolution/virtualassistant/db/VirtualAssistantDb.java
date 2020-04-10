package com.vnsolution.virtualassistant.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.vnsolution.virtualassistant.db.convertor.BooleanConverter;
import com.vnsolution.virtualassistant.db.convertor.DateConverter;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.SettingDao;
import com.vnsolution.virtualassistant.vo.GeoHistory;
import com.vnsolution.virtualassistant.vo.Place;
import com.vnsolution.virtualassistant.vo.PlaceHistory;
import com.vnsolution.virtualassistant.vo.Setting;


@Database(
    entities = {
        Place.class,
        PlaceHistory.class,
        GeoHistory.class,
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

    public abstract PlaceDao placeDao();

    public abstract SettingDao settingDao();
}
