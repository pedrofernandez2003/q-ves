package com.example.actividades;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.Person;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.listeners.onTraerDatosListener;
import com.example.objetos.Categoria;
import com.example.objetos.Equipo;
import com.example.objetos.GameContext;
import com.example.objetos.Juego;
import com.example.objetos.Mensaje;
import com.example.objetos.Personaje;
import com.example.objetos.Tarjeta;
import com.example.objetos.manejoSockets.ThreadedEchoServer;
import com.example.objetos.manejoSockets.Write;
import com.example.R;
import com.example.dataManagers.DataManagerPlantillas;
import com.example.objetos.Plantilla;
import com.example.objetos.ServicioJuego;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class TraerJuegosActivity extends AppCompatActivity {
    private static final String TAG = "";
    private int cantidadEquipos;
    private TextView textoCargando, nombreRed, claveRed, cantidadEquiposTextView;

    private WifiManager wifiManager;
    private WifiConfiguration currentConfig;
    private WifiManager.LocalOnlyHotspotReservation hotspotReservation;
    private Juego juego;
    private Button botonComenzarPartida;
    private ThreadedEchoServer server;
    private ArrayList<Categoria> categorias;
    private Boolean cantidadExacta = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("nuevo equipo");
        registerReceiver(broadcastReceiver,intentFilter);
        startService(new Intent(this, ServicioJuego.class));
        setContentView(R.layout.activity_traer_juegos);
        cantidadEquipos=0;
        textoCargando = findViewById(R.id.textoCargando);
        cantidadEquiposTextView = findViewById(R.id.cantidadEquipos);
        botonComenzarPartida = findViewById(R.id.botonComenzarPartida);
        nombreRed = findViewById(R.id.nombreRed);
        claveRed = findViewById(R.id.claveRed);
        wifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        currentConfig=new WifiConfiguration();
        juego=new Juego();
        mostrarPlantillas(this.getApplicationContext());

        botonComenzarPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Personaje personaje: juego.getPlantilla().getPersonajes()){
                    personaje.setNombre(traerPersonajesb64(personaje.getNombre()));

                }
                GameContext.setJuego(juego);
                GameContext.setRonda(1);
                categorias = juego.getPlantilla().getCategorias();
                ArrayList<HashSet<Tarjeta>> mazos = repartirTarjetas();
                for (int i=0;i<GameContext.getHijos().size();i++){ //le manda a todos los hijos la informacion de la partida
                    juego.setMazo(mazos.get(i));
                    GameContext.getJuego().getEquipos().add(new Equipo(juego.getMazo(),GameContext.getNombresEquipos().get(i)));
                    String juegoSerializado=juego.serializar();
                    ArrayList<String> datos=new ArrayList<>();
                    datos.add(juegoSerializado);
                    datos.add("\"turno\":"+i);
                    Mensaje mensaje=new Mensaje("comenzar",datos);
                    String msg=mensaje.serializar();
                    System.out.println(msg);
                    Write escribir = new Write();
                    escribir.execute(msg,i);
                }
                empezarJuego();
            }
        });
    }

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("accion traer juegos: "+intent.getAction());
            switch (intent.getAction()){
                case "nuevo equipo":
                    if(GameContext.getHijos().size() >= cantidadEquipos){
                        botonComenzarPartida.setVisibility(View.VISIBLE);
                    }
//                    else {
//                        cantidadEquiposTextView.setText(Integer.parseInt((String) cantidadEquiposTextView.getText())+1);
//                    }
                    break;
            }
        }
    };



    private String traerPersonajesb64(String url) {

        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return null;
    }
    private HashSet<Tarjeta> tresTarjetasPorCategoria(){
        int cantidadPartidas = juego.getPartidas().size();
        categorias = juego.getPlantilla().getCategorias();
        System.out.println("Cantidad tarjetas iniciales" +juego.getMazo().size());
        int contador=0;
        HashSet<Tarjeta> tarjetasARepartir = new HashSet<>();
        for (Categoria categoria: categorias) { //Selecciono 3 tarjetas por cada categoria
            for (Tarjeta tarjeta: juego.getMazo()){ //creo que es totalmente aleatorio, chequear eso
                if(tarjeta.getCategoria().equals(categoria.getNombre())){
                    tarjetasARepartir.add(tarjeta);
                    contador++;
                    System.out.println("agregue una carte de la categoria: "+ categoria.getNombre());
                }
                if(contador==cantidadPartidas){
                    contador=0;
                    break;
                }
            }
        }
        return tarjetasARepartir;
    }

    private void eliminarTarjetasDelMazo(HashSet<Tarjeta> tarjetasARepartir){
        for(Tarjeta tarjetaAEliminar: tarjetasARepartir ) { //Elimino del mazo las tarjetas ya seleccionadas para los equipos
            juego.getMazo().remove(tarjetaAEliminar);
        }
    }

    private HashSet<Tarjeta> establecerMismaCantidadDeCartas(HashSet<Tarjeta> tarjetasARepartir){
        if(tarjetasARepartir.size()%cantidadEquipos!=0) { //Chequeo que haya una cant iguales de tarjetas para repartir entre todos
            cantidadExacta = false;
            while (!cantidadExacta) { // Si no lo hay selecciono un random de tarjetas del mazo y lo agrego al array a repartir
                int size = juego.getMazo().size();
                int item = new Random().nextInt(size);
                int i = 0;
                HashSet<Tarjeta> tarjetasFinal = juego.getMazo(); // Copio el mazo a otro hashset asÃ­ puedo ir eliminando
                for (Tarjeta obj : tarjetasFinal) {
                    if (i == item) {
                        tarjetasARepartir.add(obj);
                        juego.getMazo().remove(obj);
                        break;
                    }
                    i++;
                }
                if(tarjetasARepartir.size()%cantidadEquipos==0){
                    cantidadExacta = true;
                }
            }
        }
        return tarjetasARepartir;
    }

    private ArrayList<HashSet<Tarjeta>> repartirTarjetas(){
        ArrayList<HashSet<Tarjeta>> mazoPorEquipo = new ArrayList<>(); //Objeto a retornar
        HashSet<Tarjeta> tarjetasARepartir = tresTarjetasPorCategoria();
        System.out.println("tarjetas a repartir size: " + tarjetasARepartir.size());
        eliminarTarjetasDelMazo(tarjetasARepartir);
        System.out.println("Elimine las tarjetas a repartir y el size quedo en : "+juego.getMazo().size());
        tarjetasARepartir = establecerMismaCantidadDeCartas(tarjetasARepartir);
        int tarjetasPorEquipo = tarjetasARepartir.size()/cantidadEquipos;
        int contadorVueltasPorEquipo = 0;//Cuando llegue a la cant de tarjetasPorEquipo se reinicia
        HashSet<Tarjeta> tarjetasDeUnEquipo = new HashSet<>();
        for (Tarjeta tarjEquipo: tarjetasARepartir){
            if(contadorVueltasPorEquipo<tarjetasPorEquipo) {
                tarjetasDeUnEquipo.add(tarjEquipo);
            }
            else{
                System.out.println("tarjetas De un equipo" + tarjetasDeUnEquipo.size());
                mazoPorEquipo.add(tarjetasDeUnEquipo);
                contadorVueltasPorEquipo = 0;
                tarjetasDeUnEquipo = new HashSet<>();
                tarjetasDeUnEquipo.add(tarjEquipo);
            }
            contadorVueltasPorEquipo++;
        }
        mazoPorEquipo.add(tarjetasDeUnEquipo);
        System.out.println("cantida de mazos que quedaron: "+mazoPorEquipo.size());
        return mazoPorEquipo;
    }
    Context appContext=this;
    private void mostrarPlantillas(Context appCcontext)  {
        DataManagerPlantillas.traerPlantillas(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                for (Object PlantillaObject:datos) {
                    Plantilla plantilla= (Plantilla) PlantillaObject;
                    LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                    Button button = new Button(appCcontext);
                    button.setLayoutParams(lp);
                    button.setText(plantilla.getNombre());
                    button.setBackgroundColor(999999);
                    llBotonera.addView(button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(View v) {
                            turnOnHotspot();
                            juego= new Juego(plantilla);
                            cantidadEquipos=plantilla.getCantEquipos();
                            textoCargando.setVisibility(View.VISIBLE);
                            cantidadEquiposTextView.setVisibility(View.VISIBLE);
                            nombreRed.setVisibility(View.VISIBLE);
                            claveRed.setVisibility(View.VISIBLE);
                            Intent intent= new Intent();
                            intent.setAction("crear server");
                            appContext.sendBroadcast(intent);
                        }
                    });
                }
            }
        });
    }

    private void empezarJuego(){
        unregisterReceiver(broadcastReceiver);
        Intent partida = new Intent(this, JugarActivity.class);
        startActivity(partida);
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
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
                    nombreRed.setText(currentConfig.SSID);
                    claveRed.setText(currentConfig.preSharedKey);
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