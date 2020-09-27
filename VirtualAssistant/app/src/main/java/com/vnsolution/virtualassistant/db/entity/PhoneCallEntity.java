package com.vnsolution.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(
    tableName = "PhoneCall",
    indices = {@Index(value = "CallId", unique = true)}
)
public class PhoneCallEntity extends AuditEntity {
    @ColumnInfo(name = "CallId")
    public int callId;

    @ColumnInfo(name = "PhoneNumber")
    public String phoneNumber;

    @ColumnInfo(name = "Type")
    public int type;

    @ColumnInfo(name = "GeocodedLocation")
    public String geocodedLocation;

    @ColumnInfo(name = "Presentation")
    public int presentation;

    @ColumnInfo(name = "Duration")
    public long duration;

//    @ColumnInfo(name = "SubscriptionId")
//    public long subscriptionId;

    @ColumnInfo(name = "IsRead")
    public int isRead;

    @ColumnInfo(name = "Date")
    public long date;

    @ColumnInfo(name = "Features")
    public int features;

    @ColumnInfo(name = "NormalizedNumber")
    public String normalizedNumber;

    @ColumnInfo(name = "LastModified")
    public long lastModified;

    @ColumnInfo(name = "IsNew")
    public int isNew;

//    @ColumnInfo(name = "PhoneAccountAddress")
//    public String phoneAccountAddress;

    @ColumnInfo(name = "FormattedNumber")
    public String formattedNumber;

    @ColumnInfo(name = "BlockReason")
    public int blockReason;

    @ColumnInfo(name = "CountryIso")
    public String countryIso;
}
