package com.example.projekt_inzynierski;
import com.google.gson.annotations.SerializedName;

public class Pollutant {
    @SerializedName("code")
    private String code;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("concentration")
    private Concetration concentration;

    @SerializedName("additionalInfo")
    private AdditionalInfo additionalInfo;

    // Dodaj konstruktory, gettery, settery
}