package com.poli2024.geopoli;

public class Mineros {
    private String name;
    private String lastName;
    private String email;
    private String id;
    private String latitude;
    private String longitude;

    public Mineros(){

    }

    public Mineros(String name, String lastName, String email, String id) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.latitude = null; // o puedes asignar valores predeterminados si es necesario
        this.longitude = null; // o puedes asignar valores predeterminados si es necesario
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
