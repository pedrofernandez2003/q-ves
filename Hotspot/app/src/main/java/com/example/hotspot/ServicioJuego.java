package com.example.hotspot;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                        public void conectar(int estado, int bytes, int argumento, byte[] buffer) {
                            if (estado==1){
                                String tempMsg = new String(buffer, 0, bytes);
                                System.out.println("mensaje recibido "+tempMsg);
                                try {
                                    Gson json = new Gson();
                                    Mensaje mensaje = json.fromJson(tempMsg, Mensaje.class);
                                    System.out.println(mensaje.getAccion()+" "+mensaje.getDatos());
                                    switch (mensaje.getAccion()){
                                        case "comenzar":
                                            try {
                                                Juego juego = json.fromJson(mensaje.getDatos().get(0), Juego.class);
                                                GameContext.setJuego(juego);
                                                //aca
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
                                            byte[] bytesMsg = msg.getBytes();
                                            Write escribir = new Write();
                                            escribir.execute(bytesMsg, 0);
                                            break;
                                        case "turno":
                                            HashMap<String, String> mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(0),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                                for (Map.Entry<String, String> aux: mapDatos.entrySet()){
                                                    System.out.println("clave "+aux.getKey());
                                                    System.out.println("valor "+aux.getValue());
                                                }
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            System.out.println(mapDatos.get("idJugador"));
                                            if (mapDatos.get("idJugador").equals(GameContext.getNombresEquipos().get(0))){
                                                Intent intent= new Intent();
                                                intent.setAction("turno");
                                                contexto.sendBroadcast(intent);
                                            }
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    System.out.println("se conecto un equipo");
                    break;
                case "crear server":
                    server= new ThreadedEchoServer();
                    server.start();
                    server.callbackMensaje=new conectarCallback() {
                        @Override
                        public void conectar(int estado, int bytes, int argumento, byte[] buffer) {
                            if (estado==1){
                                String tempMsg = new String(buffer, 0, bytes);
                                System.out.println("mensaje recibido "+tempMsg);
                                try {
                                    Gson json = new Gson();
                                    Mensaje mensaje = json.fromJson(tempMsg, Mensaje.class);
                                    switch (mensaje.getAccion()){
                                        case "conectado":
                                            GameContext.getNombresEquipos().add(mensaje.getDatos().get(0));
                                            System.out.println(mensaje.getAccion()+" "+mensaje.getDatos());
                                            Intent intent2= new Intent();
                                            intent2.setAction("nuevo equipo");
                                            contexto.sendBroadcast(intent2);
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    System.out.println("se creo el server");
                    break;
            }
        }
    };
    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
