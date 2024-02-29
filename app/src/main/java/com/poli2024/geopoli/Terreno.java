package com.poli2024.geopoli;
import java.text.DecimalFormat;

public class Terreno {
    private String id; // Nuevo campo para el ID del terreno
    private String nombre;
    private Double area;
    private int numeroMarcadores;

    public Terreno() {
    }

    public Terreno(String id, String nombre, Double area, int numeroMarcadores) {
        this.id = id;
        this.nombre = nombre;
        this.area = area;
        this.numeroMarcadores = numeroMarcadores;
    }

    // Getters y setters para el campo id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public int getNumeroMarcadores() {
        return numeroMarcadores;
    }

    public void setNumeroMarcadores(int numeroMarcadores) {
        this.numeroMarcadores = numeroMarcadores;
    }

    // Método para obtener el área con dos decimales
    public String getAreaFormatted() {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(area);
    }
}
