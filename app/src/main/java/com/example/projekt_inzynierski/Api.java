package com.example.projekt_inzynierski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api extends AppCompatActivity  {

    private static final String BASE_URL_GEOCODING = "https://maps.googleapis.com/";
    private static final String BASE_URL_AIR_QUALITY = "https://airquality.googleapis.com/";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        Retrofit retrofitGeocoding = new Retrofit.Builder()
                .baseUrl(BASE_URL_GEOCODING)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GeocodingService geocodingService = retrofitGeocoding.create(GeocodingService.class);

        // Przykładowa nazwa miasta
        String cityName = "Warsaw";

        Call<GeocodingResponse> callGeocoding = geocodingService.getCoordinates(cityName, "YOUR_GEOCODING_API_KEY");
        callGeocoding.enqueue(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GeocodingResult> results = response.body().getResults();
                    if (!results.isEmpty()) {
                        Location location = results.get(0).getGeometry().getLocation();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Teraz możesz użyć tych współrzędnych do żądania Air Quality API
                        sendAirQualityRequest(latitude, longitude);

                        // Ustaw marker na mapie
                        setMapMarker(new LatLng(latitude, longitude));
                    }
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                // Obsłuż błąd geokodowania
            }
        });
    }

    private void sendAirQualityRequest(double latitude, double longitude) {
        Retrofit retrofitAirQuality = new Retrofit.Builder()
                .baseUrl(BASE_URL_AIR_QUALITY)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AirQualityService airQualityService = retrofitAirQuality.create(AirQualityService.class);

        LocationData locationData = new LocationData(latitude, longitude);

        Call<AirQualityResponse> callAirQuality = airQualityService.getCurrentConditions(locationData);
        callAirQuality.enqueue(new Callback<AirQualityResponse>() {
            @Override
            public void onResponse(Call<AirQualityResponse> call, Response<AirQualityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Obsłuż odpowiedź z Air Quality API
                    Log.d("AirQuality", "Received Air Quality Response: " + response);

                    // Wyświetl Toast Message
                    Toast.makeText(Api.this, "Air Quality Response Received", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AirQualityResponse> call, Throwable t) {
                // Obsłuż błąd z Air Quality API
                Toast.makeText(Api.this, "Nie działa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMapMarker(LatLng latLng) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker at Geocoded Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }


}
