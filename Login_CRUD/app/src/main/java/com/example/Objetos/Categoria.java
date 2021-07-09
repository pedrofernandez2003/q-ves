package com.example.Objetos;

public class Categoria {

    private String nombre;
    private Color color;
    private int cantidadTarjetas;

    public Categoria(String nombre, String nombreColor, int cantidadTarjetas){
        for(Color color:Color.values()){
            if (nombreColor.equals(color.toString())){
                this.color= color;
            }
        }
        this.nombre = nombre;
        this.cantidadTarjetas=cantidadTarjetas;
    }
    public Categoria(){

    }

    public int getCantidadTarjetas() {
        return cantidadTarjetas;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setColor(Color color) {
        this.color = color;
    }


}
