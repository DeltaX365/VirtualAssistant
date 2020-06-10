package com.vnsolution.virtualassistant.model;

import java.util.Date;

public class PlaceHistory {
    public String placeId;
    public Date enterTime;
    public Date existTime;
    public Place place;

    public class Place {
        public String ggPlaceId;
        public String formattedAddress;
        public String formattedAddressInHtml;
        public double latitude;
        public double longitude;
        public String plusCodeCompound;
        public String plusCodeGlobal;
        public String types;
        public String icon;
        public String placeName;
        public int rating;
        public String reference;
        public String scope;
        public String url;
        public int userRatingsTotal;
        public String utcOffset;
        public String vicinity;
    }
}
