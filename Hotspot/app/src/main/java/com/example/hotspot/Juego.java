package com.example.hotspot;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Set;

public class Juego {
    private String codigo;
//    private Array<Partida> partidas;
//    private Array<Equipo> equipos;
    private Plantilla plantilla;
    private Set<Tarjeta> mazo;

//    public Juego(String codigo, Array<Partida> partidas, Array<Equipo> equipos, Plantilla plantilla, Set<Tarjeta> mazo){
//        this.codigo=codigo;
//        this.partidas=partidas;
//        this.equipos=equipos;
//        this.plantilla=plantilla;
//        this.mazo=mazo;
//    }
//
//    public Array<Equipo> getEquipos() {
//        return equipos;
//    }
//
//    public Array<Partida> getPartidas() {
//        return partidas;
//    }

    public Plantilla getPlantilla() {
        return plantilla;
    }

    public Set<Tarjeta> getMazo() {
        return mazo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

//    public void setEquipos(Array<Equipo> equipos) {
//        this.equipos = equipos;
//    }

    public void setMazo(Set<Tarjeta> mazo) {
        this.mazo = mazo;
    }

//    public void setPartidas(Array<Partida> partidas) {
//        this.partidas = partidas;
//    }

    public void setPlantilla(Plantilla plantilla) {
        this.plantilla = plantilla;
    }
}
