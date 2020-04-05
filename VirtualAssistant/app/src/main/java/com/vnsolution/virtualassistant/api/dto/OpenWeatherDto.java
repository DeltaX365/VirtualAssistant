package com.vnsolution.virtualassistant.api.dto;

import java.util.List;

/**
 * {
 *     "coord": {
 *         "lon": 105.83,
 *         "lat": 21.03
 *     },
 *     "weather": [
 *         {
 *             "id": 804,
 *             "main": "Clouds",
 *             "description": "overcast clouds",
 *             "icon": "04n"
 *         }
 *     ],
 *     "base": "stations",
 *     "main": {
 *         "temp": 18,
 *         "feels_like": 17.07,
 *         "temp_min": 18,
 *         "temp_max": 18,
 *         "pressure": 1018,
 *         "humidity": 72
 *     },
 *     "visibility": 10000,
 *     "wind": {
 *         "speed": 2.6,
 *         "deg": 10
 *     },
 *     "clouds": {
 *         "all": 90
 *     },
 *     "dt": 1586092071,
 *     "sys": {
 *         "type": 1,
 *         "id": 9308,
 *         "country": "VN",
 *         "sunrise": 1586040366,
 *         "sunset": 1586085133
 *     },
 *     "timezone": 25200,
 *     "id": 1581130,
 *     "name": "Hanoi",
 *     "cod": 200
 * }
 */
public class OpenWeatherDto {
    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Clouds clouds;
    public int dt;
    public Sys sys;
    public int timezone;
    public int id;
    public String name;
    public int cod;

    public class Coord {
        public double lon;
        public double lat;
    }

    public class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public class Main {
        public double temp;
        public double feelsLike;
        public double tempMin;
        public double tempMax;
        public int pressure;
        public int humidity;
    }

    public class Wind {
        public double speed;
        public int deg;
    }

    public class Clouds {
        public int all;
    }

    public class Sys {
        public int type;
        public int id;
        public double message;
        public String country;
        public int sunrise;
        public int sunset;
    }
}
