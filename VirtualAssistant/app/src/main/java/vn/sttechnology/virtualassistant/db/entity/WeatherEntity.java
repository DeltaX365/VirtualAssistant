package vn.sttechnology.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Weather")
public class WeatherEntity extends AuditEntity {
    @ColumnInfo(name = "Longitude")
    public double longitude;

    @ColumnInfo(name = "Latitude")
    public double latitude;

    @ColumnInfo(name = "Weather")
    public String weather;

    @ColumnInfo(name = "WeatherDescription")
    public String weatherDescription;

    @ColumnInfo(name = "WeatherIcon")
    public String weatherIcon;

    @ColumnInfo(name = "Temp")
    public int temp;

    @ColumnInfo(name = "TempFeelsLike")
    public double tempFeelsLike;

    @ColumnInfo(name = "TempMin")
    public int tempMin;

    @ColumnInfo(name = "TempMax")
    public int tempMax;

    @ColumnInfo(name = "Pressure")
    public int pressure;

    @ColumnInfo(name = "Humidity")
    public int humidity;

    @ColumnInfo(name = "Visibility")
    public int visibility;

    @ColumnInfo(name = "WindSpeed")
    public double windSpeed;

    @ColumnInfo(name = "WindDeg")
    public int windDeg;

    @ColumnInfo(name = "Sunrise")
    public int sunrise;

    @ColumnInfo(name = "Sunset")
    public int sunset;

    @ColumnInfo(name = "Timezone")
    public int timezone;

    @ColumnInfo(name = "Name")
    public String name;

    @ColumnInfo(name = "Cod")
    public int cod;
}
