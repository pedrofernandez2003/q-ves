package com.example.hotspot;

import android.graphics.ColorSpace;

import com.google.gson.Gson;

import java.util.HashMap;

public class Color implements Serializable{
    private String nombre;
    private ColorSpace.Rgb rgb;

    public Color(String nombre, ColorSpace.Rgb rgb) {
        this.nombre = nombre;
        this.rgb = rgb;
    }
    public Color(){
        this.nombre="rojo";
//        this.rgb=new ColorSpace.Rgb();
    }

    public String getNombre() {
        return nombre;
    }

//    public ColorSpace.Rgb getRgb() {
//        return rgb;
//    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

//    public void setRgb(ColorSpace.Rgb rgb) {
//        this.rgb = rgb;
//    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}
