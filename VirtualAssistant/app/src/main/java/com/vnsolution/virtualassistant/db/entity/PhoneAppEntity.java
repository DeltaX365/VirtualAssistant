package com.vnsolution.virtualassistant.db.entity;

import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "PhoneApp")
public class PhoneAppEntity extends AuditEntity {
    @ColumnInfo(name = "AppName")
    public String appName;

    @ColumnInfo(name = "PackageName")
    public String packageName;

    @ColumnInfo(name = "VersionName")
    public String versionName;

    @ColumnInfo(name = "VersionCode")
    public int versionCode;

    @ColumnInfo(name = "AppIcon")
    public String appIcon;

    @ColumnInfo(name = "AppLastUsedTime")
    public long appLastUsedTime;

    @ColumnInfo(name = "AppUsageTime")
    public long appUsageTime;

    @ColumnInfo(name = "QueryBeginTime")
    public long queryBeginTime;

    @ColumnInfo(name = "QueryEndTime")
    public long queryEndTime;
}
