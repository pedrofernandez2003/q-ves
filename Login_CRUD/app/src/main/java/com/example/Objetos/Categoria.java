package com.example.Objetos;

public class Categoria {

    private String nombre;
    private String color;

    public Categoria(String nombre, String color){
        this.nombre = nombre;
        this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public String getColor() {
        return color;
    }
}
