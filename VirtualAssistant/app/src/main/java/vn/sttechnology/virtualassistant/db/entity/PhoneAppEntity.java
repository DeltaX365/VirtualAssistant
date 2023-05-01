package vn.sttechnology.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.Date;

@Entity(
    tableName = "PhoneApp",
    indices = {@Index(value = "AppId", unique = true)}
)
public class PhoneAppEntity extends AuditEntity {
    @ColumnInfo(name = "AppId")
    public int appId;

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

    @ColumnInfo(name = "Type")
    public int type;

    @ColumnInfo(name = "Category")
    public String category;

    @ColumnInfo(name = "Locked")
    public int locked;

    @ColumnInfo(name = "LockedBeginTime")
    public Date lockedBeginTime;

    @ColumnInfo(name = "LockedEndTime")
    public Date lockedEndTime;
}
