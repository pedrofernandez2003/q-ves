package com.example.hotspot;

public class Moderador {
    // definir atributos
    private int id;
    private String nombre;

    public Moderador(int id, String nombre){
        this.nombre=nombre;
        this.id=id;
    }
    public Moderador(){
        this.id=1;
        this.nombre="prueba";
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}