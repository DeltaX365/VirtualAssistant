package com.vnsolution.virtualassistant.vo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Place")
public class Place extends AuditEntity {
    @ColumnInfo(name = "GGPlaceId")
    public String ggPlaceId;

    @ColumnInfo(name = "FormattedAddress")
    public String formattedAddress;

    @ColumnInfo(name = "FormattedAddressInHtml")
    public String formattedAddressInHtml;

    @ColumnInfo(name = "Latitude")
    public Double latitude;

    @ColumnInfo(name = "Longitude")
    public Double longitude;

    @ColumnInfo(name = "PlusCodeCompound")
    public String plusCodeCompound;

    @ColumnInfo(name = "PlusCodeGlobal")
    public String plusCodeGlobal;

    @ColumnInfo(name = "Types")
    public String types;

    @ColumnInfo(name = "Icon")
    public String icon;

    @ColumnInfo(name = "GGPlaceId")
    public String Name;

    @ColumnInfo(name = "Rating")
    public int rating;

    @ColumnInfo(name = "Reference")
    public String reference;

    @ColumnInfo(name = "Scope")
    public String scope;

    @ColumnInfo(name = "Url")
    public String url;

    @ColumnInfo(name = "UserRatingsTotal")
    public int userRatingsTotal;

    @ColumnInfo(name = "UtcOffset")
    public String utcOffset;

    @ColumnInfo(name = "Vicinity")
    public String vicinity;
}
