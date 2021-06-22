package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

public class Serializador {
    private Gson serializador;

    protected Serializador(){
        this.serializador = new Gson();
    }

    protected String formatoComenzar(Object objeto, String accion){
        HashMap<String, Object> mensajeHashMap = new HashMap<>();
        mensajeHashMap.put("ACCION", "COMENZAR");

        String json = this.serializador.toJson(mensajeHashMap);
        String mensaje = "{ ACCION: COMENZAR," + "DATA: " + json + "}";

        return new String();
    }
}