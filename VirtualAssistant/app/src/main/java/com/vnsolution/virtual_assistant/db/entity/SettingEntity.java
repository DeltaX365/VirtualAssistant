package com.vnsolution.virtual_assistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Setting")
public class SettingEntity extends AuditEntity {
    @ColumnInfo(name = "Name")
    public String name;

    @ColumnInfo(name = "Value")
    public String value;

    @ColumnInfo(name = "Description")
    public String desc;
}
