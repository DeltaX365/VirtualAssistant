package com.vnsolution.virtual_assistant.api;

import com.vnsolution.virtual_assistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtual_assistant.api.dto.GoogleMapPlaceDto;
import com.vnsolution.virtual_assistant.api.dto.GoogleMapPlaceDetailDto;
import com.vnsolution.virtual_assistant.api.dto.GoogleMapPlaceSearchDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApi {
    @GET("maps/api/geocode/json")
    Call<GoogleMapPlaceDto> getPlaceByLocation(@Query(value = "latlng", encoded = false) String latlng);

    @GET("maps/api/place/details/json")
    Call<GoogleMapPlaceDetailDto> getPlaceDetail(@Query("place_id") String placeId);

    @GET("maps/api/place/findplacefromtext/json")
    Call<GoogleMapPlaceSearchDto> searchPlaceByAddress(@Query("input") String search, @Query("inputtype") String type, @Query("fields") String fields);

    @GET("maps/api/place/nearbysearch/json")
    Call<GoogleMapNearByDto> searchNearByLocation(@Query(value = "location", encoded = false) String location, @Query("rankby") String rankby);
//    LiveData<ApiResponse<GoogleMapNearByDto>> searchNearByLocation(@Query(value = "location", encoded = false) String location, @Query("rankby") String rankby);
}
