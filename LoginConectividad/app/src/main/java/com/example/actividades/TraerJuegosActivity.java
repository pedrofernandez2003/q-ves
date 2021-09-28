package com.example.actividades;
import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.objetos.manejoSockets.Write;
import com.example.R;
import com.example.dataManagers.DataManagerPlantillas;
import com.example.objetos.Plantilla;
import com.example.objetos.ServicioJuego;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
    private ArrayList<Categoria> categorias;
    private Boolean cantidadExacta = true;
    private Boolean click=false;

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
                            LayoutInflater inflater = LayoutInflater.from(TraerJuegosActivity.this);

                            View dialog_layout = inflater.inflate(R.layout.confirmacion_accion, null);
                            TextView seleccionado = dialog_layout.findViewById(R.id.confirmarSelección);

                            AlertDialog.Builder db = new AlertDialog.Builder(TraerJuegosActivity.this);
                            db.setView(dialog_layout);
                            db.setTitle("Confirmar selección");
                            db.setPositiveButton("Sí", null);
                            db.setNegativeButton("Deshacer", null);
                            final AlertDialog a = db.create();

                            a.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    Button si = a.getButton(AlertDialog.BUTTON_POSITIVE);
                                    if(click){
                                       si.setEnabled(false);
                                    }
                                    Button no = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            a.dismiss();
                                        }
                                    });
                                    si.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick (View view){
                                                turnOnHotspot();
                                                juego = new Juego(plantilla);
                                                cantidadEquipos = plantilla.getCantEquipos();
                                                textoCargando.setVisibility(View.VISIBLE);
                                                cantidadEquiposTextView.setVisibility(View.VISIBLE);
                                                nombreRed.setVisibility(View.VISIBLE);
                                                claveRed.setVisibility(View.VISIBLE);
                                                Intent intent = new Intent();
                                                intent.setAction("crear server");
                                                appContext.sendBroadcast(intent);
                                                a.dismiss();
                                                click = true;
                                            }
                                    });
                                }
                            });
                            a.show();

                        }
                    });
                    for (Personaje personaje:plantilla.getPersonajes()) {
                        Picasso.with(appCcontext).load(personaje.getFoto()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                descargarImagen(bitmap,personaje.getNombre());

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                System.out.println("fallo");
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }
                }
            }
        });
    }

    private void empezarJuego(){
        unregisterReceiver(broadcastReceiver);
        Intent partida = new Intent(this, JugarActivity.class);
        startActivity(partida);
    }

    private String descargarImagen(Bitmap bitmapImage, String nombre){
        System.out.println("descargue una imagen");
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/personajes
        File directory = cw.getDir("personajes", Context.MODE_PRIVATE);
        // Create personajes
        File mypath=new File(directory,nombre+".png");
        System.out.println(mypath.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOffHotspot() {
        if (hotspotReservation != null) {
            hotspotReservation.close();
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    protected void onStop() {
//        turnOffHotspot();
//        stopService(new Intent(this, ServicioJuego.class));
//        super.onStop();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    protected void onDestroy() {
//        turnOffHotspot();
//        stopService(new Intent(this, ServicioJuego.class));
//        super.onDestroy();
//    }
}