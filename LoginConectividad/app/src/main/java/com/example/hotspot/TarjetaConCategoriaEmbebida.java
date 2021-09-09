package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;



public class TarjetaConCategoriaEmbebida implements Serializable {
    private CategoriaSinTarjetas categoria;
    private String contenido;
    private String yapa;

    public TarjetaConCategoriaEmbebida(String contenido, String yapa, CategoriaSinTarjetas categoria) {
        this.contenido = contenido;
        this.yapa = yapa;
        this.categoria= categoria;
    }

    public TarjetaConCategoriaEmbebida(){
        this.contenido = "cosas";
        this.yapa = "cosas yapa";
    }

    public CategoriaSinTarjetas getCategoria() {
        return categoria;
    }

    public String getContenido() {
        return contenido;
    }

    public String getYapa() {
        return yapa;
    }

    public void setCategoria(CategoriaSinTarjetas categoria) {
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

