package com.example.login_crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TarjetasYCategoriasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjetas_y_categorias);
        traerDatos();

        }


    private void traerDatos()  {
        HashMap<Integer, Object> categorias = new HashMap<>();
        HashMap<String, Object> categoria = new HashMap<>();
        categoria.put("nombre","calle");
        categoria.put("color","rosa");
        categoria.put("cantidadTarjetas",4);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        categorias.put(0,categoria);
        Context context = this.getApplicationContext();
        db.collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int indice = 0;
                        String TAG = "";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                                //Creamos las propiedades de layout que tendrán los botones.
                                //Son LinearLayout.LayoutParams porque los botones van a estar en un LinearLayout.
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                System.out.println(document.getId() + " => " + document.getData());
                                List<Map<String, String>> tarjetas = (List<Map<String, String>>) document.getData().get("tarjeta");
                                Button button = new Button(context);
                                //Asignamos propiedades de layout al boton
                                button.setLayoutParams(lp);
                                //Asignamos Texto al botón
                                button.setText((String) document.getData().get("nombre") + " " + tarjetas.size());
                                //Añadimos el botón a la botonera
                                button.setBackgroundColor(939393);
                                llBotonera.addView(button);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    }