package org.example.model;

public class AddressSuggestion {

    private String displayName;
    private double lat;
    private double lon;

    public AddressSuggestion(String displayName, double lat, double lon) {
        this.displayName = displayName;
        this.lat = lat;
        this.lon = lon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
