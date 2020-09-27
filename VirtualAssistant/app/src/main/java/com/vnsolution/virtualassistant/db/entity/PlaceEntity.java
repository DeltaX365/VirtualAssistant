package com.vnsolution.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Place")
public class PlaceEntity extends AuditEntity {
    @ColumnInfo(name = "GGPlaceId")
    public String ggPlaceId;

    @ColumnInfo(name = "FormattedAddress")
    public String formattedAddress;

    @ColumnInfo(name = "FormattedAddressInHtml")
    public String formattedAddressInHtml;

    @ColumnInfo(name = "Latitude")
    public double latitude;

    @ColumnInfo(name = "Longitude")
    public double longitude;

    @ColumnInfo(name = "PlusCodeCompound")
    public String plusCodeCompound;

    @ColumnInfo(name = "PlusCodeGlobal")
    public String plusCodeGlobal;

    @ColumnInfo(name = "Types")
    public String types;

    @ColumnInfo(name = "Icon")
    public String icon;

    @ColumnInfo(name = "Name")
    public String placeName;

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

    @ColumnInfo(name = "StreetNumber")
    public String streetNumber;

    @ColumnInfo(name = "Route")
    public String route;

    @ColumnInfo(name = "AdministrativeAreaLevel3")
    public String administrativeAreaLevel3;

    @ColumnInfo(name = "AdministrativeAreaLevel2")
    public String administrativeAreaLevel2;

    @ColumnInfo(name = "AdministrativeAreaLevel1")
    public String administrativeAreaLevel1;

    @ColumnInfo(name = "Country")
    public String country;
}
