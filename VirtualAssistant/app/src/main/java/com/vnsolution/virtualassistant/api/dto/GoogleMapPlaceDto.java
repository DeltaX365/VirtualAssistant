package com.vnsolution.virtualassistant.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * {
 *    "plus_code": {
 *       "compound_code": "2RHM+4J Hanoi, Hoàn Kiếm, Hanoi, Vietnam",
 *       "global_code": "7PH72RHM+4J"
 *    },
 *    "results": [
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "3",
 *                "short_name": "3",
 *                "types": [
 *                   "street_number"
 *                ]
 *             },
 *             {
 *                "long_name": "Cát Linh",
 *                "short_name": "Cát Linh",
 *                "types": [
 *                   "route"
 *                ]
 *             },
 *             {
 *                "long_name": "Đống Đa",
 *                "short_name": "Đống Đa",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hà Nội",
 *                "short_name": "Hà Nội",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "3 Cát Linh, Quốc Tử Giám, Đống Đa, Hà Nội, Vietnam",
 *          "geometry": {
 *             "location": {
 *                "lat": 21.02784,
 *                "lng": 105.834014
 *             },
 *             "location_type": "ROOFTOP",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.0291889802915,
 *                   "lng": 105.8353629802915
 *                },
 *                "southwest": {
 *                   "lat": 21.02649101970849,
 *                   "lng": 105.8326650197085
 *                }
 *             }
 *          },
 *          "place_id": "ChIJFRF5uJ6rNTERE5yUtmkSAk0",
 *          "plus_code": {
 *             "compound_code": "2RHM+4J Quốc Tử Giám, Đống Đa, Hanoi, Vietnam",
 *             "global_code": "7PH72RHM+4J"
 *          },
 *          "types": [
 *             "street_address"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "1",
 *                "short_name": "1",
 *                "types": [
 *                   "street_number"
 *                ]
 *             },
 *             {
 *                "long_name": "Cát Linh",
 *                "short_name": "Cát Linh",
 *                "types": [
 *                   "route"
 *                ]
 *             },
 *             {
 *                "long_name": "Đống Đa",
 *                "short_name": "Đống Đa",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hà Nội",
 *                "short_name": "Hà Nội",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "1 Cát Linh, Quốc Tử Giám, Đống Đa, Hà Nội, Vietnam",
 *          "geometry": {
 *             "location": {
 *                "lat": 21.0277082,
 *                "lng": 105.8339552
 *             },
 *             "location_type": "ROOFTOP",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.0290571802915,
 *                   "lng": 105.8353041802915
 *                },
 *                "southwest": {
 *                   "lat": 21.0263592197085,
 *                   "lng": 105.8326062197085
 *                }
 *             }
 *          },
 *          "place_id": "ChIJF8D7uJ6rNTERmgl8MqsfPN4",
 *          "plus_code": {
 *             "compound_code": "2RHM+3H Quốc Tử Giám, Đống Đa, Hanoi, Vietnam",
 *             "global_code": "7PH72RHM+3H"
 *          },
 *          "types": [
 *             "establishment",
 *             "point_of_interest"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "1",
 *                "short_name": "1",
 *                "types": [
 *                   "street_number"
 *                ]
 *             },
 *             {
 *                "long_name": "Cát Linh",
 *                "short_name": "Cát Linh",
 *                "types": [
 *                   "route"
 *                ]
 *             },
 *             {
 *                "long_name": "Đống Đa",
 *                "short_name": "Đống Đa",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hà Nội",
 *                "short_name": "Hà Nội",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "1 Cát Linh, Đống Đa, Hà Nội, Vietnam",
 *          "geometry": {
 *             "bounds": {
 *                "northeast": {
 *                   "lat": 21.0276847,
 *                   "lng": 105.8339976
 *                },
 *                "southwest": {
 *                   "lat": 21.0275278,
 *                   "lng": 105.8338806
 *                }
 *             },
 *             "location": {
 *                "lat": 21.0276156,
 *                "lng": 105.8339147
 *             },
 *             "location_type": "ROOFTOP",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.0289552302915,
 *                   "lng": 105.8352880802915
 *                },
 *                "southwest": {
 *                   "lat": 21.0262572697085,
 *                   "lng": 105.8325901197085
 *                }
 *             }
 *          },
 *          "place_id": "ChIJH6MWuZ6rNTEReRb5YnNU1FY",
 *          "types": [
 *             "premise"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "3",
 *                "short_name": "3",
 *                "types": [
 *                   "street_number"
 *                ]
 *             },
 *             {
 *                "long_name": "Cát Linh",
 *                "short_name": "Cát Linh",
 *                "types": [
 *                   "route"
 *                ]
 *             },
 *             {
 *                "long_name": "Đống Đa",
 *                "short_name": "Đống Đa",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hà Nội",
 *                "short_name": "Hà Nội",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "3 Cát Linh, Quốc Tử Giám, Đống Đa, Hà Nội, Vietnam",
 *          "geometry": {
 *             "location": {
 *                "lat": 21.0278605,
 *                "lng": 105.8339537
 *             },
 *             "location_type": "RANGE_INTERPOLATED",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.0292094802915,
 *                   "lng": 105.8353026802915
 *                },
 *                "southwest": {
 *                   "lat": 21.0265115197085,
 *                   "lng": 105.8326047197085
 *                }
 *             }
 *          },
 *          "place_id": "Ej8zIEPDoXQgTGluaCwgUXXhu5FjIFThu60gR2nDoW0sIMSQ4buRbmcgxJBhLCBIw6AgTuG7mWksIFZpZXRuYW0iGhIYChQKEgnxY6C4nqs1MRFza5IMZJ2NmRAD",
 *          "types": [
 *             "street_address"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "5-1",
 *                "short_name": "5-1",
 *                "types": [
 *                   "street_number"
 *                ]
 *             },
 *             {
 *                "long_name": "Cát Linh",
 *                "short_name": "Cát Linh",
 *                "types": [
 *                   "route"
 *                ]
 *             },
 *             {
 *                "long_name": "Đống Đa",
 *                "short_name": "Đống Đa",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hà Nội",
 *                "short_name": "Hà Nội",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "5-1 Cát Linh, Quốc Tử Giám, Đống Đa, Hà Nội, Vietnam",
 *          "geometry": {
 *             "bounds": {
 *                "northeast": {
 *                   "lat": 21.0279157,
 *                   "lng": 105.8340855
 *                },
 *                "southwest": {
 *                   "lat": 21.0277129,
 *                   "lng": 105.8336475
 *                }
 *             },
 *             "location": {
 *                "lat": 21.0278143,
 *                "lng": 105.8338665
 *             },
 *             "location_type": "GEOMETRIC_CENTER",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.0291632802915,
 *                   "lng": 105.8352154802915
 *                },
 *                "southwest": {
 *                   "lat": 21.0264653197085,
 *                   "lng": 105.8325175197085
 *                }
 *             }
 *          },
 *          "place_id": "ChIJ8WOguJ6rNTEREs5OYukjOZc",
 *          "types": [
 *             "route"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "Quốc Tử Giám",
 *                "short_name": "Quốc Tử Giám",
 *                "types": [
 *                   "administrative_area_level_3",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Đống Đa",
 *                "short_name": "Đống Đa",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hà Nội",
 *                "short_name": "Hà Nội",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "Quốc Tử Giám, Đống Đa, Hà Nội, Vietnam",
 *          "geometry": {
 *             "bounds": {
 *                "northeast": {
 *                   "lat": 21.0287449,
 *                   "lng": 105.835925
 *                },
 *                "southwest": {
 *                   "lat": 21.024691,
 *                   "lng": 105.8291429
 *                }
 *             },
 *             "location": {
 *                "lat": 21.0272556,
 *                "lng": 105.8326992
 *             },
 *             "location_type": "APPROXIMATE",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.0287449,
 *                   "lng": 105.835925
 *                },
 *                "southwest": {
 *                   "lat": 21.024691,
 *                   "lng": 105.8291429
 *                }
 *             }
 *          },
 *          "place_id": "ChIJN2H-gp6rNTERKRLsT64k0Sc",
 *          "types": [
 *             "administrative_area_level_3",
 *             "political"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "Quốc Tử Giám",
 *                "short_name": "Quốc Tử Giám",
 *                "types": [
 *                   "political",
 *                   "sublocality",
 *                   "sublocality_level_1"
 *                ]
 *             },
 *             {
 *                "long_name": "Đống Đa",
 *                "short_name": "Đống Đa",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hanoi",
 *                "short_name": "Hanoi",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "Quốc Tử Giám, Đống Đa, Hanoi, Vietnam",
 *          "geometry": {
 *             "bounds": {
 *                "northeast": {
 *                   "lat": 21.0286709,
 *                   "lng": 105.8362756
 *                },
 *                "southwest": {
 *                   "lat": 21.0240479,
 *                   "lng": 105.828748
 *                }
 *             },
 *             "location": {
 *                "lat": 21.0272556,
 *                "lng": 105.8326992
 *             },
 *             "location_type": "APPROXIMATE",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.0286709,
 *                   "lng": 105.8362756
 *                },
 *                "southwest": {
 *                   "lat": 21.0240479,
 *                   "lng": 105.828748
 *                }
 *             }
 *          },
 *          "place_id": "ChIJHZu_KZyrNTERSFApxoE0x00",
 *          "types": [
 *             "political",
 *             "sublocality",
 *             "sublocality_level_1"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "Đống Đa",
 *                "short_name": "Đống Đa",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hanoi",
 *                "short_name": "Hanoi",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "Đống Đa, Hanoi, Vietnam",
 *          "geometry": {
 *             "bounds": {
 *                "northeast": {
 *                   "lat": 21.032142,
 *                   "lng": 105.842786
 *                },
 *                "southwest": {
 *                   "lat": 20.997273,
 *                   "lng": 105.79826
 *                }
 *             },
 *             "location": {
 *                "lat": 21.0180725,
 *                "lng": 105.8299495
 *             },
 *             "location_type": "APPROXIMATE",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.032142,
 *                   "lng": 105.842786
 *                },
 *                "southwest": {
 *                   "lat": 20.997273,
 *                   "lng": 105.79826
 *                }
 *             }
 *          },
 *          "place_id": "ChIJ6-mLF4KrNTERdb1Jrv4EkUI",
 *          "types": [
 *             "administrative_area_level_2",
 *             "political"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "Hanoi",
 *                "short_name": "Hanoi",
 *                "types": [
 *                   "locality",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hoàn Kiếm",
 *                "short_name": "Hoàn Kiếm",
 *                "types": [
 *                   "administrative_area_level_2",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Hanoi",
 *                "short_name": "Hanoi",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "Hanoi, Hoàn Kiếm, Hanoi, Vietnam",
 *          "geometry": {
 *             "bounds": {
 *                "northeast": {
 *                   "lat": 21.0503801,
 *                   "lng": 105.8764459
 *                },
 *                "southwest": {
 *                   "lat": 20.9950991,
 *                   "lng": 105.7974815
 *                }
 *             },
 *             "location": {
 *                "lat": 21.0277644,
 *                "lng": 105.8341598
 *             },
 *             "location_type": "APPROXIMATE",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.0503801,
 *                   "lng": 105.8764459
 *                },
 *                "southwest": {
 *                   "lat": 20.9950991,
 *                   "lng": 105.7974815
 *                }
 *             }
 *          },
 *          "place_id": "ChIJoRyG2ZurNTERqRfKcnt_iOc",
 *          "types": [
 *             "locality",
 *             "political"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "Hanoi",
 *                "short_name": "Hanoi",
 *                "types": [
 *                   "administrative_area_level_1",
 *                   "political"
 *                ]
 *             },
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "Hanoi, Vietnam",
 *          "geometry": {
 *             "bounds": {
 *                "northeast": {
 *                   "lat": 21.3850271,
 *                   "lng": 106.0198859
 *                },
 *                "southwest": {
 *                   "lat": 20.5623231,
 *                   "lng": 105.2854659
 *                }
 *             },
 *             "location": {
 *                "lat": 21.0031177,
 *                "lng": 105.8201408
 *             },
 *             "location_type": "APPROXIMATE",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 21.3850271,
 *                   "lng": 106.0198859
 *                },
 *                "southwest": {
 *                   "lat": 20.5623231,
 *                   "lng": 105.2854659
 *                }
 *             }
 *          },
 *          "place_id": "ChIJKQqAE44ANTERDbkQYkF-mAI",
 *          "types": [
 *             "administrative_area_level_1",
 *             "political"
 *          ]
 *       },
 *       {
 *          "address_components": [
 *             {
 *                "long_name": "Vietnam",
 *                "short_name": "VN",
 *                "types": [
 *                   "country",
 *                   "political"
 *                ]
 *             }
 *          ],
 *          "formatted_address": "Vietnam",
 *          "geometry": {
 *             "bounds": {
 *                "northeast": {
 *                   "lat": 23.3926504,
 *                   "lng": 109.6765
 *                },
 *                "southwest": {
 *                   "lat": 8.195200099999999,
 *                   "lng": 102.1439156
 *                }
 *             },
 *             "location": {
 *                "lat": 14.058324,
 *                "lng": 108.277199
 *             },
 *             "location_type": "APPROXIMATE",
 *             "viewport": {
 *                "northeast": {
 *                   "lat": 23.3926504,
 *                   "lng": 109.6765
 *                },
 *                "southwest": {
 *                   "lat": 8.195200099999999,
 *                   "lng": 102.1439156
 *                }
 *             }
 *          },
 *          "place_id": "ChIJXx5qc016FTERvmL-4smwO7A",
 *          "types": [
 *             "country",
 *             "political"
 *          ]
 *       }
 *    ],
 *    "status": "OK"
 * }
 */
public class GoogleMapPlaceDto {
    @SerializedName("plus_code")
    public Place.PlusCode plusCode;

    @SerializedName("status")
    public String status;

    @SerializedName("results")
    public List<Place> results;

    public class Place {
        @SerializedName("address_components")
        public List<AddressComponent> addressComponents;

        @SerializedName("formatted_address")
        public String formattedAddress;

        @SerializedName("geometry")
        public Geometry geometry;

        @SerializedName("place_id")
        public String placeId;

        @SerializedName("plus_code")
        public PlusCode plusCode;

        @SerializedName("types")
        public List<String> types;

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

            @SerializedName("location_type")
            public String locationType;

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
