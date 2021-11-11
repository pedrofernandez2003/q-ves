package com.example.dataManagers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.listeners.onEliminarListener;
import com.example.listeners.onInsertarListener;
import com.example.listeners.onModificarListener;
import com.example.listeners.onTraerDatoListener;
import com.example.listeners.onTraerDatosListener;
import com.example.objetos.CategoriaSinTarjetas;
import com.example.objetos.TarjetaSinCategoria;
import com.example.objetos.Categoria;
import com.example.objetos.Tarjeta;
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
import java.util.List;
import java.util.Map;

public  abstract class DataManagerCategoria extends DataManager {

    public DataManagerCategoria() {
        super();
    }

    public static void traerCategoriasConTarjetas(String nombre, onTraerDatoListener listener) {
        DataManager.getDb().collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Categoria categoria=new Categoria();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int cantidadTarjetas=0;
                        if (document.getData().get("nombre").equals(nombre)){
                            List<Map<String, String>> tarjetas = (List<Map<String, String>>) document.getData().get("tarjeta");
                            ArrayList<Tarjeta> tarjetasArray=new ArrayList<>();
                            if(tarjetas!=null){
                                cantidadTarjetas=tarjetas.size();
                                for (int i = 0; i< tarjetas.size();i++){
                                    Tarjeta tarjeta=new Tarjeta(tarjetas.get(i).get("contenido"),tarjetas.get(i).get("yapa"),nombre);
                                    tarjetasArray.add(tarjeta);
                                }
                            }
                            categoria = new Categoria((String) document.getData().get("nombre"), (String) document.getData().get("color"),cantidadTarjetas,tarjetasArray);
                        }
                    }
                    if(categoria.getCantidadTarjetas()==0){
                        listener.traer(null);
                    }
                    else {
                        listener.traer((Object) categoria);
                    }
                } else {
                    System.out.println("Error getting documents: "+ task.getException());
                }
            }
        });
    }

    public static void traerCategorias(onTraerDatosListener listener) {
        ArrayList<Object> categorias = new ArrayList<Object>();
        DataManager.getDb().collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ArrayList<Map<String, String>> tarjetas = (ArrayList<Map<String, String>>) document.getData().get("tarjeta");
                        if(tarjetas==null){
                            tarjetas= new ArrayList<>();
                        }
                        CategoriaSinTarjetas categoria = new CategoriaSinTarjetas((String) document.getData().get("nombre"), (String) document.getData().get("color"),tarjetas.size());
                        categorias.add(categoria);
                    }
                    listener.traerDatos(categorias);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


    public static void traerIdCategoria( String nombre, onTraerDatoListener listener) {
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



    public static void insertarCategoria(CategoriaSinTarjetas categoria, onInsertarListener listener) {
        Map<String, Object> categoriaAInsertar = new HashMap<>();
        categoriaAInsertar.put("color",categoria.getColor().toString());
        categoriaAInsertar.put("nombre",categoria.getNombre());
        categoriaAInsertar.put("tarjeta",new ArrayList<>());
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



    public static void eliminarCategoria(String nombre, onEliminarListener listener) {
        traerIdCategoria(nombre, new onTraerDatoListener() {
            @Override
            public void traer(Object dato) {
                DataManager.getDb().collection("categorias").document((String)dato)
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
                                    TarjetaSinCategoria tarjeta = new TarjetaSinCategoria((String ) atributosTarjeta.get("contenido"), (String ) atributosTarjeta.get("yapa"));
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

    public static void insertarTarjeta(TarjetaSinCategoria tarjetaAInsertar, String id, onInsertarListener listener) {
        DataManager.getDb().collection("categorias")
                .document(id)
                .update("tarjeta", FieldValue.arrayUnion(tarjetaAInsertar))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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

    public static void eliminarTarjeta(TarjetaSinCategoria tarjetaAInsertar, String id, onEliminarListener listener) {
        DataManager.getDb().collection("categorias")
                .document(id)
                .update("tarjeta", FieldValue.arrayRemove(tarjetaAInsertar))
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


    public static void modificarDatosCategoria(String id, CategoriaSinTarjetas categoriaModificada, onModificarListener listener) {
        DataManager.getDb().collection("categorias").document(id)
                .update(
                "nombre", categoriaModificada.getNombre(),
                "color",categoriaModificada.getColor().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.modificar(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.modificar(false);
                    }
                });
    }

    public static void modificarDatosTarjeta(String idCategoria, TarjetaSinCategoria tarjetaModificada,TarjetaSinCategoria tarjetaAntigua, onModificarListener listener) {
        DataManager.getDb().collection("categorias")
                .document(idCategoria)
                .update("tarjeta", FieldValue.arrayRemove(tarjetaAntigua))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DataManager.getDb().collection("categorias")
                                .document(idCategoria)
                                .update("tarjeta", FieldValue.arrayUnion(tarjetaModificada))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        listener.modificar(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        listener.modificar(false);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.modificar(false);
                    }
                });
    }
}





