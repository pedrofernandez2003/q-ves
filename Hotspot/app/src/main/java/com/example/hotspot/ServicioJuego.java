package com.example.hotspot;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServicioJuego extends Service {
    static final int MESSAGE_READ = 1;
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
                                            mensaje=new Mensaje("conectado",datos);
                                            String msg=mensaje.serializar();
                                            byte[] bytesMsg = msg.getBytes();
                                            Write escribir = new Write();
                                            escribir.execute(bytesMsg, 0);
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
    //    Handler handlerMensajes = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(@NonNull Message msg) {
//            switch (msg.what) {
//                case MESSAGE_READ:
//                    byte[] readBuff = (byte[]) msg.obj;
//                    String tempMsg = new String(readBuff, 0, msg.arg1);
//                    System.out.println("mensaje recibido "+tempMsg);
//                    try {
//                        Gson json = new Gson();
//                        Mensaje mensaje = json.fromJson(tempMsg, Mensaje.class);
//                        if (mensaje.getAccion().equals("comenzar")){
//                            Juego juego = json.fromJson(mensaje.getDatos().get(0), Juego.class);
//                            GameContext.setJuego(juego);
//                            Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
//                            System.out.println("setea el juego del game context-------------------------------------");
//                            Intent intent= new Intent();
//                            intent.setAction("comenzar");
//                            this.sendBroadcast(intent);
//                        }
//                        else if(mensaje.getAccion().equals("turno")) {
//                            System.out.println("entre turno");
//                            Intent intent= new Intent();
//                            intent.setAction("turno");
//                            this.sendBroadcast(intent);
//                            Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//            }
//            return true;
//        }
//
//        private void sendBroadcast(Intent intent) {
//            this.sendBroadcast(intent);
//        }
//    });
//
//    Handler handlerNuevoEquipo = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(@NonNull Message msg) {
//            Intent intent= new Intent();
//            intent.setAction("nuevo equipo");
//            this.sendBroadcast(intent);
//            return true;
//        }
//
//        private void sendBroadcast(Intent intent) {
//            this.sendBroadcast(intent);
//        }
//    });

}
