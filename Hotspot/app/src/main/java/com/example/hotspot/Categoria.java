package com.example.hotspot;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class Categoria implements Serializable {
//    private int cantidadTarjetas;
//    private String nombre;
//    private Color color;
//    private ArrayList<Tarjeta> tarjetas;
//
////    public Categoria(String nombre, Color color, int cantidadTarjetas,ArrayList<Tarjeta> tarjetas){
////        this.nombre = nombre;
////        this.color=color;
////        this.cantidadTarjetas=cantidadTarjetas;
////        this.tarjetas=tarjetas;
////    }
//    public Categoria(String nombre, Color color, int cantidadTarjetas){
//        this.nombre = nombre;
//        this.color=color;
//        this.cantidadTarjetas=cantidadTarjetas;
//        this.tarjetas=tarjetas;
//    }
//    public Categoria(){
//        this.nombre="Vivienda";
//        this.color=Color.AMARILLO;
//    }
//
//    public int getCantidadTarjetas() {
//        return cantidadTarjetas;
//    }
//
//    public void setCantidadTarjetas(int cantidadTarjetas) {
//        this.cantidadTarjetas = cantidadTarjetas;
//    }
//
//    public ArrayList<Tarjeta> getTarjetas() {
//        return tarjetas;
//    }
//
//    public void setTarjetas(ArrayList<Tarjeta> tarjetas) {
//        this.tarjetas = tarjetas;
//    }
//
//    public String getnombre() {
//        return nombre;
//    }
//
//    public Color getColor() {
//        return color;
//    }
//
//    public void setNombre(String nombre) {
//        this.nombre = nombre;
//    }
//
//    public void setColor(Color color) {
//        this.color = color;
//    }
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
    public Categoria(){ }

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
    
    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}