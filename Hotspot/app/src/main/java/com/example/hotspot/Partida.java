package com.example.hotspot;

import android.app.Person;

import java.util.ArrayList;

public class Partida {
    private Personaje personaje;
    private ArrayList<Casillero> casilleros;
    private int turno;

    public Partida(Personaje personaje, ArrayList<Casillero>casilleros, int turno){
        this.personaje=personaje;
        this.casilleros=casilleros;
        this.turno=turno;
    }
    
    public Partida(){
        this.personaje = new Personaje();
        this.casilleros = new ArrayList<>();
        casilleros.add(new Casillero());
        this.turno = 0;
    }

    public ArrayList<Casillero> getCasilleros() {
        return casilleros;
    }

    public int getTurno() {
        return turno;
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public void setCasilleros(ArrayList<Casillero> casilleros) {
        this.casilleros = casilleros;
    }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }
}