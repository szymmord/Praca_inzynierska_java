package com.example.projekt_inzynierski;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class BreezometerApi {

    private static final String API_KEY = "AIzaSyCrgXEWZKfcmKfCOCLCCeGohtwscgUQbQI";
    private static final String BASE_URL = "https://api.breezometer.com/";

    public interface BreezometerCallback {
        void onSuccess(BreezometerData data);
        void onError(VolleyError error);
    }

    public static void getAirQualityData(Context context, double latitude, double longitude, BreezometerCallback callback) {
        String url = BASE_URL + "air-quality/v2/current-conditions";
        url += "?lat=" + latitude + "&lon=" + longitude + "&key=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    BreezometerData data = gson.fromJson(response.toString(), BreezometerData.class);
                    callback.onSuccess(data);
                },
                error -> callback.onError(error));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
    public class BreezometerData {

        private String country;
        private String city;
        private double aqi; // Indeks jakości powietrza
        private String dominantPollutant;
        private String datetime;

        // Dodaj pozostałe pola, które są dostarczane przez Breezometer API

        // Konstruktory, gettery, settery

        // Pamiętaj o dostosowaniu do struktury faktycznych danych z API

        // Przykładowe gettery poniżej:

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }

        public double getAqi() {
            return aqi;
        }

        public String getDominantPollutant() {
            return dominantPollutant;
        }

        public String getDatetime() {
            return datetime;
        }
    }
}
