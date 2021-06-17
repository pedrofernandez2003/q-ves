package com.example.hotspot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Plantilla {
    private ArrayList<Categoria> categorias;
    private ArrayList<Personaje> personajes;
    private String nombre;
    private int cantPartidas;
//    private Moderador moderador;
    private int cantEquipos;
    private PlantillaBuilder plantillaBuilder;

    public Plantilla(){
        
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

//    public void setModerador(Moderador moderador) {
//        this.moderador = moderador;
//    }

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

//    public Moderador getModerador() {
//        return moderador;
//    }

    public String getNombre() {
        return nombre;
    }
}
