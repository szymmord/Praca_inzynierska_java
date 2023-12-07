package com.example.projekt_inzynierski;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AirQualityResponse {
    @SerializedName("dateTime")
    private String dateTime;

    @SerializedName("regionCode")
    private String regionCode;

    @SerializedName("indexes")
    private List<AirQualityIndex> indexes;

    @SerializedName("pollutants")
    private List<Pollutant> pollutants;

    @SerializedName("healthRecommendations")
    private HealthRecommendations healthRecommendations;

    // Dodaj konstruktory, gettery, settery

    // Przykładowy konstruktor:
    public AirQualityResponse(String dateTime, String regionCode, List<AirQualityIndex> indexes,
                              List<Pollutant> pollutants, HealthRecommendations healthRecommendations) {
        this.dateTime = dateTime;
        this.regionCode = regionCode;
        this.indexes = indexes;
        this.pollutants = pollutants;
        this.healthRecommendations = healthRecommendations;
    }

    // Przykładowe gettery:
    public String getDateTime() {
        return dateTime;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public List<AirQualityIndex> getIndexes() {
        return indexes;
    }

    public List<Pollutant> getPollutants() {
        return pollutants;
    }

    public HealthRecommendations getHealthRecommendations() {
        return healthRecommendations;
    }
}
