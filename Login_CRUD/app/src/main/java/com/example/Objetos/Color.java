package com.example.Objetos;

import android.graphics.ColorSpace;

public class Color {
    private String nombre;
    private ColorSpace.Rgb rgb;

    public Color(String nombre, ColorSpace.Rgb rgb) {
        this.nombre = nombre;
        this.rgb = rgb;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ColorSpace.Rgb getRgb() {
        return rgb;
    }

    public void setRgb(ColorSpace.Rgb rgb) {
        this.rgb = rgb;
    }

}
