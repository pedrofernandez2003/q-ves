package com.example.hotspot;

import java.util.HashSet;

public class Equipo {
    private HashSet<Tarjeta>tarjetas;
    private String nombre;
    private int id;
    
    public Equipo(HashSet<Tarjeta> tarjetas, String nombre, int id){
        this.id=id;
        this.nombre=nombre;
        this.tarjetas=tarjetas;
    }
    
    public Equipo(){
        this.id=0;
        this.nombre="prueba";
        this.tarjetas=new HashSet<>();
    }

    public String getNombre() {
        return nombre;
    }

    public HashSet<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public int getId() {
        return id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTarjetas(HashSet<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }
}
