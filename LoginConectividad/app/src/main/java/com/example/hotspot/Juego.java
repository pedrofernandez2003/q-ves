package com.example.hotspot;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;

public class Juego implements Serializable {
//    private String codigo;
    private ArrayList<Partida> partidas;
    private ArrayList<Equipo> equipos;
    private Plantilla plantilla;
    private HashSet<Tarjeta> mazo;

    public Juego(Plantilla plantilla){//sacamos el mazo
        this.partidas=new ArrayList<Partida>();
        for (int i=0;i<plantilla.getCantPartidas();i++){
            ArrayList<Casillero> casilleros=new ArrayList<>();
            for (int j=0;j<plantilla.getCategorias().size();j++){
                Casillero casillero=new Casillero();
                casilleros.add(casillero);
            }
            Partida partida=new Partida(plantilla.getPersonajes().get(i),casilleros,0);
            this.partidas.add(partida);
        }
        this.equipos=new ArrayList<Equipo>();
        this.plantilla=plantilla;
        this.mazo=new HashSet<Tarjeta>();
        for(int i=0;i<plantilla.getCantPartidas();i++){
            this.getPartidas().get(i).setPersonaje(plantilla.getPersonajes().get(i));
        }
        for (Categoria categoria:plantilla.getCategorias()) {
            for (Tarjeta tarjeta:categoria.getTarjetas()) {
                mazo.add(tarjeta);
            }
        }
    }
    public Juego(){
        this.partidas=new ArrayList<>();
        partidas.add(new Partida());
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
        String json = serializador.toJson(this);
        return json;
    }
}
