package com.poli2024.geopoli;

public class LocationData {
    private String latitude;
    private String longitude;

    public LocationData() {
        // Constructor vacío requerido por Firebase
    }

    public LocationData(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
