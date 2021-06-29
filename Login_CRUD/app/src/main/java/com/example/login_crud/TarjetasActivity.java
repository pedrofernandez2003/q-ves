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

import com.example.Listeners.onInsertarListener;
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
        String nombre= extras.getString("Nombre");
        /*
        Button aniadirTarjeta = (Button) findViewById(R.id.aniadirTarjeta);
        aniadirTarjeta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                aniadirTarjeta(v);
            }
        });
        traerTarjetas(context);*/
    }

    public void aniadirTarjeta(View view) {

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
                DataManagerCategoria.insertarTarjeta(tarjeta, "1", new onInsertarListener() {
                    @Override
                    public void insertar(boolean insertado) {

                    }
                });
            }
        });
        AlertDialog dialog = db.show();
    }

    private void coloresUsados() {

    }

    /*private void insertarCategoria(String nombre, String color) {
        System.out.println("Inserto categoria");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> categoria = new HashMap<>();
        categoria.put("color", color);
        categoria.put("nombre", nombre);
        categoria.put("tarjeta", new ArrayList<>());
        db.collection("categorias")
                .add(categoria)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }*/

   private void traerTarjetas(Context context) {
        ArrayList<String> colores = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DataManagerCategoria.traerTarjetasCategoria("1", new onTraerDatosListener() {
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
                    button.setBackgroundColor(939393);
                    llBotonera.addView(button);
                }
            }
        });
    }
}