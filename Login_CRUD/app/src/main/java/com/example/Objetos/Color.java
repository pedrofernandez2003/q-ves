package com.example.Objetos;


public class Color {
    private String nombre;
    private String rgb;

    public Color(String nombre, String rgb) {
        this.nombre = nombre;
        this.rgb = rgb;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRgb() {
        return rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

}
