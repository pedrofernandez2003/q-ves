package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

public class Tarjeta implements Serializable {
    private Categoria categoria;
    private String contenido;
    private String yapa;

    public Tarjeta(Categoria categoria, String contenido, String yapa) {
        this.categoria = categoria;
        this.contenido = contenido;
        this.yapa = yapa;
    }

    public Tarjeta(){
        this.categoria = new Categoria();
        this.contenido = "cosas";
        this.yapa = "cosas yapa";
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getContenido() {
        return contenido;
    }

    public String getYapa() {
        return yapa;
    }

    public void setCategoria(Categoria categoria) {
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
