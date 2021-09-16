package com.example.objetos;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.interfaces.conectarCallback;
import com.example.objetos.manejoSockets.ClientClass;
import com.example.objetos.manejoSockets.ThreadedEchoServer;
import com.example.objetos.manejoSockets.Write;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;

public class ServicioJuego extends Service {
    private ThreadedEchoServer server;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("unirse");
        intentFilter.addAction("crear server");
        registerReceiver(broadcastReceiver,intentFilter);
    }
    Context contexto=this;
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("accion: "+intent.getAction());
            switch (intent.getAction()){
                case "unirse":
                    ClientClass clientClass = new ClientClass(intent.getStringExtra("codigo"));
                    clientClass.start();
                    clientClass.callbackMensaje=new conectarCallback() {
                        @Override
                        public void conectar(int estado, String buffer) {
                            if (estado==1){
                                System.out.println("mensaje recibido "+buffer);
                                try {
                                    Gson json = new Gson();
                                    Mensaje mensaje = json.fromJson(buffer, Mensaje.class);
                                    switch (mensaje.getAccion()){
                                        case "comenzar":
                                            try {
                                                Juego juego = json.fromJson(mensaje.getDatos().get(0), Juego.class);
                                                GameContext.setJuego(juego);
                                                GameContext.setPartidaActual(juego.getPartidas().get(0));
                                                GameContext.setRonda(1);
                                                Equipo equipo= new Equipo(juego.getMazo(),GameContext.getNombresEquipos().get(0));
                                                GameContext.setEquipo(equipo);
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            Intent intent2= new Intent();
                                            intent2.setAction("comenzar");
                                            contexto.sendBroadcast(intent2);
                                            break;
                                        case "conectar":
                                            System.out.println("me llego conectar");
                                            ArrayList<String> datos=new ArrayList<>();
                                            datos.add(GameContext.getNombresEquipos().get(0));
                                            mensaje=new Mensaje("conectado",datos);
                                            String msg=mensaje.serializar();
                                            Write escribir = new Write();
                                            escribir.execute(msg, 0);
                                            break;
                                        case "turno":
                                            HashMap<String, String> mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(0),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            if (mapDatos.get("idJugador").equals(GameContext.getNombresEquipos().get(0))){
                                                GameContext.setEsMiTurno(true);
                                                Intent intent= new Intent();
                                                intent.setAction("turno");
                                                contexto.sendBroadcast(intent);
                                            }
                                            else{
                                                GameContext.setEsMiTurno(false);
                                            }
                                            break;
                                        case "actualizacion_tablero":
                                            Tarjeta tarjeta= json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(1),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            Intent intent= new Intent();
                                            intent.setAction("actualizar");
                                            contexto.sendBroadcast(intent);
                                            System.out.println(mapDatos.get("idJugador")+" tiro esta carta "+tarjeta.getContenido());
                                            break;

                                        case "partida_nueva":
                                            GameContext.setRonda(GameContext.getRonda()+1);
                                            intent= new Intent();
                                            intent.setAction("reiniciar");
                                            contexto.sendBroadcast(intent);
                                            break;

                                        case "terminar_juego":
                                            datos=new ArrayList<>();
                                            datos.add("{\"cantidadTarjetas\": \""+GameContext.getEquipo().getTarjetas().size()+"\"}");
                                            datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(0)+"\"}");
                                            mensaje=new Mensaje("misCartas",datos);
                                            msg=mensaje.serializar();
                                            escribir = new Write();
                                            escribir.execute(msg, 0);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    break;
                case "crear server":
                    server= new ThreadedEchoServer();
                    server.start();
                    server.callbackMensaje=new conectarCallback() {
                        @Override
                        public void conectar(int estado, String buffer) {
                            if (estado==1){
                                try {
                                    Gson json = new Gson();
                                    Mensaje mensaje = json.fromJson(buffer, Mensaje.class);
                                    switch (mensaje.getAccion()){
                                        case "conectado":
                                            GameContext.getNombresEquipos().add(mensaje.getDatos().get(0));
                                            System.out.println(mensaje.getAccion()+" "+mensaje.getDatos());
                                            Intent intent2= new Intent();
                                            intent2.setAction("nuevo equipo");
                                            contexto.sendBroadcast(intent2);
                                            break;
                                        case "jugarListo":
                                            for (int i=0;i<GameContext.getHijos().size();i++) {
                                                ArrayList<String> datos=new ArrayList<>();
                                                datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(GameContext.getPartidaActual().getTurno())+"\"}");
                                                mensaje=new Mensaje("turno",datos);
                                                String msg=mensaje.serializar();
                                                Write escribir = new Write();
                                                escribir.execute(msg, i);
                                            }
                                            break;
                                        case "jugada":
                                            System.out.println("buffer "+buffer +"\n mensaje"+mensaje.getDatos().get(0));
                                            Tarjeta tarjeta= json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            HashMap<String, String> mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(1),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            String nombreEquipo= mapDatos.get("idJugador");
                                            if (GameContext.getPartidaActual().getTurno()==GameContext.getJuego().getEquipos().size()-1){
                                                GameContext.getPartidaActual().setTurno(0);
                                            }
                                            else{
                                                GameContext.getPartidaActual().setTurno(GameContext.getPartidaActual().getTurno()+1);//no se si esta bien aca
                                            }
                                            for (int i=0;i<GameContext.getHijos().size();i++){
                                                if(!GameContext.getNombresEquipos().get(i).equals(nombreEquipo)){ //para que no se lo mande al que jugo
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    datos.add(tarjeta.serializar());
                                                    datos.add("{\"idJugador\": \""+nombreEquipo+"\"}");
                                                    mensaje=new Mensaje("actualizacion_tablero",datos);
                                                    System.out.println("mande actualizacion");
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                            }
                                            boolean terminarPartida=true;
                                            System.out.println("casilleros "+GameContext.getPartidaActual().getCasilleros().size());
                                            for (Casillero casillero:GameContext.getPartidaActual().getCasilleros()) {
                                                if(casillero.getTarjeta()==null){
                                                    casillero.setTarjeta(tarjeta);
                                                    terminarPartida=false;
                                                    break;
                                                }
                                            }
                                            if (!terminarPartida){
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(GameContext.getPartidaActual().getTurno())+"\"}");
                                                    mensaje=new Mensaje("turno",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                            }
                                            else if (GameContext.getRonda()!=GameContext.getJuego().getPartidas().size()){
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    mensaje=new Mensaje("partida_nueva",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                                System.out.println("termina la ronda");
                                                GameContext.setRonda(GameContext.getRonda()+1);
                                            }
                                            else{
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    mensaje=new Mensaje("terminar_juego",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                                System.out.println("termina el juego");
                                            }
                                            break;

                                        case "misCartas":
                                            int cantidadCartas= Integer.parseInt(mensaje.getDatos().get(0));
                                            String nombreJugador= mensaje.getDatos().get(1);
                                            GameContext.getResultados().put(nombreJugador,cantidadCartas);
                                            GameContext.setCantMensajesRecibidos(GameContext.getCantMensajesRecibidos()+1);
                                            if(GameContext.getCantMensajesRecibidos()==GameContext.getJuego().getEquipos().size()){
                                                intent2= new Intent();
                                                intent2.putExtra("ganador",obtenerGanardor());
                                                intent2.setAction("ganador");
                                                contexto.sendBroadcast(intent2);
                                            }
                                            break;

                                        case "pasarTurno":
                                            if (GameContext.getPartidaActual().getTurno()==GameContext.getJuego().getEquipos().size()-1){
                                                GameContext.getPartidaActual().setTurno(0);
                                            }
                                            else{
                                                GameContext.getPartidaActual().setTurno(GameContext.getPartidaActual().getTurno()+1);//no se si esta bien aca
                                            }
                                            //ver esto
                                            terminarPartida=true;
                                            for (Casillero casillero:GameContext.getPartidaActual().getCasilleros()) {
                                                if(casillero.getTarjeta()==null){
                                                    terminarPartida=false;
                                                    break;
                                                }
                                            }
                                            if (!terminarPartida){
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(GameContext.getPartidaActual().getTurno())+"\"}");
                                                    mensaje=new Mensaje("turno",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                            }
                                            else if (GameContext.getRonda()!=GameContext.getJuego().getPartidas().size()){
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    mensaje=new Mensaje("partida_nueva",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                                GameContext.setRonda(GameContext.getRonda()+1);
                                            }
                                            else{
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    mensaje=new Mensaje("terminar_juego",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                            }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    break;
            }
        }
    };

    public String obtenerGanardor(){
        return "pepe";
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}