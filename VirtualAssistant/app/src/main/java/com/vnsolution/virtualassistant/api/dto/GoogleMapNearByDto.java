package com.vnsolution.virtualassistant.api.dto;

import java.util.List;

public class GoogleMapNearByDto {
    public List<String> htmlAttributions;
    public String nextPageToken;
    public List<NearBy> results;
    public String status;

    public class NearBy {
        public String businessStatus;
        public Geometry geometry;
        public String icon;
        public String id;
        public String name;
        public String placeId;
        public PlusCode plusCode;
        public String reference;
        public String scope;
        public List<String> types;
        public int userRatingsTotal;
        public String vicinity;

        public class Geometry {
            public Location location;
            public Viewport viewport;

            public class Location {
                public double lat;
                public double lng;
            }

            public class Viewport {
                public Northeast northeast;
                public Southwest southwest;

                public class Northeast {
                    public double lat;
                    public double lng;
                }

                public class Southwest {
                    public double lat;
                    public double lng;
                }
            }
        }

        public class PlusCode {
            public String compoundCode;
            public String globalCode;
        }
    }
}
