package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

public class Tarjeta implements Serializable {
    private String categoria;
    private String contenido;
    private String yapa;

    public Tarjeta(String contenido, String yapa, String categoria) {
        this.contenido = contenido;
        this.yapa = yapa;
        this.categoria= categoria;
    }

    public Tarjeta(){
        this.contenido = "cosas";
        this.yapa = "cosas yapa";
    }

    public String getCategoria() {
        return categoria;
    }

    public String getContenido() {
        return contenido;
    }

    public String getYapa() {
        return yapa;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setYapa(String yapa) {
        this.yapa = yapa;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}
