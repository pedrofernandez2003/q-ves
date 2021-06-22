package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

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
        HashMap<String, Object> informacion=new HashMap<>();
        informacion.put("Casillero",this);
        String json = serializador.toJson(informacion);
        return json;
    }
}
