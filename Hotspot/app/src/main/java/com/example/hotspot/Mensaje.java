package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

public class Mensaje implements Serializable {
    private String accion;
    private String datos;

    public Mensaje(String accion, String datos){
         this.accion=accion;
         this.datos=datos;
    }

    public String getAccion() {
        return accion;
    }

    public String getDatos() {
        return datos;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }


    @Override
    public String serializar() {
        Gson serializador=new Gson();
//        HashMap<String, Object> informacion=new HashMap<>();
//        informacion.put("Mensaje",this);
        String json = serializador.toJson(this);
        return json;
    }
}
