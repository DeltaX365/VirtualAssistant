package vn.sttechnology.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Device")
public class DeviceEntity extends AuditEntity {
    @ColumnInfo(name = "AvailableLocationProviders")
    public String availableLocationProviders;

    @ColumnInfo(name = "BatteryLevel")
    public String batteryLevel;

    @ColumnInfo(name = "Carrier")
    public String carrier;

    @ColumnInfo(name = "FreeDiskStorage")
    public double freeDiskStorage;

    @ColumnInfo(name = "UsedMemory")
    public double usedMemory;

    @ColumnInfo(name = "Host")
    public String host;

    @ColumnInfo(name = "IpAddress")
    public String ipAddress;

    @ColumnInfo(name = "MacAddress")
    public String macAddress;

    @ColumnInfo(name = "Manufacturer")
    public String manufacturer;

    @ColumnInfo(name = "PhoneNumber")
    public String phoneNumber;

    @ColumnInfo(name = "PowerState")
    public String powerState;
}
