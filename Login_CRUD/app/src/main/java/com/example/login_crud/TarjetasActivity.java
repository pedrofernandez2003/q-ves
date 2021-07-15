package com.example.login_crud;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.Listeners.onInsertarListener;
import com.example.Listeners.onModificarListener;
import com.example.Listeners.onTraerDatoListener;
import com.example.Listeners.onTraerDatosListener;
import com.example.Objetos.Tarjeta;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TarjetasActivity extends FragmentActivity {

    public boolean ModoModificar=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjetas);
        Bundle extras=getIntent().getExtras();
        int color= extras.getInt("Color");
        String nombreCategoria= extras.getString("Nombre");
        System.out.println(color+nombreCategoria);

        Button aniadirTarjeta = (Button) findViewById(R.id.aniadirTarjeta);
        aniadirTarjeta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                aniadirTarjeta(v,nombreCategoria);
                traerTarjetas(context,nombreCategoria,color);
            }
        });
        Button modificarTarjeta= (Button) findViewById(R.id.modificarTarjeta);
        modificarTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModoModificar=!ModoModificar;

            }
        });
        traerTarjetas(context,nombreCategoria,color);
    }

    public void aniadirTarjeta(View view,String nombreCategoria) {

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
                        Tarjeta tarjeta= new Tarjeta(contenidoTarjeta,yapaTarjeta);
                        insertarTarjeta(tarjeta,nombreCategoria);
                        a.dismiss();
                        
                    }
                });
            }
        });
        a.show();

    }

    private void insertarTarjeta(Tarjeta tarjeta, String nombreCategoria){
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

    private void modificarTarjeta(Tarjeta tarjetaVieja,Tarjeta tarjetaActualizada,String nombreCategoria){
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



   private void traerTarjetas(Context context, String nombreCategoria, int color) {
        ArrayList<String> colores = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DataManagerCategoria.traerIdCategoria(nombreCategoria, new onTraerDatoListener() {
            @Override
            public void traer(Object dato) {
                DataManagerCategoria.traerTarjetasCategoria((String) dato, new onTraerDatosListener() {
                    @Override
                    public void traerDatos(ArrayList<Object> datos) {
                        for (Object tarjetaObject:datos) {
                            Tarjeta tarjeta= (Tarjeta) tarjetaObject;
                            System.out.println(tarjeta.getContenido()+" "+tarjeta.getYapa());
                            LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera2);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                            Button button = new Button(context);
                            button.setLayoutParams(lp);
                            button.setText(tarjeta.getContenido() + " " + tarjeta.getYapa());
                            button.setBackgroundColor(color);
                            llBotonera.addView(button);
                            button.setOnClickListener(new View.OnClickListener() {
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
                                                        Tarjeta tarjetaNueva= new Tarjeta(contenidoTarjeta,yapaTarjeta);
                                                        modificarTarjeta(tarjeta,tarjetaNueva,nombreCategoria);

                                                        llBotonera.removeAllViews();
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

/*IDEA, PARA CATEGORIA ACTIVITY
 QUE CUANDO TOQUES EL BOTON CAMBIE UN BOOLEANO GLOBAL
 Y EN EL ONCLICK SI ES TRUE SEA PARA MODIFICAR LA CATEGORIA Y SI ES FALSE ENTRA A LAS TARJETAS*/