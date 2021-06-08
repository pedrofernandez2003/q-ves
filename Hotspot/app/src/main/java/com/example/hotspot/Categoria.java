package com.example.hotspot;

public class Categoria {
//    CARACTERISTICAS("Caracteristicas",Color.AMARILLO), VIVIENDA("Vivienda",Color.VERDE),
//    SEXUALIDAD("Sexualidad",Color.CELESTE), PASATIEMPO("Pasatiempo",Color.TURQUESA),
//    CUERPO("Cuerpo", Color.AZUL), INFANCIA("Infancia", Color.AZULFRANCIA), MEDIOS("Medios", Color.VIOLETA),
//    CALLE("Calle",Color.ROJO), TRABAJO("Trabajo", Color.ROSA), EMOCIONES("Emociones", Color.NARANJA),
//    ANULADA("Anulada",Color.NEGRO);

    private String nombre;
    private Color color;

    private Categoria(String nombre, Color color){
        this.nombre = nombre;
        this.color=color;
    }

    public String getnombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }
}