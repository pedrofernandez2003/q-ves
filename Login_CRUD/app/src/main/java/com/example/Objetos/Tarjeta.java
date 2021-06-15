package com.example.Objetos;

public class Tarjeta {
    private Color contenido;
    private Color yapa;

    public Tarjeta(Color contenido, Color yapa) {
        this.contenido = contenido;
        this.yapa = yapa;
    }

    public Tarjeta() {
    }

    public Color getContenido() {
        return contenido;
    }

    public void setContenido(Color contenido) {
        this.contenido = contenido;
    }

    public Color getYapa() {
        return yapa;
    }

    public void setYapa(Color yapa) {
        this.yapa = yapa;
    }
}
