package com.example.login_crud;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.Listeners.onInsertarListener;
import com.example.Listeners.onTraerDatoListener;
import com.example.Listeners.onTraerDatosListener;
import com.example.Objetos.Tarjeta;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TarjetasActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjetas);
        Bundle extras=getIntent().getExtras();
        String color= extras.getString("Color");
        String nombreCategoria= extras.getString("Nombre");
        System.out.println(color+nombreCategoria);

        Button aniadirTarjeta = (Button) findViewById(R.id.aniadirTarjeta);
        aniadirTarjeta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                aniadirTarjeta(v,nombreCategoria);
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
        db.setTitle("Nueva categoria");
        db.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String contenidoTarjeta = contenido.getText().toString();
                String yapaTarjeta = yapa.getText().toString();
                Tarjeta tarjeta= new Tarjeta(contenidoTarjeta,yapaTarjeta);
                DataManagerCategoria.insertarTarjeta(tarjeta,nombreCategoria, new onInsertarListener() {
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
        AlertDialog dialog = db.show();
    }



   private void traerTarjetas(Context context,String nombreCategoria,String color) {
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
                            button.setBackgroundColor(939393); //aca iria el string color pero por ahora no hay nada
                            llBotonera.addView(button);
                        }
                    }
                });
            }
        });

    }
}