package com.example.login_crud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.Objetos.onCollectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public  abstract class DataManagerCategoria extends DataManager {

    public DataManagerCategoria() {
        super();
    }

    public static void traerCategorias(onCollectionListener listener) {
        Object objeto = null;
        DataManagerCategoria.getDb().collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        System.out.println(document.getId() + " => " + document.getData());
                        listener.onComplete(objeto);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }});


    }
    static void main(String args[]){
        DataManagerCategoria.traerCategorias(new onCollectionListener() {
            @Override
            public void onComplete(Object objeto) {

            }
        });
    }

}




//conseguir todas las categorias, conseguir una categoria segun color?, conseguir categoria segun nombre

