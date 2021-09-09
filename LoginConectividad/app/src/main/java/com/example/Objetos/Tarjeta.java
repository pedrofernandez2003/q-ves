package com.example.Objetos;

public class Tarjeta {
    private String contenido;
    private String  yapa;

    public Tarjeta(String  contenido, String  yapa) {
        this.contenido = contenido;
        this.yapa = yapa;
    }

    public Tarjeta() {
    }


    public String  getContenido() {
        return contenido;
    }

    public void setContenido(String  contenido) {
        this.contenido = contenido;
    }

    public String  getYapa() {
        return yapa;
    }

    public void setYapa(String  yapa) {
        this.yapa = yapa;
    }
}
