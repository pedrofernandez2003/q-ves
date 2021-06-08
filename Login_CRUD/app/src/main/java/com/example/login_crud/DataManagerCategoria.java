package com.example.login_crud;

import android.graphics.ColorSpace;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.Objetos.Categoria;
import com.example.Objetos.Color;
import com.example.Objetos.onCollectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public  abstract class DataManagerCategoria {

    /*public DataManagerCategoria() {
        super();
    }*/

    public static void traerCategorias(onCollectionListener listener) {
        ArrayList<Categoria> categorias=new ArrayList<Categoria>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                if (task.isSuccessful()) { //ver como guardamos el tema del rgb, si como esta clase colorspace.rgc o como un hexadecimal
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Color color = new Color((String) document.getData().get("nombreColor"), (ColorSpace.Rgb) document.getData().get("rgbColor"));
                        Categoria categoria=new Categoria((String) document.getData().get("nombre"),color);
                        categorias.add(categoria);
                    }
                    listener.onComplete(categorias);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }});

    }

}




//conseguir todas las categorias, conseguir una categoria segun color?, conseguir categoria segun nombre

