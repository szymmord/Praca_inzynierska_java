package com.example.projekt_inzynierski;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingService {
    @GET("maps/api/geocode/json")
    Call<GeocodingResponse> getCoordinates(@Query("address") String address, @Query("key") String apiKey);
}
