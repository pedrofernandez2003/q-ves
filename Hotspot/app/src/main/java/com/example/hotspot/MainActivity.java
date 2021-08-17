package com.example.hotspot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    private SendReceive sendReceive;
    private ClientClass clientClass;
    static final int MESSAGE_READ = 1;
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
        registerReceiver(broadcastReceiver,intentFilter);
        startService(new Intent(this,ServicioJuego.class));
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
                intent.setAction("nuevo equipo");
                intent.putExtra("codigo",codigo);
                appContext.sendBroadcast(intent);
//                clientClass = new ClientClass(codigo);
//                clientClass.start();
                System.out.println("nombre equipo: "+nombreEquipo.getText().toString());
                GameContext.getNombresEquipos().add(nombreEquipo.getText().toString());
                setContentView(R.layout.cargando);
            }
        });

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("On click");
                Intent elegirPlantilla = new Intent(appContext, TraerJuegos.class);
                startActivity(elegirPlantilla);
            }
        });
    }
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("accion main activity: "+intent.getAction());
            switch (intent.getAction()){
                case "comenzar":
                    System.out.println("comienza");
                    empezarJuego();
                    break;
            }
        }
    };

    public String formatIP(int IpAddress) {
        return Formatter.formatIpAddress(IpAddress);
    }

    private void empezarJuego(){
        unregisterReceiver(broadcastReceiver);
        Intent partida = new Intent(this, Jugar.class);
        startActivity(partida);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

//    @Override
//    protected void onPause() {
//        unregisterReceiver(broadcastReceiver);
//        super.onPause();
//    }
    //    Handler handler = new Handler(new Handler.Callback() {
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
//                            empezarJuego();
//                        }
//                        else if(mensaje.getAccion().equals("turno")) {
//                            System.out.println("entre turno");
//                            System.out.println(GameContext.getNombresEquipos().get( GameContext.getJuego().getPartidas().get(0).getTurno())+"  "+nombreEquipo.getText().toString());
//                            if(GameContext.getNombresEquipos().get( GameContext.getJuego().getPartidas().get(0).getTurno()).equals(nombreEquipo.getText().toString())){
//                                System.out.println("entre porque es mi turno");
//                                turno.setVisibility(View.VISIBLE);
//                            }
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
//    });

//    private class SendReceive extends Thread {
//        private Socket socket;
//        private InputStream inputStream;
//        private OutputStream outputStream;
//
//        public SendReceive(Socket skt) {
//            System.out.println("entre al constructor");
//            socket = skt;
//            try {
//                System.out.println("se construyo el sendReceive");
//                inputStream = socket.getInputStream();
//                outputStream = socket.getOutputStream();
//            } catch (IOException e) {
//                System.out.println("entre al catch");
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            byte[] buffer = new byte[1024];
//            int bytes;
//            while (socket != null) {
//                try {
//                    bytes = inputStream.read(buffer);
//                    if (bytes > 0) {
//                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

//        public void write(byte[] bytes) {
//            try {
//                outputStream.write(bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public class ClientClass extends Thread {
//        Socket socket;
//        String hostAdd;
//
//        public ClientClass(String hostAddress) {
//            hostAdd = hostAddress;
//            socket = new Socket();
//        }
//
//        @Override
//        public void run() {
//            try {
//                System.out.println("entre al run client");
//                socket.connect(new InetSocketAddress(hostAdd, 7028), 5000);
//                sendReceive = new SendReceive(socket);
//                sendReceive.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}