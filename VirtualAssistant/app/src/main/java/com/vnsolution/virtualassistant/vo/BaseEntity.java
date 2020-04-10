package com.vnsolution.virtualassistant.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class BaseEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Id")
    public String id;
}
