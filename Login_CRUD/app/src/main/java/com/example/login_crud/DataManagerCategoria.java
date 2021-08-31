package com.example.login_crud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.Listeners.onEliminarListener;
import com.example.Listeners.onInsertarListener;
import com.example.Listeners.onModificarListener;
import com.example.Listeners.onTraerDatoListener;
import com.example.Listeners.onTraerDatosListener;
import com.example.Listeners.onTraerPersonajesListener;
import com.example.Objetos.Categoria;
import com.example.Objetos.Color;
import com.example.Objetos.Plantilla;
import com.example.Objetos.Tarjeta;
import com.example.simpleimagegallery.utils.pictureFacer;
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
                        Categoria categoria = new Categoria((String) document.getData().get("nombre"), (String) document.getData().get("color"),tarjetas.size());
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
    public static void traerCategoria(String nombre, onTraerDatoListener listener) {
        DataManager.getDb().collection("categorias").whereEqualTo("nombre", nombre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                String id = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<Map<String, String>> tarjetas = (List<Map<String, String>>) document.getData().get("tarjeta");
                        Categoria categoria = new Categoria((String) document.getData().get("nombre"), (String) document.getData().get("color"),tarjetas.size());
                        listener.traer((Object) categoria);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


    public static void insertarCategoria(Categoria categoria, onInsertarListener listener) {
        Map<String, Object> categoriaAInsertar = new HashMap<>();
        categoriaAInsertar.put("color",categoria.getColor().toString());
        categoriaAInsertar.put("nombre",categoria.getNombre());
        categoriaAInsertar.put("tarjetas",new ArrayList<>());
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

    public static void insertarPlantilla(Plantilla plantilla, onInsertarListener listener) {
        Map<String, Object> plantillaAInsertar = new HashMap<>();
        plantillaAInsertar.put("cantEquipos",plantilla.getCantidadEquipos());
        plantillaAInsertar.put("cantPartidas",String.valueOf(plantilla.getUrls().size()));
        plantillaAInsertar.put("categorias",plantilla.getCategoriasElegidas());
        plantillaAInsertar.put("usuario", plantilla.getUsuario());
        plantillaAInsertar.put("nombre",plantilla.getNombrePlantilla());
        plantillaAInsertar.put("personajes",plantilla.getUrls());

        DataManager.getDb().collection("plantillas")
                .add(plantillaAInsertar)
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
                                    Tarjeta tarjeta = new Tarjeta((String ) atributosTarjeta.get("contenido"), (String ) atributosTarjeta.get("yapa"));
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

    public static void eliminarTarjeta(Tarjeta tarjetaAInsertar, String id, onEliminarListener listener) {
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


    public static void modificarDatosCategoria(String id, Categoria categoriaModificada, onModificarListener listener) {
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

    public static void modificarDatosTarjeta(String idCategoria, Tarjeta tarjetaModificada,Tarjeta tarjetaAntigua, onModificarListener listener) {
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





