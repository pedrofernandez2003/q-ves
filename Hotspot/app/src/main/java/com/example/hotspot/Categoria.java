package com.example.hotspot;

import com.google.gson.Gson;

import java.util.HashMap;

public class Categoria implements Serializable {
//    CARACTERISTICAS("Caracteristicas",Color.AMARILLO), VIVIENDA("Vivienda",Color.VERDE),
//    SEXUALIDAD("Sexualidad",Color.CELESTE), PASATIEMPO("Pasatiempo",Color.TURQUESA),
//    CUERPO("Cuerpo", Color.AZUL), INFANCIA("Infancia", Color.AZULFRANCIA), MEDIOS("Medios", Color.VIOLETA),
//    CALLE("Calle",Color.ROJO), TRABAJO("Trabajo", Color.ROSA), EMOCIONES("Emociones", Color.NARANJA),
//    ANULADA("Anulada",Color.NEGRO);

    private String nombre;
    private Color color;

    public Categoria(String nombre, Color color){
        this.nombre = nombre;
        this.color=color;
    }
    public Categoria(){
        this.nombre="Vivienda";
        this.color=new Color();
    }

    public String getnombre() {
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