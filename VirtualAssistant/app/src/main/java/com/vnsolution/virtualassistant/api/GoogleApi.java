package com.vnsolution.virtualassistant.api;

import android.location.Address;

import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDto;
import com.vnsolution.virtualassistant.api.dto.GoogleMapPlaceDetailDto;
import com.vnsolution.virtualassistant.model.StopPoint;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApi {
    @GET("maps/api/geocode/json")
    Call<GoogleMapPlaceDto> getPlaceByLocation(@Query(value = "latlng", encoded = false) String latlng);

    @GET("maps/api/place/details/json")
    Call<GoogleMapPlaceDetailDto> getPlaceDetail(@Query("place_id") String placeId);

    @GET("maps/api/place/nearbysearch/json")
    Call<GoogleMapNearByDto> searchNearByLocation(@Query(value = "location", encoded = false) String location, @Query("rankby") String rankby);
}
