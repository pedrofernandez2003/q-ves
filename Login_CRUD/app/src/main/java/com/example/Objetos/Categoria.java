package com.example.Objetos;

public class Categoria {

    private String nombre;
    private Color color;
    private int cantidadTarjetas;

    public Categoria(String nombre, Color color, int cantidadTarjetas){
        this.nombre = nombre;
        this.color = color;
        this.cantidadTarjetas=cantidadTarjetas;
    }
    public Categoria(){

    }

    public int getCantidadTarjetas() {
        return cantidadTarjetas;
    }

    public void setCantidadTarjetas(int cantidadTarjetas) {
        this.cantidadTarjetas = cantidadTarjetas;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }
}
