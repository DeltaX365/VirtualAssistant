package com.vnsolution.virtualassistant.db.entity;

import androidx.room.ColumnInfo;

public class PlaceSummaryEntity {
    @ColumnInfo(name = "TotalPlaceYesterday")
    public int totalPlaceYesterday;

    @ColumnInfo(name = "TotalPlace")
    public int totalPlace;

    @ColumnInfo(name = "TotalCity")
    public int totalCity;

    @ColumnInfo(name = "TotalCountry")
    public int totalCountry;
}
