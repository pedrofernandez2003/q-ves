package com.example.objetos;

import com.example.interfaces.Serializable;
import com.google.gson.Gson;

public class Casillero implements Serializable {
    private Categoria categoria;
    private Tarjeta tarjeta;

    public Casillero(Categoria categoria, Tarjeta tarjeta){
        this.categoria=categoria;
        this.tarjeta=tarjeta;
    }
    
    public Casillero(){
        this.categoria = new Categoria();
        this.tarjeta = new Tarjeta();
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
