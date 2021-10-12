package com.example.objetos;

import com.example.interfaces.Serializable;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Categoria implements Serializable {
    private String nombre;
    private Color color;
    private int cantidadTarjetas;
    private ArrayList<Tarjeta> tarjetas;

    public Categoria(String nombre, String nombreColor, int cantidadTarjetas, ArrayList<Tarjeta>tarjetas){
        for(Color color:Color.values()){
            if (nombreColor.equals(color.toString())){
                this.color= color;
            }
        }
        this.nombre = nombre;
        this.cantidadTarjetas=cantidadTarjetas;
        this.tarjetas=tarjetas;
    }
    public Categoria(){
        this.cantidadTarjetas=0;
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

    public ArrayList<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(ArrayList<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}