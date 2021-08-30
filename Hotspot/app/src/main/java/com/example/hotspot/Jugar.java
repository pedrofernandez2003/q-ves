package com.example.hotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class Jugar extends AppCompatActivity  {
    private GameContext context;
    private Juego juego;
    private Partida partida;
    private TextView turno;

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("accion jugar: "+intent.getAction());
            switch (intent.getAction()){
                case "turno":
                    System.out.println("modifico el label turno");
                    turno = findViewById(R.id.turno);
                    turno.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("turno");
        registerReceiver(broadcastReceiver,intentFilter);
        setContentView(R.layout.tablero);
        context=GameContext.getGameContext();
        juego= GameContext.getJuego();
        System.out.println("codigo juego: "+juego.getCodigo());
        partida=juego.getPartidas().get(0);
        if (GameContext.getHijos().size()>1){//para que solo haga esto el server
            for (int i=0;i<GameContext.getHijos().size();i++) {
                ArrayList<String> datos=new ArrayList<>();
                System.out.println("turno de: "+GameContext.getNombresEquipos().get(partida.getTurno()));
                System.out.println("hijos "+GameContext.getHijos().size());
                datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(partida.getTurno())+"\"}");
                Mensaje mensaje=new Mensaje("turno",datos);
                String msg=mensaje.serializar();
                byte[] bytesMsg = msg.getBytes();
                Write escribir = new Write();
                escribir.execute(bytesMsg, i);
            }
        }
    }
    public void turnoVisible(){
        turno.setVisibility(View.VISIBLE);
    }
}