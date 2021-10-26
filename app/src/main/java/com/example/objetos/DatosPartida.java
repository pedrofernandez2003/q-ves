package com.example.objetos;

import com.example.interfaces.Serializable;
import com.google.gson.Gson;

public class DatosPartida implements Serializable {
    private String ronda;
    private String juego;

    public DatosPartida(String ronda, String juego) {
        this.ronda = ronda;
        this.juego = juego;
    }

    public String getRonda() {
        return ronda;
    }

    public void setRonda(String ronda) {
        this.ronda = ronda;
    }

    public String getJuego() {
        return juego;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}
