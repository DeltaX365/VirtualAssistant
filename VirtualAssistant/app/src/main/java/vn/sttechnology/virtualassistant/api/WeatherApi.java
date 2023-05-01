package vn.sttechnology.virtualassistant.api;

import vn.sttechnology.virtualassistant.api.dto.OpenWeatherDto;
import vn.sttechnology.virtualassistant.api.dto.OpenWeatherForecastDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("data/2.5/weather")
    Call<OpenWeatherDto> getWeatherByLocation(@Query("lat") double latitude, @Query("lon") double longitude);

    @GET("data/2.5/forecast")
    Call<OpenWeatherForecastDto> getWeatherForecastByLocation(@Query("lat") double latitude, @Query("lon") double longitude);
}
