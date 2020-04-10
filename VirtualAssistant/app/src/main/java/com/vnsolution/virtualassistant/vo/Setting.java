package com.vnsolution.virtualassistant.vo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Setting")
public class Setting extends AuditEntity {
    @ColumnInfo(name = "Name")
    public String name;

    @ColumnInfo(name = "Value")
    public String value;

    @ColumnInfo(name = "Description")
    public String desc;
}
