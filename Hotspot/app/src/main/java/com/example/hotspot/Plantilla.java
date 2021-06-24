package com.example.hotspot;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Plantilla implements Serializable {
    private ArrayList<Categoria> categorias;
    private ArrayList<Personaje> personajes;
    private String nombre;
    private int cantPartidas;
    private Moderador moderador;
    private int cantEquipos;

    public Plantilla(){
        this.categorias = new ArrayList<>();
        this.personajes = new ArrayList<>();
        this.nombre = "prueba";
        this.cantPartidas = 3;
        this.moderador = new Moderador();
        this.cantEquipos = 4;
    }

    public void setCantEquipos(int cantEquipos) {
        this.cantEquipos = cantEquipos;
    }

    public void setCantPartidas(int cantPartidas) {
        this.cantPartidas = cantPartidas;
    }

    public void setCategorias(ArrayList<Categoria> categorias) {
        this.categorias = categorias;
    }

    public void setModerador(Moderador moderador) {
        this.moderador = moderador;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPersonajes(ArrayList<Personaje> personajes) {
        this.personajes = personajes;
    }

    public ArrayList<Categoria> getCategorias() {
        return categorias;
    }

    public ArrayList<Personaje> getPersonajes() {
        return personajes;
    }

    public int getCantEquipos() {
        return cantEquipos;
    }

    public int getCantPartidas() {
        return cantPartidas;
    }

    public Moderador getModerador() {
        return moderador;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}
