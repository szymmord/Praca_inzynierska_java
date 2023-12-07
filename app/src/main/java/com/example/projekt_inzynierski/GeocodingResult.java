package com.example.projekt_inzynierski;

import com.google.gson.annotations.SerializedName;

public class GeocodingResult {
    @SerializedName("geometry")
    private Geometry geometry;

    public Geometry getGeometry() {
        return geometry;
    }
}

class Geometry {
    @SerializedName("location")
    private Location location;

    public Location getLocation() {
        return location;
    }
}

class Location {
    @SerializedName("lat")
    private double latitude;

    @SerializedName("lng")
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
