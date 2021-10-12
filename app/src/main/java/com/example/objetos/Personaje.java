package com.example.objetos;

import com.example.interfaces.Serializable;
import com.google.gson.Gson;

public class Personaje implements Serializable{
    private String foto;
    private String nombre;

    public Personaje(String foto,String nombre){
        this.nombre=nombre;
        this.foto=foto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}