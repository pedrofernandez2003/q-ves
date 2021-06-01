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
        Button aniadirTarjeta = (Button) findViewById(R.id.aniadirTarjeta);
        aniadirTarjeta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                aniadirTarjeta(v);
            }
        });
        traerDatos(context);
    }

    public void aniadirTarjeta(View view) {

        LayoutInflater inflater = LayoutInflater.from(TarjetasActivity.this);
        View dialog_layout = inflater.inflate(R.layout.activity_aniadir_categoria,null);
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setView(dialog_layout);
        EditText nombre = dialog_layout.findViewById(R.id.nombreCategoria);;
        EditText color = dialog_layout.findViewById(R.id.colorCategoria);
        db.setTitle("Nueva categoria");
        db.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String nombreCategoria = nombre.getText().toString();
                String colorCategoria = color.getText().toString();
                System.out.println("nombre: "+ nombreCategoria);
                insertarCategoria(nombreCategoria,colorCategoria);

            }
        });
        AlertDialog dialog = db.show();
    }

    private void coloresUsados(){

    }
    private void insertarCategoria(String nombre, String color){
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
    }