package com.example.objetos;

public class CategoriaSinTarjetas {

    private String nombre;
    private Color color;
    private int cantidadTarjetas;

    public CategoriaSinTarjetas(String nombre, String nombreColor, int cantidadTarjetas){
        for(Color color:Color.values()){
            if (nombreColor.equals(color.toString())){
                this.color= color;
            }
        }
        this.nombre = nombre;
        this.cantidadTarjetas=cantidadTarjetas;
    }
    public CategoriaSinTarjetas(){

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
