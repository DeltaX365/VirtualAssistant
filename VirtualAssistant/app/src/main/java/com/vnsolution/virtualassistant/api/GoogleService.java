package com.vnsolution.virtualassistant.api;

import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDetailDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleService {
    @GET("maps/api/geocode/json")
    Call<GoogleMapPlaceDto> getPlaceByLocation(@Query(value = "latlng", encoded = false) String latlng);

    @GET("maps/api/place/details/json")
    Call<GoogleMapPlaceDetailDto> getPlaceDetail(@Query("place_id") String placeId);
}
