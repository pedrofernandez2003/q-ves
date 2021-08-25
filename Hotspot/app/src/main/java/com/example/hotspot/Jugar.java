package com.example.hotspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
                    turno.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        turno = findViewById(R.id.turno);
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("turno");
        registerReceiver(broadcastReceiver,intentFilter);
        setContentView(R.layout.tablero);
        context=GameContext.getGameContext();
        juego= GameContext.getJuego();
        System.out.println("codigo juego: "+juego.getCodigo());
        partida=juego.getPartidas().get(0);
        ArrayList<String> datos=new ArrayList<>();
        datos.add("\"idJugador\": "+partida.getTurno());
        Mensaje mensaje=new Mensaje("turno",datos);
        String msg=mensaje.serializar();
//        System.out.println(msg);
        byte[] bytesMsg = msg.getBytes();
        Write escribir = new Write();
        for (int i=0;i<GameContext.getHijos().size();i++) {
            escribir.execute(bytesMsg, i);
        }
    }
    public void turnoVisible(){
        turno.setVisibility(View.VISIBLE);
    }
}