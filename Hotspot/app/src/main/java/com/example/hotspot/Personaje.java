package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

public class Personaje implements Serializable{
    private String foto;
    private String nombre;
    
    public Personaje(String foto,String nombre){
        this.nombre=nombre;
        this.foto=foto;
    }

    public Personaje(){
        this.nombre="prueba";
        this.foto="prueba.jpg";
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
        HashMap<String, Object>informacion=new HashMap<>();
        informacion.put("Personaje",this);
        String json = serializador.toJson(informacion);
        return json;
    }
}
