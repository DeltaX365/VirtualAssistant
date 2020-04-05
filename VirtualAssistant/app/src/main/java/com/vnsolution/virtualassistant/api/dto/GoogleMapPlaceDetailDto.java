package com.vnsolution.virtualassistant.api.dto;

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
    public List<String> htmlAttributions;
    public List<PlaceDetail> results;
    public String status;

    public class PlaceDetail {
        public List<AddressComponent> addressComponents;
        public String adrAddress;
        public String formattedAddress;
        public Geometry geometry;
        public String icon;
        public String id;
        public String name;
        public String placeId;
        public PlusCode plusCode;
        public int rating;
        public String reference;
        public String scope;
        public List<String> types;
        public String url;
        public int userRatingsTotal;
        public int utcOffset;
        public String vicinity;

        public class AddressComponent {
            public String longName;
            public String shortName;
            public List<String> types;
        }

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
