package com.example.objetos;

import com.example.interfaces.Serializable;
import com.google.gson.Gson;

public class DatosPartida implements Serializable {
    private String ronda;
    private String juego;
    private String tarjetas;
    private Tarjeta ultTarjeta;

    public DatosPartida(String ronda, String juego, String tarjetas,Tarjeta ultTarjeta) {
        this.ronda = ronda;
        this.juego = juego;
        this.tarjetas=tarjetas;
        this.ultTarjeta=ultTarjeta;
    }

    public Tarjeta getUltTarjeta() {
        return ultTarjeta;
    }

    public void setUltTarjeta(Tarjeta ultTarjeta) {
        this.ultTarjeta = ultTarjeta;
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

    public String getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(String tarjetas) {
        this.tarjetas = tarjetas;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}
