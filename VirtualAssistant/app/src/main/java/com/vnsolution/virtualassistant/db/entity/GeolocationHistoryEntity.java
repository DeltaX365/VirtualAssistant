package com.vnsolution.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "GeolocationHistory")
public class GeolocationHistoryEntity extends BaseEntity {
    @ColumnInfo(name = "Latitude")
    public double latitude;

    @ColumnInfo(name = "Longitude")
    public double longitude;

    @ColumnInfo(name = "Accuracy")
    public int accuracy;

    @ColumnInfo(name = "Altitude")
    public int altitude;

    @ColumnInfo(name = "Bearing")
    public double bearing;

    @ColumnInfo(name = "IsFromMockProvider")
    public boolean isFromMockProvider;

    @ColumnInfo(name = "Provider")
    public String provider;

    @ColumnInfo(name = "Speed")
    public double speed;

    @ColumnInfo(name = "RecordTime")
    public Date recordTime;
}
