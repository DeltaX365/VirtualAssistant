package com.vnsolution.virtualassistant.db.entity;

import androidx.room.ColumnInfo;

public class PhoneCallSummaryEntity {
    @ColumnInfo(name = "Type")
    public int type;

    @ColumnInfo(name = "Total")
    public int total;

    @ColumnInfo(name = "Duration")
    public int duration;
}
