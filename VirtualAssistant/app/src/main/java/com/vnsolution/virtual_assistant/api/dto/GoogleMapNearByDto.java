package com.vnsolution.virtual_assistant.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleMapNearByDto {
    public List<String> htmlAttributions;
    public String nextPageToken;
    public List<NearBy> results;
    public String status;

    public class NearBy {
        @SerializedName("business_status")
        public String businessStatus;

        @SerializedName("geometry")
        public Geometry geometry;

        @SerializedName("icon")
        public String icon;

        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;

        @SerializedName("place_id")
        public String placeId;

        @SerializedName("plus_code")
        public PlusCode plusCode;

        @SerializedName("reference")
        public String reference;

        @SerializedName("scope")
        public String scope;

        @SerializedName("types")
        public List<String> types;

        @SerializedName("user_ratings_total")
        public int userRatingsTotal;

        @SerializedName("vicinity")
        public String vicinity;

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
