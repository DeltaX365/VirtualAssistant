package com.vnsolution.virtualassistant.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * {
 *    "html_attributions": [],
 *    "result": {
 *       "address_components": [
 *          {
 *             "long_name": "1",
 *             "short_name": "1",
 *             "types": [
 *                "street_number"
 *             ]
 *          },
 *          {
 *             "long_name": "Cát Linh",
 *             "short_name": "Cát Linh",
 *             "types": [
 *                "route"
 *             ]
 *          },
 *          {
 *             "long_name": "Đống Đa",
 *             "short_name": "Đống Đa",
 *             "types": [
 *                "administrative_area_level_2",
 *                "political"
 *             ]
 *          },
 *          {
 *             "long_name": "Hà Nội",
 *             "short_name": "Hà Nội",
 *             "types": [
 *                "administrative_area_level_1",
 *                "political"
 *             ]
 *          },
 *          {
 *             "long_name": "Vietnam",
 *             "short_name": "VN",
 *             "types": [
 *                "country",
 *                "political"
 *             ]
 *          }
 *       ],
 *       "adr_address": "<span class=\"street-address\">1 Cát Linh</span>, <span class=\"extended-address\">Quốc Tử Giám</span>, <span class=\"locality\">Đống Đa</span>, <span class=\"region\">Hà Nội</span>, <span class=\"country-name\">Vietnam</span>",
 *       "formatted_address": "1 Cát Linh, Quốc Tử Giám, Đống Đa, Hà Nội, Vietnam",
 *       "geometry": {
 *          "location": {
 *             "lat": 21.0277082,
 *             "lng": 105.8339552
 *          },
 *          "viewport": {
 *             "northeast": {
 *                "lat": 21.0290811802915,
 *                "lng": 105.8353168802915
 *             },
 *             "southwest": {
 *                "lat": 21.02638321970849,
 *                "lng": 105.8326189197085
 *             }
 *          }
 *       },
 *       "icon": "https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png",
 *       "id": "5198e4d60a6358c81ed6b87302e7f57240edad70",
 *       "name": "Pet's House",
 *       "place_id": "ChIJF8D7uJ6rNTERmgl8MqsfPN4",
 *       "plus_code": {
 *          "compound_code": "2RHM+3H Quốc Tử Giám, Đống Đa, Hanoi, Vietnam",
 *          "global_code": "7PH72RHM+3H"
 *       },
 *       "rating": 5,
 *       "reference": "ChIJF8D7uJ6rNTERmgl8MqsfPN4",
 *       "scope": "GOOGLE",
 *       "types": [
 *          "point_of_interest",
 *          "establishment"
 *       ],
 *       "url": "https://maps.google.com/?cid=16013709195169499546",
 *       "user_ratings_total": 1,
 *       "utc_offset": 420,
 *       "vicinity": "1 Cát Linh, Quốc Tử Giám"
 *    },
 *    "status": "OK"
 * }
 */
public class GoogleMapPlaceDetailDto {
    @SerializedName("html_attributions")
    public List<String> htmlAttributions;

    @SerializedName("result")
    public PlaceDetail result;

    @SerializedName("status")
    public String status;

    public class PlaceDetail {
        @SerializedName("address_components")
        public List<AddressComponent> addressComponents;

        @SerializedName("adr_address")
        public String adrAddress;

        @SerializedName("formatted_address")
        public String formattedAddress;

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

        @SerializedName("rating")
        public double rating;

        @SerializedName("reference")
        public String reference;

        @SerializedName("scope")
        public String scope;

        @SerializedName("types")
        public List<String> types;

        @SerializedName("url")
        public String url;

        @SerializedName("user_ratings_total")
        public double userRatingsTotal;

        @SerializedName("utc_offset")
        public int utcOffset;

        @SerializedName("vicinity")
        public String vicinity;

        @SerializedName("photos")
        public List<Photo> photos;

        public class AddressComponent {
            @SerializedName("long_name")
            public String longName;

            @SerializedName("short_name")
            public String shortName;

            @SerializedName("types")
            public List<String> types;
        }

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

        public class Photo {
            @SerializedName("photo_reference")
            public String photoReference;

            @SerializedName("width")
            public int width;

            @SerializedName("height")
            public int height;

            @SerializedName("html_attributions")
            public List<String> htmlAttributions;
        }
    }
}
