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
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.objetos.Usuario;
import com.example.objetos.manejoSockets.ThreadedEchoServer;
import com.example.objetos.manejoSockets.Write;
import com.example.R;
import com.example.dataManagers.DataManagerPlantillas;
import com.example.objetos.Plantilla;
import com.example.objetos.ServicioJuego;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

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
    private Button descargarImagenes;
    private WifiManager wifiManager;
    private WifiConfiguration currentConfig;
    private WifiManager.LocalOnlyHotspotReservation hotspotReservation;
    private Juego juego;
    private ImageView botonComenzarPartida;
    private ArrayList<Categoria> categorias;
    private Boolean cantidadExacta = true;
    private Boolean click=false;
    private ArrayList<Plantilla> plantillas=new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        turnOffHotspot();
        firebaseAuth = FirebaseAuth.getInstance();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("nuevo equipo");
        registerReceiver(broadcastReceiver,intentFilter);
        startService(new Intent(this, ServicioJuego.class));
        setContentView(R.layout.activity_traer_juegos);
        cantidadEquipos=0;
        textoCargando = findViewById(R.id.textoCargando);
        descargarImagenes = findViewById(R.id.descargarImagenes);
        cantidadEquiposTextView = findViewById(R.id.cantidadEquipos);
        botonComenzarPartida = findViewById(R.id.botonComenzarPartida);
        nombreRed = findViewById(R.id.nombreRed);
        claveRed = findViewById(R.id.claveRed);
        wifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        currentConfig=new WifiConfiguration();
        juego=new Juego();
        mostrarPlantillas(firebaseAuth.getCurrentUser().getEmail(), this.getApplicationContext());

        descargarImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Plantilla plantilla:plantillas){
                    for (Personaje personaje:plantilla.getPersonajes()) {
                            Picasso.with(appContext).load(personaje.getFoto()).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    descargarImagen(bitmap,personaje.getNombre());

                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    System.out.println(errorDrawable);
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                        }
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleOnBackPressed() {//que apague el hotspot y despues vaya para atras
                turnOffHotspot();
                Intent intent;
                if(Usuario.getUsuario().getRol().equals("administrador")){
                    intent=new Intent(TraerJuegosActivity.this,AdministradorActivity.class);
                }
                else{
                    intent=new Intent(TraerJuegosActivity.this,ModeradorActivity.class);
                }
               startActivity(intent);
                finish();
            }
        };

        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "nuevo equipo"://cada vez que se conecta un equipo incrementa un contador
                    int nuevaCantEquipos= Integer.parseInt((String) cantidadEquiposTextView.getText()) + 1;
                    cantidadEquiposTextView.setText(String.valueOf(nuevaCantEquipos));
                    if(GameContext.getHijos().size() >= cantidadEquipos){
                        botonComenzarPartida.setColorFilter(appContext.getResources().getColor(R.color.azul_plantilla));
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
                                    datos.add("\"turno\":"+ i);
                                    Mensaje mensaje=new Mensaje("comenzar",datos);
                                    String msg=mensaje.serializar();
                                    Write escribir = new Write();
                                    escribir.execute(msg,i);
                                }
                                empezarJuego();
                            }
                        });
                    }
                    break;
            }
        }
    };

    private HashSet<Tarjeta> tresTarjetasPorCategoria(){
        int cantidadPartidas = juego.getPartidas().size();
        categorias = juego.getPlantilla().getCategorias();
        int contador=0;
        HashSet<Tarjeta> tarjetasARepartir = new HashSet<>();
        for (Categoria categoria: categorias) { //Selecciono 3 tarjetas por cada categoria
            for (Tarjeta tarjeta: juego.getMazo()){
                if(tarjeta.getCategoria().equals(categoria.getNombre())){
                    tarjetasARepartir.add(tarjeta);
                    contador++;
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
        eliminarTarjetasDelMazo(tarjetasARepartir);
        tarjetasARepartir = establecerMismaCantidadDeCartas(tarjetasARepartir);
        int tarjetasPorEquipo = tarjetasARepartir.size()/cantidadEquipos;
        int contadorVueltasPorEquipo = 0;//Cuando llegue a la cant de tarjetasPorEquipo se reinicia
        HashSet<Tarjeta> tarjetasDeUnEquipo = new HashSet<>();
        for (Tarjeta tarjEquipo: tarjetasARepartir){
            if(contadorVueltasPorEquipo<tarjetasPorEquipo) {
                tarjetasDeUnEquipo.add(tarjEquipo);
            }
            else{
                mazoPorEquipo.add(tarjetasDeUnEquipo);
                contadorVueltasPorEquipo = 0;
                tarjetasDeUnEquipo = new HashSet<>();
                tarjetasDeUnEquipo.add(tarjEquipo);
            }
            contadorVueltasPorEquipo++;
        }
        mazoPorEquipo.add(tarjetasDeUnEquipo);
        return mazoPorEquipo;
    }

    Context appContext=this;
    private void mostrarPlantillas(String moderador,Context appCcontext)  {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int widthPlantilla = (width*4)/5;
        int heightPlantilla = widthPlantilla/4;
        DataManagerPlantillas.traerPlantillas(moderador,new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                if(datos.size()>0) {
                    for (Object PlantillaObject : datos) {
                        Plantilla plantilla = (Plantilla) PlantillaObject;
                        plantillas.add(plantilla);
                        LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                        llBotonera.setGravity(Gravity.CENTER_HORIZONTAL);

                        CardView cardView = new CardView(appCcontext);
                        LayoutParams params= new LayoutParams(widthPlantilla, heightPlantilla);
                        params.setMargins(0,25,0,0);
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.azul_plantilla));
                        cardView.setRadius(40);
                        params.gravity = Gravity.CENTER;
                        cardView.setLayoutParams(params);

                        ConstraintLayout constraintLayout = new ConstraintLayout(appCcontext);
                        params = new LayoutParams(widthPlantilla, heightPlantilla);
                        constraintLayout.setLayoutParams(params);
                        constraintLayout.setId(ViewCompat.generateViewId());
                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.azul_plantilla));
                        cardView.addView(constraintLayout);


                        TextView texto = new TextView(appCcontext);
                        params = new LayoutParams((widthPlantilla*7)/10, ViewGroup.LayoutParams.MATCH_PARENT);
                        texto.setLayoutParams(params);
                        texto.setText(plantilla.getNombre());
                        params.gravity = Gravity.CENTER;
                        texto.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        texto.setTextSize((widthPlantilla)/25);
                        texto.setTextColor(getResources().getColor(R.color.white));
                        texto.setTypeface(ResourcesCompat.getFont(appCcontext, R.font.poertsen_one_regular));
                        texto.setGravity(Gravity.CENTER);
                        texto.setId(ViewCompat.generateViewId());


                        ImageView persona = new ImageView(appCcontext);
                        params = new LayoutParams(width/12, width/12);
                        params.setMargins(0, heightPlantilla/20, 0, heightPlantilla/40);
                        persona.setLayoutParams(params);
                        persona.setColorFilter(getResources().getColor(R.color.white));
                        persona.setId(ViewCompat.generateViewId());
                        persona.setImageDrawable(getResources().getDrawable(R.drawable.ic_person));


                        ImageView ruleta = new ImageView(appCcontext);
                        params = new LayoutParams(width/12, width/12);
                        params.setMargins(0, heightPlantilla/40, 0, heightPlantilla/20);
                        ruleta.setLayoutParams(params);
                        ruleta.setColorFilter(getResources().getColor(R.color.white));
                        ruleta.setId(ViewCompat.generateViewId());
                        ruleta.setImageDrawable(getResources().getDrawable(R.drawable.ic_roulette));

                        TextView cantPersonas = new TextView(appCcontext);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            cantPersonas.setTypeface(getResources().getFont(R.font.poertsen_one_regular));
                        }
                        cantPersonas.setTextColor(getResources().getColor(R.color.white));
                        cantPersonas.setText(plantilla.getCantEquipos()+"");
                        cantPersonas.setTextSize(width/50);

                        cantPersonas.setId(ViewCompat.generateViewId());
                        TextView cantRondas = new TextView(appCcontext);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            cantRondas.setTypeface(getResources().getFont(R.font.poertsen_one_regular));
                        }
                        cantRondas.setTextColor(getResources().getColor(R.color.white));
                        cantRondas.setText(plantilla.getCantPartidas()+"");
                        cantRondas.setTextSize(width/50);

                        cantRondas.setId(ViewCompat.generateViewId());
                        constraintLayout.addView(texto);
                        constraintLayout.addView(cantPersonas);
                        constraintLayout.addView(cantRondas);
                        constraintLayout.addView(ruleta);
                        constraintLayout.addView(persona);
                        ConstraintSet set = new ConstraintSet();

                        set.clone(constraintLayout);
                        set.connect(texto.getId(), ConstraintSet.RIGHT, persona.getId(), ConstraintSet.LEFT);
                        set.connect(texto.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                        set.connect(texto.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
                        set.connect(texto.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT);

                        set.connect(persona.getId(), ConstraintSet.RIGHT, cantPersonas.getId(), ConstraintSet.LEFT);
                        set.connect(persona.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                        set.connect(persona.getId(), ConstraintSet.BOTTOM, ruleta.getId(), ConstraintSet.TOP);
                        set.connect(persona.getId(), ConstraintSet.LEFT, texto.getId(), ConstraintSet.RIGHT);


                        set.connect(ruleta.getId(), ConstraintSet.TOP, persona.getId(), ConstraintSet.BOTTOM);
                        set.connect(ruleta.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
                        set.connect(ruleta.getId(), ConstraintSet.RIGHT, cantRondas.getId(), ConstraintSet.LEFT);
                        set.connect(ruleta.getId(), ConstraintSet.LEFT, texto.getId(), ConstraintSet.RIGHT);

                        set.connect(cantPersonas.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                        set.connect(cantPersonas.getId(), ConstraintSet.BOTTOM, cantRondas.getId(), ConstraintSet.TOP);
                        set.connect(cantPersonas.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT);
                        set.connect(cantPersonas.getId(), ConstraintSet.LEFT, persona.getId(), ConstraintSet.RIGHT);

                        set.connect(cantRondas.getId(), ConstraintSet.TOP, cantPersonas.getId(), ConstraintSet.BOTTOM);
                        set.connect(cantRondas.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
                        set.connect(cantRondas.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT);
                        set.connect(cantRondas.getId(), ConstraintSet.LEFT, ruleta.getId(), ConstraintSet.RIGHT);

                        set.applyTo(constraintLayout);

                        llBotonera.addView(cardView);
                        cardView.setOnClickListener(new View.OnClickListener() {
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
                                            public void onClick (View view){//se abre el hotspot, se crea el juego y el server
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
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("personajes", Context.MODE_PRIVATE);
        // creamos el directorio personajes
        File mypath=new File(directory,nombre+".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
//        stopService(new Intent(this, ServicioJuego.class));
        turnOffHotspot();
        super.onDestroy();
    }
}