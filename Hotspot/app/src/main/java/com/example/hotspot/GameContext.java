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
//import android.content.Context;

public class GameContext extends Thread {
    private static GameContext context;
    private static ThreadedEchoServer server;
    public static List<ThreadedEchoServer> servers = new ArrayList<ThreadedEchoServer>();
    private static ArrayList<SendReceive> hijos=new ArrayList<>();
    private static ArrayList<String> nombresEquipos=new ArrayList<>();
    private static Juego juego;
//    private static Context contextoTraerJuego;

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
//
//    public static Context getContextoTraerJuego() {
//        return contextoTraerJuego;
//    }
//
//    public static void setContextoTraerJuego(Context context) {
//        GameContext.contextoTraerJuego = context;
//    }

    public static void setNombresEquipos(ArrayList<String> nombresEquipos) {
        GameContext.nombresEquipos = nombresEquipos;
    }
}
