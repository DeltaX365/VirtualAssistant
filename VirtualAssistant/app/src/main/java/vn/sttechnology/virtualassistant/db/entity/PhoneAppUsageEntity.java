package vn.sttechnology.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "PhoneAppUsage")
public class PhoneAppUsageEntity extends AuditEntity {
    @ColumnInfo(name = "AppId")
    public String appId;

    @ColumnInfo(name = "AppLastUsedTime")
    public long appLastUsedTime;

    @ColumnInfo(name = "AppUsageTime")
    public long appUsageTime;

    @ColumnInfo(name = "QueryBeginTime")
    public long queryBeginTime;

    @ColumnInfo(name = "QueryEndTime")
    public long queryEndTime;
}
