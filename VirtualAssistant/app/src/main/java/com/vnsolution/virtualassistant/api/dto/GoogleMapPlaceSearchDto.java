package com.vnsolution.virtualassistant.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleMapPlaceSearchDto {
    @SerializedName("candidates")
    public List<PlaceSearch> candidates;

    @SerializedName("status")
    public String status;

    public class PlaceSearch {
        @SerializedName("business_status")
        public String businessStatus;

        @SerializedName("formatted_address")
        public String formattedAddress;

        @SerializedName("geometry")
        public Geometry geometry;

        @SerializedName("icon")
        public String icon;

        @SerializedName("name")
        public String name;

        @SerializedName("place_id")
        public String placeId;

        @SerializedName("plus_code")
        public PlusCode plusCode;

        @SerializedName("types")
        public List<String> types;

        @SerializedName("rating")
        public int rating;

        public class Geometry {
            @SerializedName("location")
            public Location location;

            @SerializedName("viewport")
            public Viewport viewport;

            public class Location {
                @SerializedName("lat")
                public double lat;

                @SerializedName("lng")
                public double lng;
            }

            public class Viewport {
                @SerializedName("northeast")
                public Northeast northeast;

                @SerializedName("southwest")
                public Southwest southwest;

                public class Northeast {
                    @SerializedName("lat")
                    public double lat;

                    @SerializedName("lng")
                    public double lng;
                }

                public class Southwest {
                    @SerializedName("lat")
                    public double lat;

                    @SerializedName("lng")
                    public double lng;
                }
            }
        }

        public class PlusCode {
            @SerializedName("compound_code")
            public String compoundCode;

            @SerializedName("global_code")
            public String globalCode;
        }
    }
}
