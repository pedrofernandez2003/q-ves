package com.example.hotspot;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameContext extends Thread {
    private static GameContext context;
    private static TraerJuegos.ThreadedEchoServer server;
    public static List<TraerJuegos.ThreadedEchoServer> servers = new ArrayList<TraerJuegos.ThreadedEchoServer>();
    private static ArrayList<SendReceive> hijos=new ArrayList<>();
    private static ArrayList<String> nombresEquipos=new ArrayList<>();
    private static Juego juego;

    private GameContext(){}
    public static GameContext getGameContext() {
        //instantiate a new CustomerLab if we didn't instantiate one yet
        if (context == null) {
            context = new GameContext();
        }
        return context;
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

    public static List<TraerJuegos.ThreadedEchoServer> getServers() {
        return servers;
    }

    public static TraerJuegos.ThreadedEchoServer getServer() {
        return server;
    }

    public static void setServer(TraerJuegos.ThreadedEchoServer server) {
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
