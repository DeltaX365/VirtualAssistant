package com.vnsolution.virtualassistant.vo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Device")
public class Device extends AuditEntity {
    @ColumnInfo(name = "AvailableLocationProviders")
    public String availableLocationProviders;

    @ColumnInfo(name = "BatteryLevel")
    public String batteryLevel;

    @ColumnInfo(name = "Carrier")
    public String carrier;

    @ColumnInfo(name = "FreeDiskStorage")
    public String freeDiskStorage;

    @ColumnInfo(name = "UsedMemory")
    public String usedMemory;

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
