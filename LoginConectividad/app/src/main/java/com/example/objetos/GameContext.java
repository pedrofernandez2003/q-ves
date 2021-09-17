package com.example.objetos;

import com.example.objetos.manejoSockets.SendReceive;
import com.example.objetos.manejoSockets.ThreadedEchoServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameContext extends Thread {
    private static GameContext context;
    private static ThreadedEchoServer server;
    public static List<ThreadedEchoServer> servers = new ArrayList<ThreadedEchoServer>();
    private static ArrayList<SendReceive> hijos=new ArrayList<>();
    private static ArrayList<String> nombresEquipos=new ArrayList<>();
    private static Juego juego;
//    private static Partida partidaActual;
    private static int ronda;
    private static Equipo equipo;
    private static boolean esMiTurno;
    private static HashMap<String, Integer> resultados=new HashMap<>();
    private static int cantMensajesRecibidos=0; //los jugadores empiezan a mandar la cantidad de cartas que les quedan
    private static Tarjeta tarjetaElegida;

    private GameContext(){}
    public static GameContext getGameContext() {
        if (context == null) {
            context = new GameContext();
        }
        return context;
    }

    public static int getCantMensajesRecibidos() {
        return cantMensajesRecibidos;
    }

    public static void setCantMensajesRecibidos(int cantMensajesRecibidos) {
        GameContext.cantMensajesRecibidos = cantMensajesRecibidos;
    }

    public static HashMap<String, Integer> getResultados() {
        return resultados;
    }

    public static boolean isEsMiTurno() {
        return esMiTurno;
    }


    public static int getRonda() {
        return ronda;
    }

    public static void setRonda(int ronda) {
        GameContext.ronda = ronda;
    }

    public static void setEsMiTurno(boolean esMiTurno) {
        GameContext.esMiTurno = esMiTurno;
    }


    public static Equipo getEquipo() {
        return equipo;
    }

    public static void setEquipo(Equipo equipo) {
        GameContext.equipo = equipo;
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

    public static Tarjeta getTarjetaElegida() {
        return tarjetaElegida;
    }

    public static void setTarjetaElegida(Tarjeta tarjetaElegida) {
        GameContext.tarjetaElegida = tarjetaElegida;
    }
}