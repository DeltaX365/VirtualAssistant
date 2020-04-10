package com.vnsolution.virtualassistant.vo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

@Entity(tableName = "PlaceHistory",
    foreignKeys = {
        @ForeignKey(
            entity = Place.class,
            parentColumns = "Id",
            childColumns = "PlaceId",
            onDelete = ForeignKey.NO_ACTION
        )
    },
    indices = { @Index(value = "PlaceId")}
)
public class PlaceHistory extends BaseEntity {
    @ColumnInfo(name = "PlaceId")
    public String placeId;

    @ColumnInfo(name = "EnterTime")
    public Date enterTime;

    @ColumnInfo(name = "ExistTime")
    public Date existTime;
}
