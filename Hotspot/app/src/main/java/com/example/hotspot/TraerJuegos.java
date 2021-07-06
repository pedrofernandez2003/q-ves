package com.example.hotspot;
import com.example.hotspot.EchoThread;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TraerJuegos extends AppCompatActivity {
    private static final String TAG = "";
    static final int MESSAGE_READ=1;
//    private ServerClass serverClass;
    private ClientClass clientClass;
//    private SendReceive sendReceive;
    private TextView textoCargando, IPDispositivo;


    private WifiManager wifiManager;
    private WifiConfiguration currentConfig;
    private WifiManager.LocalOnlyHotspotReservation hotspotReservation;
    private Controlador controlador;
    private Juego juego;
    private Equipo equipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traer_juegos);
        textoCargando = findViewById(R.id.textoCargando);
        IPDispositivo = findViewById(R.id.ipDispositivo);
        wifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        currentConfig=new WifiConfiguration();
        juego=new Juego();
        equipo=new Equipo();
        mostrarPlantillas(this.getApplicationContext());
    }

    private void mostrarPlantillas(Context context)  {
        DataManagerPlantillas.traerPlantillas(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                for (Object PlantillaObject:datos) {
                    Plantilla plantilla= (Plantilla) PlantillaObject;
                    LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                    Button button = new Button(context);
                    button.setLayoutParams(lp);
                    button.setText(plantilla.getNombre());
                    button.setBackgroundColor(939393);
                    llBotonera.addView(button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(View v) {
                            System.out.println("deberia comenzar la partida");
                            IPDispositivo.setText(getIPAddress(true));
//                            turnOnHotspot();
                            ThreadedEchoServer server=new ThreadedEchoServer();
                            server.start();
//                            clientClass = new ClientClass("192.168.43.1");
//                            clientClass.start();
                            textoCargando.setVisibility(View.VISIBLE);
//                            ArrayList<String> datos=new ArrayList<>();
//                            datos.add(juego.serializar());
////                datos.add("\"turno\":1");
//                            datos.add(equipo.serializar());
//                            Mensaje mensaje=new Mensaje("comenzar",datos);
//                            String msg=mensaje.serializar();
//                            System.out.println(msg);
//                            byte[] bytesMsg = msg.getBytes();
//                            for (int i = 0; i < bytesMsg.length; i++) {
//                                System.out.println(bytesMsg[i]);
//                            }
//                            System.out.println("tocaste comenzar");
//                            Write escribir = new Write();
//                            escribir.execute(bytesMsg);
                        }
                    });
                }
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    System.out.println(tempMsg);
                    Gson json = new Gson();
                    Mensaje mensaje= json.fromJson(tempMsg, Mensaje.class);
                    Juego juego= json.fromJson(mensaje.getDatos().get(0), Juego.class);
                    Equipo equipo= json.fromJson(mensaje.getDatos().get(1), Equipo.class);
                    System.out.println(juego.getCodigo());
                    System.out.println(equipo.getNombre());
                    Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });


    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;

        public ClientClass(String hostAddress) {
            hostAdd = hostAddress;
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                System.out.println("entre al run client");
                socket.connect(new InetSocketAddress(hostAdd, 7028), 5000);
//                sendReceive = new SendReceive(socket);
//                sendReceive.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
//
//        public void write(byte[] bytes) {
//            try {
//                outputStream.write(bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


//    public class Write extends AsyncTask {
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            try {
//                (EchoThread) objects[1].write((byte[]) objects[0]);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }


    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    private void hotspotDetailsDialog() {
        Log.v(TAG, this.getApplicationContext().getString(R.string.hotspot_details_message) + "\n" + this.getApplicationContext().getString(
                R.string.hotspot_ssid_label) + " " + currentConfig.SSID + "\n" + this.getApplicationContext().getString(
                R.string.hotspot_pass_label) + " " + currentConfig.preSharedKey);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void turnOnHotspot() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    hotspotReservation = reservation;
                    currentConfig = hotspotReservation.getWifiConfiguration();
                    Log.v("DANG", "THE PASSWORD IS: " + currentConfig.preSharedKey + " \n SSID is : " + currentConfig.SSID);
                    hotspotDetailsDialog();
                    IPDispositivo.setText(getIPAddress(true)); //agrego esto porque sino se queda con la ip de la red de mi casa
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    Log.v("DANG", "Local Hotspot Stopped");
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Log.v("DANG", "Local Hotspot failed to start");
                }
            }, new Handler());
        }
    }

}
