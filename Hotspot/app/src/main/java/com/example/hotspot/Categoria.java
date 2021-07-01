package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

public class Categoria implements Serializable {
    private int cantidadTarjetas;
    private String nombre;
    private Color color;

    public Categoria(String nombre, Color color, int cantidadTarjetas){
        this.nombre = nombre;
        this.color=color;
        this.cantidadTarjetas=cantidadTarjetas;
    }
    public Categoria(){
        this.nombre="Vivienda";
        this.color=Color.AMARILLO;
    }

    public int getCantidadTarjetas() {
        return cantidadTarjetas;
    }

    public void setCantidadTarjetas(int cantidadTarjetas) {
        this.cantidadTarjetas = cantidadTarjetas;
    }

    public String getnombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}