package com.example.geopoli;

import java.time.LocalDateTime;

public class LocationUser {
    private String id;
    private String latitude;
    private String longitud;
    private String date;


    public LocationUser( ) {
    }

    public LocationUser(String id, String latitude, String longitud, String date) {
        this.id = id;
        this.latitude = latitude;
        this.longitud = longitud;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
