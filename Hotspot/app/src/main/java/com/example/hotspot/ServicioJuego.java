package com.example.hotspot;

import android.app.Service;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServicioJuego extends Service {
    int turno;
    static final int MESSAGE_READ = 1;
    private ThreadedEchoServer server;
    private SendReceive sendReceive;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        turno=0;
    }

    public int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    Handler handlerMensajes = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    System.out.println("mensaje recibido "+tempMsg);
                    try {
                        Gson json = new Gson();
                        Mensaje mensaje = json.fromJson(tempMsg, Mensaje.class);
                        if (mensaje.getAccion().equals("comenzar")){
                            Juego juego = json.fromJson(mensaje.getDatos().get(0), Juego.class);
                            GameContext.setJuego(juego);
                            Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
                            System.out.println("setea el juego del game context-------------------------------------");
                            Intent intent= new Intent();
                            intent.setAction("comenzar");
                            this.sendBroadcast(intent);
                        }
                        else if(mensaje.getAccion().equals("turno")) {
                            System.out.println("entre turno");
                            Intent intent= new Intent();
                            intent.setAction("turno");
                            this.sendBroadcast(intent);
                            Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }

        private void sendBroadcast(Intent intent) {
            this.sendBroadcast(intent);
        }
    });

    Handler handlerNuevoEquipo = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Intent intent= new Intent();
            intent.setAction("nuevo equipo");
            this.sendBroadcast(intent);
            return true;
        }

        private void sendBroadcast(Intent intent) {
            this.sendBroadcast(intent);
        }
    });

}
