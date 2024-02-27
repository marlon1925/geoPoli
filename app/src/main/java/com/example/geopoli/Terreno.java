package com.example.geopoli;

import java.lang.reflect.Array;
import java.util.List;

public class Terreno {
    private String nombre;
    private double area;
    private List<Marcador> marcadores;

    public Terreno() {
        // Constructor vacío requerido para Firebase
    }

    public Terreno(String nombre, double area, List<Marcador> marcadores) {
        this.nombre = nombre;
        this.area = area;
        this.marcadores = marcadores;
    }

    // Métodos getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public List<Marcador> getMarcadores() {
        return marcadores;
    }

    public void setMarcadores(List<Marcador> marcadores) {
        this.marcadores = marcadores;
    }
}
