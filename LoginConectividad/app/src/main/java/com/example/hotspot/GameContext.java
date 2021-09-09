package com.example.hotspot;

import java.util.ArrayList;
import java.util.List;

public class GameContext extends Thread {
    private static GameContext context;
    private static ThreadedEchoServer server;
    public static List<ThreadedEchoServer> servers = new ArrayList<ThreadedEchoServer>();
    private static ArrayList<SendReceive> hijos=new ArrayList<>();
    private static ArrayList<String> nombresEquipos=new ArrayList<>();
    private static Juego juego;
    private static Partida partidaActual;
    private Equipo equipo;

    private GameContext(){}
    public static GameContext getGameContext() {
        if (context == null) {
            context = new GameContext();
        }
        return context;
    }

    public static void setPartidaActual(Partida partidaActual) {
        GameContext.partidaActual = partidaActual;
    }

    public static Partida getPartidaActual() {
        return partidaActual;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public static void setEquipo(Equipo equipo) {
        equipo = equipo;
    }

    public static Juego getJuego() {
        return juego;
    }

    public static void setJuego(Juego juego) {
        GameContext.juego = juego;
    }

    public static GameContext getContext() {
        return context;
    }

    public static List<ThreadedEchoServer> getServers() {
        return servers;
    }

    public static ThreadedEchoServer getServer() {
        return server;
    }

    public static void setServer(ThreadedEchoServer server) {
        GameContext.server = server;
    }
    public static void agregarHijo(SendReceive hijo){
        hijos.add(hijo);
    }

    public static ArrayList<SendReceive> getHijos() {
        return hijos;
    }

    public static ArrayList<String> getNombresEquipos() {
        return nombresEquipos;
    }

    public static void setNombresEquipos(ArrayList<String> nombresEquipos) {
        GameContext.nombresEquipos = nombresEquipos;
    }
}
