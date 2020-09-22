package com.vnsolution.virtual_assistant.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class BaseEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Id")
    public String id;
}
