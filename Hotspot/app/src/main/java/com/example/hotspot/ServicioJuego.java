package com.example.hotspot;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

public class ServicioJuego extends Service {
    String prueba;
    static final int MESSAGE_READ = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prueba="hola";
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    Handler handler = new Handler(new Handler.Callback() {
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
                            System.out.println("setea el juego del game context-------------------------------------");
                        }
                        else if(mensaje.getAccion().equals("turno")) {
                            System.out.println("entre turno");
                            //System.out.println(GameContext.getNombresEquipos().get( GameContext.getJuego().getPartidas().get(0).getTurno())+"  "+nombreEquipo.getText().toString());
                            //if(GameContext.getNombresEquipos().get( GameContext.getJuego().getPartidas().get(0).getTurno()).equals(nombreEquipo.getText().toString())){
                            //    System.out.println("entre porque es mi turno");
                            //}
                            Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });
}
