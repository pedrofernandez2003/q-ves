package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

public class Serializador {
    private Gson serializador;

    protected Serializador(){
        this.serializador = new Gson();
    }

    protected String formatoComenzar(String datos, String accion){
        String mensaje = "{ \"accion\":"+"\"" + accion +"\""+ "," + "\"datos\": " + datos + "}";
        return mensaje;
    }
}