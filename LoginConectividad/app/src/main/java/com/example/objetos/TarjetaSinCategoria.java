package com.example.objetos;

public class TarjetaSinCategoria {
    private String contenido;
    private String  yapa;

    public TarjetaSinCategoria(String  contenido, String  yapa) {
        this.contenido = contenido;
        this.yapa = yapa;
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
