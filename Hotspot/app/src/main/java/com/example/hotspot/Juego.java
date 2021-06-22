package com.example.hotspot;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Juego implements Serializable {
    private String codigo;
    private ArrayList<Partida> partidas;
    private ArrayList<Equipo> equipos;
    private Plantilla plantilla;
    private HashSet<Tarjeta> mazo;

    public Juego(String codigo, ArrayList<Partida> partidas, ArrayList<Equipo> equipos, Plantilla plantilla, HashSet<Tarjeta> mazo){
        this.codigo=codigo;
        this.partidas=partidas;
        this.equipos=equipos;
        this.plantilla=plantilla;
        this.mazo=mazo;
    }
    public Juego(){
        this.codigo="abc";
        this.partidas=new ArrayList<>();
        this.equipos=new ArrayList<>();
        this.plantilla=new Plantilla();
        this.mazo=new HashSet<>();
    }

    public ArrayList<Equipo> getEquipos() {
        return equipos;
    }

    public ArrayList<Partida> getPartidas() {
        return partidas;
    }

    public Plantilla getPlantilla() {
        return plantilla;
    }

    public HashSet<Tarjeta> getMazo() {
        return mazo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setEquipos(ArrayList<Equipo> equipos) {
        this.equipos = equipos;
    }

    public void setMazo(HashSet<Tarjeta> mazo) {
        this.mazo = mazo;
    }

    public void setPartidas(ArrayList<Partida> partidas) {
        this.partidas = partidas;
    }

    public void setPlantilla(Plantilla plantilla) {
        this.plantilla = plantilla;
    }

    @Override
    public String serializar() {
        Gson serializador=new Gson();
//        HashMap<String, String>informacion=new HashMap<>();
//        informacion.put("Juego",this);
        String json = serializador.toJson(this);
        return json;
    }
}
