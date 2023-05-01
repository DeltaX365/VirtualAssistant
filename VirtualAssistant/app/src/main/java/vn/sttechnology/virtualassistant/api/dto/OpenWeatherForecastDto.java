package vn.sttechnology.virtualassistant.api.dto;

import java.util.List;

/**
 * {
 *     "cod": "200",
 *     "message": 0,
 *     "cnt": 40,
 *     "list": [
 *         {
 *             "dt": 1586098800,
 *             "main": {
 *                 "temp": 17.53,
 *                 "feels_like": 18.27,
 *                 "temp_min": 17.53,
 *                 "temp_max": 18.41,
 *                 "pressure": 1019,
 *                 "sea_level": 1019,
 *                 "grnd_level": 1018,
 *                 "humidity": 84,
 *                 "temp_kf": -0.88
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 1.15,
 *                 "deg": 29
 *             },
 *             "rain": {
 *                 "3h": 0.6
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-05 15:00:00"
 *         },
 *         {
 *             "dt": 1586109600,
 *             "main": {
 *                 "temp": 16.76,
 *                 "feels_like": 16.93,
 *                 "temp_min": 16.76,
 *                 "temp_max": 17.42,
 *                 "pressure": 1017,
 *                 "sea_level": 1017,
 *                 "grnd_level": 1015,
 *                 "humidity": 89,
 *                 "temp_kf": -0.66
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 2.03,
 *                 "deg": 32
 *             },
 *             "rain": {
 *                 "3h": 1.21
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-05 18:00:00"
 *         },
 *         {
 *             "dt": 1586120400,
 *             "main": {
 *                 "temp": 16.87,
 *                 "feels_like": 17.26,
 *                 "temp_min": 16.87,
 *                 "temp_max": 17.31,
 *                 "pressure": 1016,
 *                 "sea_level": 1016,
 *                 "grnd_level": 1014,
 *                 "humidity": 89,
 *                 "temp_kf": -0.44
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 1.78,
 *                 "deg": 25
 *             },
 *             "rain": {
 *                 "3h": 0.14
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-05 21:00:00"
 *         },
 *         {
 *             "dt": 1586131200,
 *             "main": {
 *                 "temp": 17.23,
 *                 "feels_like": 17.76,
 *                 "temp_min": 17.23,
 *                 "temp_max": 17.45,
 *                 "pressure": 1017,
 *                 "sea_level": 1017,
 *                 "grnd_level": 1015,
 *                 "humidity": 88,
 *                 "temp_kf": -0.22
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 1.67,
 *                 "deg": 9
 *             },
 *             "rain": {
 *                 "3h": 0.18
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-06 00:00:00"
 *         },
 *         {
 *             "dt": 1586142000,
 *             "main": {
 *                 "temp": 20.27,
 *                 "feels_like": 21.54,
 *                 "temp_min": 20.27,
 *                 "temp_max": 20.27,
 *                 "pressure": 1017,
 *                 "sea_level": 1017,
 *                 "grnd_level": 1016,
 *                 "humidity": 79,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 1.3,
 *                 "deg": 28
 *             },
 *             "rain": {
 *                 "3h": 0.69
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-06 03:00:00"
 *         },
 *         {
 *             "dt": 1586152800,
 *             "main": {
 *                 "temp": 22.64,
 *                 "feels_like": 24.19,
 *                 "temp_min": 22.64,
 *                 "temp_max": 22.64,
 *                 "pressure": 1015,
 *                 "sea_level": 1015,
 *                 "grnd_level": 1013,
 *                 "humidity": 75,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 99
 *             },
 *             "wind": {
 *                 "speed": 1.77,
 *                 "deg": 27
 *             },
 *             "rain": {
 *                 "3h": 0.86
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-06 06:00:00"
 *         },
 *         {
 *             "dt": 1586163600,
 *             "main": {
 *                 "temp": 23.88,
 *                 "feels_like": 25.78,
 *                 "temp_min": 23.88,
 *                 "temp_max": 23.88,
 *                 "pressure": 1012,
 *                 "sea_level": 1012,
 *                 "grnd_level": 1010,
 *                 "humidity": 74,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 93
 *             },
 *             "wind": {
 *                 "speed": 1.88,
 *                 "deg": 91
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-06 09:00:00"
 *         },
 *         {
 *             "dt": 1586174400,
 *             "main": {
 *                 "temp": 22.24,
 *                 "feels_like": 23.94,
 *                 "temp_min": 22.24,
 *                 "temp_max": 22.24,
 *                 "pressure": 1013,
 *                 "sea_level": 1013,
 *                 "grnd_level": 1011,
 *                 "humidity": 85,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 92
 *             },
 *             "wind": {
 *                 "speed": 2.58,
 *                 "deg": 115
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-06 12:00:00"
 *         },
 *         {
 *             "dt": 1586185200,
 *             "main": {
 *                 "temp": 20.76,
 *                 "feels_like": 21.5,
 *                 "temp_min": 20.76,
 *                 "temp_max": 20.76,
 *                 "pressure": 1015,
 *                 "sea_level": 1015,
 *                 "grnd_level": 1013,
 *                 "humidity": 89,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 96
 *             },
 *             "wind": {
 *                 "speed": 3.48,
 *                 "deg": 127
 *             },
 *             "rain": {
 *                 "3h": 0.14
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-06 15:00:00"
 *         },
 *         {
 *             "dt": 1586196000,
 *             "main": {
 *                 "temp": 20.38,
 *                 "feels_like": 22.3,
 *                 "temp_min": 20.38,
 *                 "temp_max": 20.38,
 *                 "pressure": 1014,
 *                 "sea_level": 1014,
 *                 "grnd_level": 1012,
 *                 "humidity": 91,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 97
 *             },
 *             "wind": {
 *                 "speed": 1.78,
 *                 "deg": 136
 *             },
 *             "rain": {
 *                 "3h": 0.39
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-06 18:00:00"
 *         },
 *         {
 *             "dt": 1586206800,
 *             "main": {
 *                 "temp": 20.01,
 *                 "feels_like": 22.9,
 *                 "temp_min": 20.01,
 *                 "temp_max": 20.01,
 *                 "pressure": 1013,
 *                 "sea_level": 1013,
 *                 "grnd_level": 1011,
 *                 "humidity": 94,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 0.5,
 *                 "deg": 82
 *             },
 *             "rain": {
 *                 "3h": 1.04
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-06 21:00:00"
 *         },
 *         {
 *             "dt": 1586217600,
 *             "main": {
 *                 "temp": 19.93,
 *                 "feels_like": 21.95,
 *                 "temp_min": 19.93,
 *                 "temp_max": 19.93,
 *                 "pressure": 1015,
 *                 "sea_level": 1015,
 *                 "grnd_level": 1013,
 *                 "humidity": 92,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 1.47,
 *                 "deg": 57
 *             },
 *             "rain": {
 *                 "3h": 0.76
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-07 00:00:00"
 *         },
 *         {
 *             "dt": 1586228400,
 *             "main": {
 *                 "temp": 21.64,
 *                 "feels_like": 23.54,
 *                 "temp_min": 21.64,
 *                 "temp_max": 21.64,
 *                 "pressure": 1015,
 *                 "sea_level": 1015,
 *                 "grnd_level": 1013,
 *                 "humidity": 84,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 99
 *             },
 *             "wind": {
 *                 "speed": 1.79,
 *                 "deg": 45
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-07 03:00:00"
 *         },
 *         {
 *             "dt": 1586239200,
 *             "main": {
 *                 "temp": 24.21,
 *                 "feels_like": 26.21,
 *                 "temp_min": 24.21,
 *                 "temp_max": 24.21,
 *                 "pressure": 1013,
 *                 "sea_level": 1013,
 *                 "grnd_level": 1011,
 *                 "humidity": 76,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 96
 *             },
 *             "wind": {
 *                 "speed": 2.23,
 *                 "deg": 107
 *             },
 *             "rain": {
 *                 "3h": 0.16
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-07 06:00:00"
 *         },
 *         {
 *             "dt": 1586250000,
 *             "main": {
 *                 "temp": 23.46,
 *                 "feels_like": 25.34,
 *                 "temp_min": 23.46,
 *                 "temp_max": 23.46,
 *                 "pressure": 1011,
 *                 "sea_level": 1011,
 *                 "grnd_level": 1009,
 *                 "humidity": 83,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 2.87,
 *                 "deg": 118
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-07 09:00:00"
 *         },
 *         {
 *             "dt": 1586260800,
 *             "main": {
 *                 "temp": 22.19,
 *                 "feels_like": 23.92,
 *                 "temp_min": 22.19,
 *                 "temp_max": 22.19,
 *                 "pressure": 1012,
 *                 "sea_level": 1012,
 *                 "grnd_level": 1011,
 *                 "humidity": 87,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 97
 *             },
 *             "wind": {
 *                 "speed": 2.75,
 *                 "deg": 135
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-07 12:00:00"
 *         },
 *         {
 *             "dt": 1586271600,
 *             "main": {
 *                 "temp": 21.31,
 *                 "feels_like": 22.4,
 *                 "temp_min": 21.31,
 *                 "temp_max": 21.31,
 *                 "pressure": 1014,
 *                 "sea_level": 1014,
 *                 "grnd_level": 1013,
 *                 "humidity": 88,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 99
 *             },
 *             "wind": {
 *                 "speed": 3.22,
 *                 "deg": 127
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-07 15:00:00"
 *         },
 *         {
 *             "dt": 1586282400,
 *             "main": {
 *                 "temp": 20.63,
 *                 "feels_like": 22.2,
 *                 "temp_min": 20.63,
 *                 "temp_max": 20.63,
 *                 "pressure": 1013,
 *                 "sea_level": 1013,
 *                 "grnd_level": 1012,
 *                 "humidity": 91,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 99
 *             },
 *             "wind": {
 *                 "speed": 2.44,
 *                 "deg": 126
 *             },
 *             "rain": {
 *                 "3h": 0.57
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-07 18:00:00"
 *         },
 *         {
 *             "dt": 1586293200,
 *             "main": {
 *                 "temp": 20.4,
 *                 "feels_like": 23.06,
 *                 "temp_min": 20.4,
 *                 "temp_max": 20.4,
 *                 "pressure": 1012,
 *                 "sea_level": 1012,
 *                 "grnd_level": 1010,
 *                 "humidity": 93,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 0.96,
 *                 "deg": 72
 *             },
 *             "rain": {
 *                 "3h": 0.53
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-07 21:00:00"
 *         },
 *         {
 *             "dt": 1586304000,
 *             "main": {
 *                 "temp": 20.4,
 *                 "feels_like": 22.78,
 *                 "temp_min": 20.4,
 *                 "temp_max": 20.4,
 *                 "pressure": 1014,
 *                 "sea_level": 1014,
 *                 "grnd_level": 1013,
 *                 "humidity": 92,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 1.25,
 *                 "deg": 55
 *             },
 *             "rain": {
 *                 "3h": 0.6
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-08 00:00:00"
 *         },
 *         {
 *             "dt": 1586314800,
 *             "main": {
 *                 "temp": 22.02,
 *                 "feels_like": 25.24,
 *                 "temp_min": 22.02,
 *                 "temp_max": 22.02,
 *                 "pressure": 1015,
 *                 "sea_level": 1015,
 *                 "grnd_level": 1013,
 *                 "humidity": 86,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 99
 *             },
 *             "wind": {
 *                 "speed": 0.39,
 *                 "deg": 66
 *             },
 *             "rain": {
 *                 "3h": 0.16
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-08 03:00:00"
 *         },
 *         {
 *             "dt": 1586325600,
 *             "main": {
 *                 "temp": 25.3,
 *                 "feels_like": 28.4,
 *                 "temp_min": 25.3,
 *                 "temp_max": 25.3,
 *                 "pressure": 1012,
 *                 "sea_level": 1012,
 *                 "grnd_level": 1011,
 *                 "humidity": 77,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 96
 *             },
 *             "wind": {
 *                 "speed": 1.53,
 *                 "deg": 104
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-08 06:00:00"
 *         },
 *         {
 *             "dt": 1586336400,
 *             "main": {
 *                 "temp": 27.18,
 *                 "feels_like": 29.66,
 *                 "temp_min": 27.18,
 *                 "temp_max": 27.18,
 *                 "pressure": 1010,
 *                 "sea_level": 1010,
 *                 "grnd_level": 1009,
 *                 "humidity": 74,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 72
 *             },
 *             "wind": {
 *                 "speed": 3.28,
 *                 "deg": 127
 *             },
 *             "rain": {
 *                 "3h": 0.36
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-08 09:00:00"
 *         },
 *         {
 *             "dt": 1586347200,
 *             "main": {
 *                 "temp": 23.58,
 *                 "feels_like": 25.45,
 *                 "temp_min": 23.58,
 *                 "temp_max": 23.58,
 *                 "pressure": 1012,
 *                 "sea_level": 1012,
 *                 "grnd_level": 1011,
 *                 "humidity": 90,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 74
 *             },
 *             "wind": {
 *                 "speed": 3.93,
 *                 "deg": 128
 *             },
 *             "rain": {
 *                 "3h": 0.49
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-08 12:00:00"
 *         },
 *         {
 *             "dt": 1586358000,
 *             "main": {
 *                 "temp": 22.23,
 *                 "feels_like": 24.01,
 *                 "temp_min": 22.23,
 *                 "temp_max": 22.23,
 *                 "pressure": 1014,
 *                 "sea_level": 1014,
 *                 "grnd_level": 1012,
 *                 "humidity": 93,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 803,
 *                     "main": "Clouds",
 *                     "description": "broken clouds",
 *                     "icon": "04n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 61
 *             },
 *             "wind": {
 *                 "speed": 3.47,
 *                 "deg": 131
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-08 15:00:00"
 *         },
 *         {
 *             "dt": 1586368800,
 *             "main": {
 *                 "temp": 22.41,
 *                 "feels_like": 24.78,
 *                 "temp_min": 22.41,
 *                 "temp_max": 22.41,
 *                 "pressure": 1013,
 *                 "sea_level": 1013,
 *                 "grnd_level": 1011,
 *                 "humidity": 93,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 80
 *             },
 *             "wind": {
 *                 "speed": 2.75,
 *                 "deg": 116
 *             },
 *             "rain": {
 *                 "3h": 0.27
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-08 18:00:00"
 *         },
 *         {
 *             "dt": 1586379600,
 *             "main": {
 *                 "temp": 22.25,
 *                 "feels_like": 25.05,
 *                 "temp_min": 22.25,
 *                 "temp_max": 22.25,
 *                 "pressure": 1012,
 *                 "sea_level": 1012,
 *                 "grnd_level": 1011,
 *                 "humidity": 94,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 2.15,
 *                 "deg": 94
 *             },
 *             "rain": {
 *                 "3h": 0.6
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-08 21:00:00"
 *         },
 *         {
 *             "dt": 1586390400,
 *             "main": {
 *                 "temp": 22.7,
 *                 "feels_like": 25.56,
 *                 "temp_min": 22.7,
 *                 "temp_max": 22.7,
 *                 "pressure": 1014,
 *                 "sea_level": 1014,
 *                 "grnd_level": 1012,
 *                 "humidity": 93,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 2.26,
 *                 "deg": 111
 *             },
 *             "rain": {
 *                 "3h": 0.56
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-09 00:00:00"
 *         },
 *         {
 *             "dt": 1586401200,
 *             "main": {
 *                 "temp": 25.47,
 *                 "feels_like": 28.3,
 *                 "temp_min": 25.47,
 *                 "temp_max": 25.47,
 *                 "pressure": 1015,
 *                 "sea_level": 1015,
 *                 "grnd_level": 1013,
 *                 "humidity": 84,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 97
 *             },
 *             "wind": {
 *                 "speed": 3.1,
 *                 "deg": 140
 *             },
 *             "rain": {
 *                 "3h": 0.75
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-09 03:00:00"
 *         },
 *         {
 *             "dt": 1586412000,
 *             "main": {
 *                 "temp": 28.01,
 *                 "feels_like": 29.6,
 *                 "temp_min": 28.01,
 *                 "temp_max": 28.01,
 *                 "pressure": 1012,
 *                 "sea_level": 1012,
 *                 "grnd_level": 1010,
 *                 "humidity": 73,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 501,
 *                     "main": "Rain",
 *                     "description": "moderate rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 85
 *             },
 *             "wind": {
 *                 "speed": 4.99,
 *                 "deg": 149
 *             },
 *             "rain": {
 *                 "3h": 3.9
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-09 06:00:00"
 *         },
 *         {
 *             "dt": 1586422800,
 *             "main": {
 *                 "temp": 27.79,
 *                 "feels_like": 28.5,
 *                 "temp_min": 27.79,
 *                 "temp_max": 27.79,
 *                 "pressure": 1010,
 *                 "sea_level": 1010,
 *                 "grnd_level": 1009,
 *                 "humidity": 75,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 6.43,
 *                 "deg": 138
 *             },
 *             "rain": {
 *                 "3h": 2.26
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-09 09:00:00"
 *         },
 *         {
 *             "dt": 1586433600,
 *             "main": {
 *                 "temp": 24.21,
 *                 "feels_like": 25.6,
 *                 "temp_min": 24.21,
 *                 "temp_max": 24.21,
 *                 "pressure": 1013,
 *                 "sea_level": 1013,
 *                 "grnd_level": 1011,
 *                 "humidity": 87,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 4.65,
 *                 "deg": 129
 *             },
 *             "rain": {
 *                 "3h": 0.5
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-09 12:00:00"
 *         },
 *         {
 *             "dt": 1586444400,
 *             "main": {
 *                 "temp": 23.36,
 *                 "feels_like": 26.39,
 *                 "temp_min": 23.36,
 *                 "temp_max": 23.36,
 *                 "pressure": 1015,
 *                 "sea_level": 1015,
 *                 "grnd_level": 1013,
 *                 "humidity": 90,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 2.11,
 *                 "deg": 139
 *             },
 *             "rain": {
 *                 "3h": 0.62
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-09 15:00:00"
 *         },
 *         {
 *             "dt": 1586455200,
 *             "main": {
 *                 "temp": 23.13,
 *                 "feels_like": 24.66,
 *                 "temp_min": 23.13,
 *                 "temp_max": 23.13,
 *                 "pressure": 1014,
 *                 "sea_level": 1014,
 *                 "grnd_level": 1012,
 *                 "humidity": 92,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 4.34,
 *                 "deg": 137
 *             },
 *             "rain": {
 *                 "3h": 0.54
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-09 18:00:00"
 *         },
 *         {
 *             "dt": 1586466000,
 *             "main": {
 *                 "temp": 23.36,
 *                 "feels_like": 26.35,
 *                 "temp_min": 23.36,
 *                 "temp_max": 23.36,
 *                 "pressure": 1012,
 *                 "sea_level": 1012,
 *                 "grnd_level": 1011,
 *                 "humidity": 93,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 2.57,
 *                 "deg": 155
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-09 21:00:00"
 *         },
 *         {
 *             "dt": 1586476800,
 *             "main": {
 *                 "temp": 24.2,
 *                 "feels_like": 26.64,
 *                 "temp_min": 24.2,
 *                 "temp_max": 24.2,
 *                 "pressure": 1014,
 *                 "sea_level": 1014,
 *                 "grnd_level": 1012,
 *                 "humidity": 90,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 3.57,
 *                 "deg": 155
 *             },
 *             "rain": {
 *                 "3h": 0.31
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-10 00:00:00"
 *         },
 *         {
 *             "dt": 1586487600,
 *             "main": {
 *                 "temp": 26.78,
 *                 "feels_like": 28.24,
 *                 "temp_min": 26.78,
 *                 "temp_max": 26.78,
 *                 "pressure": 1015,
 *                 "sea_level": 1015,
 *                 "grnd_level": 1013,
 *                 "humidity": 77,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 4.94,
 *                 "deg": 167
 *             },
 *             "rain": {
 *                 "3h": 0.69
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-10 03:00:00"
 *         },
 *         {
 *             "dt": 1586498400,
 *             "main": {
 *                 "temp": 29.97,
 *                 "feels_like": 30.07,
 *                 "temp_min": 29.97,
 *                 "temp_max": 29.97,
 *                 "pressure": 1011,
 *                 "sea_level": 1011,
 *                 "grnd_level": 1010,
 *                 "humidity": 64,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 6.88,
 *                 "deg": 167
 *             },
 *             "rain": {
 *                 "3h": 0.27
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-10 06:00:00"
 *         },
 *         {
 *             "dt": 1586509200,
 *             "main": {
 *                 "temp": 29.19,
 *                 "feels_like": 28.89,
 *                 "temp_min": 29.19,
 *                 "temp_max": 29.19,
 *                 "pressure": 1010,
 *                 "sea_level": 1010,
 *                 "grnd_level": 1008,
 *                 "humidity": 67,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 500,
 *                     "main": "Rain",
 *                     "description": "light rain",
 *                     "icon": "10d"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 7.46,
 *                 "deg": 155
 *             },
 *             "rain": {
 *                 "3h": 0.3
 *             },
 *             "sys": {
 *                 "pod": "d"
 *             },
 *             "dt_txt": "2020-04-10 09:00:00"
 *         },
 *         {
 *             "dt": 1586520000,
 *             "main": {
 *                 "temp": 25.23,
 *                 "feels_like": 25.88,
 *                 "temp_min": 25.23,
 *                 "temp_max": 25.23,
 *                 "pressure": 1011,
 *                 "sea_level": 1011,
 *                 "grnd_level": 1009,
 *                 "humidity": 82,
 *                 "temp_kf": 0
 *             },
 *             "weather": [
 *                 {
 *                     "id": 804,
 *                     "main": "Clouds",
 *                     "description": "overcast clouds",
 *                     "icon": "04n"
 *                 }
 *             ],
 *             "clouds": {
 *                 "all": 100
 *             },
 *             "wind": {
 *                 "speed": 5.73,
 *                 "deg": 153
 *             },
 *             "sys": {
 *                 "pod": "n"
 *             },
 *             "dt_txt": "2020-04-10 12:00:00"
 *         }
 *     ],
 *     "city": {
 *         "id": 1581130,
 *         "name": "Hanoi",
 *         "coord": {
 *             "lat": 21.0245,
 *             "lon": 105.8412
 *         },
 *         "country": "VN",
 *         "population": 1431270,
 *         "timezone": 25200,
 *         "sunrise": 1586040363,
 *         "sunset": 1586085130
 *     }
 * }
 */
public class OpenWeatherForecastDto {
    public String cod;
    public int message;
    public int cnt;
    public List<OpenWeatherForecast> list;
    public City city;

    public class OpenWeatherForecast {
        public int dt;
        public Main main;
        public List<Weather> weather;
        public Clouds clouds;
        public Wind wind;
        public Rain rain;
        public Sys sys;
        public String dtTxt;

        public class Main {
            public double temp;
            public double feelsLike;
            public double tempMin;
            public double tempMax;
            public int pressure;
            public int seaLevel;
            public int grndLevel;
            public int humidity;
            public double tempKf;
        }

        public class Weather {
            public int id;
            public String main;
            public String description;
            public String icon;
        }

        public class Clouds {
            public int all;
        }

        public class Wind {
            public double speed;
            public int deg;
        }

        public class Rain {
            public double threeHour;
        }

        public class Sys {
            public String pod;
        }
    }

    public class City {
        public int id;
        public String name;
        public Coord coord;
        public String country;
        public int population;
        public int timezone;
        public int sunrise;
        public int sunset;

        public class Coord {
            public double lat;
            public double lon;
        }
    }
}
