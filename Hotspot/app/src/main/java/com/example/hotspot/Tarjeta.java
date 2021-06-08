package com.example.hotspot;

public class Tarjeta {
    private Categoria categoria;
    private String contenido;
    private String yapa;

    public Tarjeta(Categoria categoria, String contenido, String yapa) {
        this.categoria = categoria;
        this.contenido = contenido;
        this.yapa = yapa;
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
}
