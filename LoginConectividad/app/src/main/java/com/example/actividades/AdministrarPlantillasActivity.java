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
import android.widget.Toast;

import com.example.dataManagers.DataManager;
import com.example.listeners.onEliminarListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class AdministrarPlantillasActivity extends AppCompatActivity {
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
    private ImageView agregarPlantilla;
    private ArrayList<Plantilla> plantillas=new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    
      
        setContentView(R.layout.activity_administrar_plantillas);
        cantidadEquipos=0;
        agregarPlantilla = findViewById(R.id.agregarPlantilla);
       
        mostrarPlantillas(firebaseAuth.getCurrentUser().getEmail(), this.getApplicationContext());

        agregarPlantilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministrarPlantillasActivity.this,CrearJuegoActivity.class);
                startActivity(intent);
            }
        });


//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void handleOnBackPressed() {//que apague el hotspot y despues vaya para atras
////                turnOffHotspot();
//                Intent intent=new Intent(appContext,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        };

//        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    
    private void mostrarPlantillas(String moderador,Context appCcontext) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int widthPlantilla = (width * 4) / 5;
        int heightPlantilla = widthPlantilla / 4;
        DataManagerPlantillas.traerPlantillas(moderador, new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                System.out.println("datos" + datos);
                if (datos.size() > 0) {
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
                        params = new LayoutParams(width/10, width/10);
                        params.setMargins(0, heightPlantilla/20, 0, heightPlantilla/40);
                        persona.setLayoutParams(params);
                        persona.setColorFilter(getResources().getColor(R.color.white));
                        persona.setId(ViewCompat.generateViewId());
                        persona.setImageDrawable(getResources().getDrawable(R.drawable.ic_person));


                        ImageView ruleta = new ImageView(appCcontext);
                        params = new LayoutParams(width/10, width/10);
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
                                LayoutInflater inflater = LayoutInflater.from(AdministrarPlantillasActivity.this);

                                View dialog_layout = inflater.inflate(R.layout.confirmacion_accion, null);
                                TextView seleccionado = dialog_layout.findViewById(R.id.confirmarSelección);
                                seleccionado.setText("¿Desea eliminar la plantilla?");
                                AlertDialog.Builder db = new AlertDialog.Builder(AdministrarPlantillasActivity.this);
                                db.setView(dialog_layout);
                                db.setTitle("Eliminar plantilla");
                                db.setPositiveButton("Sí", null);
                                db.setNegativeButton("No", null);
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
                                                DataManagerPlantillas.eliminarPlantilla(plantilla.getNombre(), new onEliminarListener() {
                                                    @Override
                                                    public void eliminar(boolean eliminado) {
                                                        Toast.makeText(getApplicationContext(), "Plantilla eliminada", Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(AdministrarPlantillasActivity.this, AdministrarPlantillasActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
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
}