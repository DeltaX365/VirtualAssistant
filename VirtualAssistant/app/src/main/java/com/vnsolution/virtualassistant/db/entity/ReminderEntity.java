package com.vnsolution.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "Reminder")
public class ReminderEntity extends AuditEntity {
    @ColumnInfo(name = "Title")
    public String name;

    @ColumnInfo(name = "Date")
    public Date date;

    @ColumnInfo(name = "Repeat")
    public String repeat;

    @ColumnInfo(name = "Action")
    public String action;

    @ColumnInfo(name = "ExtendedData")
    public String extendedData;

    @ColumnInfo(name = "AlarmRequestCode")
    public int alarmRequestCode;
}
