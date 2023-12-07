package com.example.projekt_inzynierski;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface AirQualityService {

        @Headers("Content-Type: application/json")
        @POST("v1/currentConditions:lookup?key=AIzaSyCmUpVSnRFwnPIq16VNfKkyudamUVPALqU")
        Call<AirQualityResponse> getCurrentConditions(@Body LocationData locationData);
    }

