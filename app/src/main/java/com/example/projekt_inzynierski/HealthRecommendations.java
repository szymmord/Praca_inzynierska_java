package com.example.projekt_inzynierski;

import com.google.gson.annotations.SerializedName;

public class HealthRecommendations {
    @SerializedName("generalPopulation")
    private String generalPopulation;

    @SerializedName("elderly")
    private String elderly;

    @SerializedName("lungDiseasePopulation")
    private String lungDiseasePopulation;

    @SerializedName("heartDiseasePopulation")
    private String heartDiseasePopulation;

    @SerializedName("athletes")
    private String athletes;

    @SerializedName("pregnantWomen")
    private String pregnantWomen;

    @SerializedName("children")
    private String children;

    // Dodaj konstruktory, gettery, settery
}