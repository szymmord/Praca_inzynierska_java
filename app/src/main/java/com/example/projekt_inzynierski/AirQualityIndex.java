package com.example.projekt_inzynierski;

import com.google.gson.annotations.SerializedName;

public class AirQualityIndex {
    @SerializedName("code")
    private String code;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("aqi")
    private int aqi;

    @SerializedName("aqiDisplay")
    private String aqiDisplay;

    @SerializedName("color")
    private Color color;

    @SerializedName("category")
    private String category;

    @SerializedName("dominantPollutant")
    private String dominantPollutant;

    // Dodaj konstruktory, gettery, settery
}
