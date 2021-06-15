package com.example.login_crud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.Listeners.onEliminarListener;
import com.example.Listeners.onInsertarListener;
import com.example.Listeners.onModificarListener;
import com.example.Listeners.onTraerDatoListener;
import com.example.Listeners.onTraerDatosListener;
import com.example.Objetos.Categoria;
import com.example.Objetos.Color;
import com.example.Objetos.Tarjeta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public  abstract class DataManagerCategoria extends DataManager {

    public DataManagerCategoria() {
        super();
    }

    public static void traerCategorias(onTraerDatosListener listener) {
        ArrayList<Object> categorias = new ArrayList<Object>();
        DataManager.getDb().collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        HashMap<String, String> colorInfo = (HashMap<String, String>) document.getData().get("color");
//                        Color color = new Color((String) colorInfo.get("nombre"), (String) colorInfo.get("codigo"));
//                        System.out.println("codigo de amarillo es: "+ String.valueOf(string).getCodigo());
                        Categoria categoria = new Categoria((String) document.getData().get("nombre"), (String) document.getData().get("color"));
                        categorias.add(categoria);
                    }
                    listener.traerDatos(categorias);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public static void traerIdCategoria(String nombre, onTraerDatoListener listener) {
        DataManager.getDb().collection("categorias").whereEqualTo("nombre", nombre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                String id = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                    }
                    listener.traer((Object) id);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public static void insertarCategoria(Categoria categoriaAInsertar, onInsertarListener listener) {
        DataManager.getDb().collection("categorias")
                .add(categoriaAInsertar)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        listener.insertar(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.insertar(false);

                    }
                });
    }

    public static void eliminarCategoria(String id, onEliminarListener listener) {
        DataManager.getDb().collection("categorias").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.eliminar(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.eliminar(false);
                    }
                });
    }

    public static void traerTarjetasCategoria(String id, onTraerDatosListener listener) {
        DataManager.getDb().collection("categorias").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<Object> tarjetas = new ArrayList<Object>();
                        Map<String, Object> categoria = document.getData();
                        for (Map.Entry<String, Object> atributosCategoria : categoria.entrySet()) {
                            if (atributosCategoria.getKey().equals("tarjeta")) {
                                for (HashMap<String, Object> atributosTarjeta : (ArrayList<HashMap<String, Object>>) atributosCategoria.getValue()) {
                                    Tarjeta tarjeta = new Tarjeta((Color) atributosTarjeta.get("contenido"), (Color) atributosTarjeta.get("yapa"));
                                    tarjetas.add(tarjeta);
                                }
                            }
                        }
                        listener.traerDatos(tarjetas);
                    }
                }
            }
        });
    }

    public static void insertarTarjeta(Tarjeta tarjetaAInsertar, String id, onInsertarListener listener) {
        DataManager.getDb().collection("categorias")
                .document(id)
                .update("tarjeta", FieldValue.arrayUnion(tarjetaAInsertar))
                .addOnSuccessListener((OnSuccessListener) o -> listener.insertar(true))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.insertar(false);
                    }
                });
    }

    public static void eliminarTarjeta(Tarjeta tarjetaAInsertar, String id, onEliminarListener listener) {
        DataManager.getDb().collection("categorias")
                .document(id)
                .update("tarjeta", FieldValue.arrayRemove(tarjetaAInsertar))
                .addOnSuccessListener((OnSuccessListener) o -> listener.eliminar(true))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.eliminar(false);
                    }
                });
    }


    public static void modificarDatosCategoria(Categoria categoriaModificada, onModificarListener listener) {
        DataManagerCategoria.traerIdCategoria(categoriaModificada.getNombre(), new onTraerDatoListener() {
            @Override
            public void traer(Object id) {
                DataManager.getDb().collection("users").document((String) id).update(
                        "nombre", categoriaModificada.getNombre(),
                        "color", categoriaModificada.getColor()
                ).addOnSuccessListener((OnSuccessListener) o -> listener.modificar(true))
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                listener.modificar(false);
                            }
                        });
            }
        });

    }
}




