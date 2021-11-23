package com.example.actividades;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;

import com.example.R;

import android.widget.TextView;
import android.widget.Toast;

import com.example.listeners.onEliminarListener;
import com.example.listeners.onInsertarListener;
import com.example.listeners.onModificarListener;
import com.example.listeners.onTraerDatoListener;
import com.example.listeners.onTraerDatosListener;
import com.example.objetos.TarjetaSinCategoria;
import com.example.dataManagers.DataManagerCategoria;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class TarjetasActivity extends AppCompatActivity {

    public boolean ModoModificar=false;
    public  String nombreCategoria;
    public int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjetas);

        Bundle extras=getIntent().getExtras();
        color= extras.getInt("Color");
        nombreCategoria= extras.getString("Nombre");

        CardView aniadirTarjeta = (CardView) findViewById(R.id.aniadirTarjeta);
        aniadirTarjeta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                aniadirTarjeta(context,v,nombreCategoria);

            }
        });
        CardView modificarTarjeta= (CardView) findViewById(R.id.modificarTarjeta);
        TextView textoModificarTarjeta=findViewById(R.id.textoModificarTarjeta);
        modificarTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModoModificar=!ModoModificar;
                if (ModoModificar){
                    textoModificarTarjeta.setText("Salir del modo editable");
                }
                else{
                    textoModificarTarjeta.setText("Editar tarjeta");
                }

            }
        });
        traerTarjetas(context,nombreCategoria,color);
    }

    public CardView crearTarjeta(int width, int height, int margin, int color, String categoria, String contenido, String yapaContenido){

        // Crear la base
        CardView carta = new CardView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.setMargins(margin,margin,margin,margin);
        carta.setLayoutParams(params);
        carta.setBackgroundColor(-1644568);

        // Crear el constraint layout
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        params = new FrameLayout.LayoutParams(width, height);
        constraintLayout.setLayoutParams(params);
        constraintLayout.setId(ViewCompat.generateViewId());
        carta.addView(constraintLayout);

        // Crear el borde de arriba
        CardView bordeTop = new CardView(this);
        params = new FrameLayout.LayoutParams(width, height/8);
        bordeTop.setLayoutParams(params);
        bordeTop.setBackgroundColor(color);
        bordeTop.setId(ViewCompat.generateViewId());
        constraintLayout.addView(bordeTop);


        // Crear el borde de abajo
        CardView bordeBot = new CardView(this);
        params = new FrameLayout.LayoutParams(width, (height*3)/50);
        bordeBot.setLayoutParams(params);
        bordeBot.setBackgroundColor(color);
        bordeBot.setId(ViewCompat.generateViewId());
        constraintLayout.addView(bordeBot);

        //Crear el textview con la categoria
        TextView textoCategoria = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textoCategoria.setLayoutParams(params);
        textoCategoria.setText(categoria);
        textoCategoria.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/10);
        textoCategoria.setTypeface(ResourcesCompat.getFont(this, R.font.poertsen_one_regular));
        textoCategoria.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoCategoria);

        //Crear el textview para el contenido
        TextView textoContenido = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        textoContenido.setLayoutParams(params);
        textoContenido.setText(contenido);
        textoContenido.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/15));
        textoContenido.setGravity(Gravity.CENTER);
        textoContenido.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoContenido);

        //Crear la yapa
        TextView yapa = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        yapa.setLayoutParams(params);
        yapa.setText(yapaContenido);
        yapa.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/20));
        yapa.setGravity(Gravity.CENTER);
        yapa.setId(ViewCompat.generateViewId());
        constraintLayout.addView(yapa);

        //Constraints
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        set.connect(bordeTop.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 0);
        set.connect(bordeBot.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(textoCategoria.getId(), ConstraintSet.TOP, bordeTop.getId(), ConstraintSet.BOTTOM);
        set.connect(textoCategoria.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoCategoria.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoContenido.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.TOP, textoCategoria.getId(), ConstraintSet.BOTTOM,height/50);
        set.connect(yapa.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(yapa.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(yapa.getId(), ConstraintSet.BOTTOM, bordeBot.getId(), ConstraintSet.TOP);
        set.applyTo(constraintLayout);

        return carta;

    }


    public void aniadirTarjeta(Context context, View view,String nombreCategoria) {

        LayoutInflater inflater = LayoutInflater.from(TarjetasActivity.this);
        View dialog_layout = inflater.inflate(R.layout.activity_aniadir_tarjeta, null);
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setView(dialog_layout);
        EditText contenido = dialog_layout.findViewById(R.id.contenido);
        EditText yapa = dialog_layout.findViewById(R.id.yapa);
        db.setTitle("Nueva Tarjeta");
        db.setPositiveButton("Añadir", null);
        final AlertDialog a = db.create();

        a.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = a.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String contenidoTarjeta = contenido.getText().toString();
                        String yapaTarjeta = yapa.getText().toString();
                        TarjetaSinCategoria tarjeta= new TarjetaSinCategoria(contenidoTarjeta,yapaTarjeta);
                        insertarTarjeta(tarjeta,nombreCategoria);

                        GridLayout grid = findViewById(R.id.gridBotonera2);
                        grid.removeAllViews();
                        traerTarjetas(context,nombreCategoria,color);

                        a.dismiss();

                    }
                });
            }
        });
        a.show();

    }

    private void insertarTarjeta(TarjetaSinCategoria tarjeta, String nombreCategoria){
        DataManagerCategoria.traerIdCategoria(nombreCategoria, new onTraerDatoListener() {
            @Override
            public void traer(Object dato) {
                DataManagerCategoria.insertarTarjeta(tarjeta,(String) dato, new onInsertarListener() {
                    @Override
                    public void insertar(boolean insertado) {
                        if (insertado){
                            Toast.makeText(TarjetasActivity.this, "Insertado!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(TarjetasActivity.this, "Error al insertar :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void modificarTarjeta(TarjetaSinCategoria tarjetaVieja,TarjetaSinCategoria tarjetaActualizada,String nombreCategoria){
        DataManagerCategoria.traerIdCategoria(nombreCategoria, new onTraerDatoListener() {
            @Override
            public void traer(Object dato) {
                DataManagerCategoria.modificarDatosTarjeta((String) dato, tarjetaActualizada, tarjetaVieja, new onModificarListener() {
                    @Override
                    public void modificar(boolean modificado) {
                        if (modificado){
                            Toast.makeText(TarjetasActivity.this, "Modifico Correctamente", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(TarjetasActivity.this, "Problemas en la base, intentar de vuelta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CategoriasActivity.class));
    }

    private void traerTarjetas(Context context, String nombreCategoria, int color) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int widthCarta = (width*9)/20;
        int heightCarta = (widthCarta*7)/5;
        int marginCarta = width/50;
        GridLayout gridCartas = findViewById(R.id.gridBotonera2);
        DataManagerCategoria.traerIdCategoria(nombreCategoria, new onTraerDatoListener() {
            @Override
            public void traer(Object dato) {
                DataManagerCategoria.traerTarjetasCategoria((String) dato, new onTraerDatosListener() {
                    @Override
                    public void traerDatos(ArrayList<Object> datos) {
                        for (Object tarjetaObject:datos) {
                            TarjetaSinCategoria tarjeta= (TarjetaSinCategoria) tarjetaObject;
                            CardView carta = crearTarjeta(widthCarta, heightCarta, marginCarta, color, nombreCategoria, tarjeta.getContenido(), tarjeta.getYapa());
                            gridCartas.addView(carta);

                            carta.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ModoModificar){

                                        LayoutInflater inflater = LayoutInflater.from(TarjetasActivity.this);
                                        View dialog_layout = inflater.inflate(R.layout.activity_aniadir_tarjeta, null);
                                        AlertDialog.Builder db = new AlertDialog.Builder(TarjetasActivity.this);
                                        db.setView(dialog_layout);

                                        EditText contenido = dialog_layout.findViewById(R.id.contenido);
                                        contenido.setText(tarjeta.getContenido());
                                        EditText yapa = dialog_layout.findViewById(R.id.yapa);
                                        yapa.setText(tarjeta.getYapa());

                                        db.setTitle("Nueva Tarjeta");
                                        db.setPositiveButton("Modificar", null);
                                        db.setNegativeButton("Eliminar", null);

                                        final AlertDialog a = db.create();
                                        a.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                Button modificarTarjeta = a.getButton(AlertDialog.BUTTON_POSITIVE);
                                                Button eliminarTarjeta = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                                                eliminarTarjeta.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        DataManagerCategoria.traerIdCategoria(nombreCategoria, new onTraerDatoListener() {
                                                            @Override
                                                            public void traer(Object dato) {
                                                                DataManagerCategoria.eliminarTarjeta(tarjeta, (String) dato, new onEliminarListener() {
                                                                    @Override
                                                                    public void eliminar(boolean eliminado) {
                                                                        if(eliminado) {
                                                                            gridCartas.removeAllViews();
                                                                            traerTarjetas(context,nombreCategoria,color);
                                                                            a.dismiss();
                                                                        }
                                                                        else{
                                                                            Toast.makeText(TarjetasActivity.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                                modificarTarjeta.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        String contenidoTarjeta = contenido.getText().toString();
                                                        String yapaTarjeta = yapa.getText().toString();
                                                        TarjetaSinCategoria tarjetaNueva= new TarjetaSinCategoria(contenidoTarjeta,yapaTarjeta);
                                                        modificarTarjeta(tarjeta,tarjetaNueva,nombreCategoria);

                                                        gridCartas.removeAllViews();
                                                        traerTarjetas(context,nombreCategoria,color);

                                                        a.dismiss();
                                                    }
                                                });
                                            }
                                        });
                                        a.show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}