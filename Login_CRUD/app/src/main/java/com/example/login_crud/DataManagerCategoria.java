package com.example.login_crud;

import android.util.Log;

import androidx.annotation.NonNull;

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
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        System.out.println(document.getId() + " => " + document.getData());
                        Categoria categoria=new Categoria((String) document.getData().get("nombre"), (String) document.getData().get("color"));
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

