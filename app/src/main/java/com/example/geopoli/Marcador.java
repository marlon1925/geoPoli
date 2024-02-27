package com.example.geopoli;

public class Marcador {
    private double latitud;
    private double longitud;

    public Marcador() {
        // Constructor vacío requerido para Firebase
    }

    public Marcador(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Métodos getters y setters
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
