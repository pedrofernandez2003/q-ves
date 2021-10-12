package com.example.objetos;

import com.example.interfaces.Serializable;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Mensaje implements Serializable {
    private String accion;
    private ArrayList<String> datos;

    public Mensaje(String accion, ArrayList<String> datos){
         this.accion=accion;
         this.datos=datos;
    }

    public String getAccion() {
        return accion;
    }

    public ArrayList<String> getDatos() {
        return datos;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setDatos(ArrayList<String> datos) {
        this.datos = datos;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}
