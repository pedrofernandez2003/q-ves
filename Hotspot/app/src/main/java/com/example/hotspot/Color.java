package com.example.hotspot;

import android.graphics.ColorSpace;

import com.google.gson.Gson;

import java.util.HashMap;

public  enum Color implements Serializable{
    AMARILLO("HOLA"), VERDE("1"), CELESTE("F"), TURQUESA("2"),
    AZUL("3"),  VIOLETA("4"),GRIS("5"), ROJO("6"), ROSA("7"), NARANJA("8"),
    NEGRO("9");

    //    private String nombre;
    private String codigo;

    Color(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(java.lang.String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
        String json = serializador.toJson(this);
        return json;
    }

}
