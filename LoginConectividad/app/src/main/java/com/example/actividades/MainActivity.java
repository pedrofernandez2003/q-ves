package com.example.actividades;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.objetos.Equipo;
import com.example.objetos.GameContext;
import com.example.R;

import androidx.appcompat.app.AppCompatActivity;

import com.example.objetos.ServicioJuego;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private DhcpInfo dhcpInfo;
    private Button botonUnirse, botonIniciarSesion;
    private TextInputEditText nombreEquipo;
    private TextView turno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("comenzar");
        intentFilter.addAction("turno");
        registerReceiver(broadcastReceiver,intentFilter);
        startService(new Intent(this, ServicioJuego.class));
        setContentView(R.layout.activity_main);
        botonUnirse = (Button) findViewById(R.id.botonUnirse);
        turno = findViewById(R.id.turno);
        nombreEquipo =  (TextInputEditText) findViewById(R.id.nombreEquipo);
        botonIniciarSesion=(Button)findViewById(R.id.iniciarSesion);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Context appContext=this;
        botonUnirse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dhcpInfo=wifiManager.getDhcpInfo();
                String codigo = formatIP(dhcpInfo.gateway);
                Intent intent= new Intent();
                intent.setAction("unirse");
                intent.putExtra("codigo",codigo);
                appContext.sendBroadcast(intent);
                GameContext.setEquipo(new Equipo());
                GameContext.getNombresEquipos().add(nombreEquipo.getText().toString());
                setContentView(R.layout.cargando);
            }
        });

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("On click");
                Intent elegirPlantilla = new Intent(MainActivity.this, com.example.actividades.LoginActivity.class);
                startActivity(elegirPlantilla);
            }
        });
    }
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "comenzar":
                    System.out.println("comienza");
                    GameContext.setServer(null);//creo que no hace falta igual
                    empezarJuego();
                    break;
                case "turno":
                    System.out.println("modifico el label turno");
                    setContentView(R.layout.tablero_creable);
                    turno = findViewById(R.id.turno);
                    turno.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public String formatIP(int IpAddress) {
        return Formatter.formatIpAddress(IpAddress);
    }

    private void empezarJuego(){
        unregisterReceiver(broadcastReceiver);
        Intent partida = new Intent(this, JugarActivity.class);
        startActivity(partida);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}