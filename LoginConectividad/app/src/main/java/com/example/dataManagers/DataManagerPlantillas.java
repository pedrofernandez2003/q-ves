package com.example.dataManagers;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.listeners.onInsertarListener;
import com.example.listeners.onTraerDatoListener;
import com.example.listeners.onTraerDatosListener;
import com.example.objetos.Categoria;
import com.example.objetos.Personaje;
import com.example.objetos.PlantillaNueva;
import com.example.objetos.Plantilla;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataManagerPlantillas extends DataManager {
    public DataManagerPlantillas() {
        super();
    }

    public static void traerPlantillas(String moderador, onTraerDatosListener listener) {
        ArrayList<Object> plantillas = new ArrayList<Object>();
        DataManager.getDb().collection("plantillas").whereEqualTo("usuario", moderador).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        HashMap<String, String> colorInfo = (HashMap<String, String>) document.getData().get("colorcolor");
                        int cantPartidas= Integer.parseInt((String) document.getData().get("cantPartidas"));
                        int cantEquipos= Integer.parseInt((String) document.getData().get("cantEquipos"));
                        ArrayList<String>categoriasBase= (ArrayList<String>) document.getData().get("categorias");
                        ArrayList<Categoria>categorias=new ArrayList<>();
                        ArrayList<String>personajesBase=(ArrayList<String>) document.getData().get("personajes");
                        ArrayList<Personaje>personajes=new ArrayList<>();
                        int contador=0;
                        for (String url:personajesBase){
                            contador++;
                            Personaje personaje=new Personaje(url,(String) document.getData().get("nombre")+"_imagen_"+contador);
                            personajes.add(personaje);
                        }
                        String mailModerador= (String) document.getData().get("moderador");
                        for(String nombreCategoria:categoriasBase){
                            DataManagerCategoria.traerCategoriasConTarjetas(nombreCategoria, new onTraerDatoListener() {
                                @Override
                                public void traer(Object dato) {
                                    if (dato!=null){
                                        categorias.add((Categoria) dato);
                                    }
                                    if(categoriasBase.size()==categorias.size()){
                                        Plantilla plantilla = new Plantilla(categorias,personajes,(String) document.getData().get("nombre"), cantPartidas,cantEquipos,mailModerador);
                                        plantillas.add(plantilla);
                                    }
                                    if(plantillas.size()==task.getResult().size()){
                                        listener.traerDatos(plantillas);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public static void insertarPlantilla(PlantillaNueva plantillaNueva, onInsertarListener listener) {
        Map<String, Object> plantillaAInsertar = new HashMap<>();
        plantillaAInsertar.put("cantEquipos", plantillaNueva.getCantidadEquipos());
        plantillaAInsertar.put("cantPartidas",String.valueOf(plantillaNueva.getUrls().size()));
        plantillaAInsertar.put("categorias", plantillaNueva.getCategoriasElegidas());
        plantillaAInsertar.put("usuario", plantillaNueva.getUsuario());
        plantillaAInsertar.put("nombre", plantillaNueva.getNombrePlantilla());
        plantillaAInsertar.put("personajes", plantillaNueva.getUrls());

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
}