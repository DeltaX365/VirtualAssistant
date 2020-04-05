package com.vnsolution.virtualassistant.api;

import com.vnsolution.virtualassistant.api.dto.OpenWeatherDto;
import com.vnsolution.virtualassistant.api.dto.OpenWeatherForecastDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather")
    Call<OpenWeatherDto> getWeatherByLocation(@Query("lat") double latitude, @Query("lon") double longitude);

    @GET("data/2.5/forecast")
    Call<OpenWeatherForecastDto> getWeatherForecastByLocation(@Query("lat") double latitude, @Query("lon") double longitude);
}
