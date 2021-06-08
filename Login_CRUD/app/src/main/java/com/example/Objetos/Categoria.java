package com.example.Objetos;

public class Categoria {

    private String nombre;
    private Color color;

    public Categoria(String nombre, Color color){
        this.nombre = nombre;
        this.color=color;
    }

    public String getnombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }
}
