package com.example.objetos;

import com.example.interfaces.Serializable;
import com.google.gson.Gson;

public class Casillero implements Serializable {
    private Categoria categoria;
    private Tarjeta tarjeta;
    private int id;

    public Casillero(Categoria categoria){
        this.categoria = categoria;
        this.tarjeta = null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Tarjeta getTarjeta() {
        return tarjeta;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setTarjeta(Tarjeta tarjeta) {
        this.tarjeta = tarjeta;
    }
    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }
}
