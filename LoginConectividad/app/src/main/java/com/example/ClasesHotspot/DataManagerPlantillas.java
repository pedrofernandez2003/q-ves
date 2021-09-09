package com.example.hotspot;

import android.app.Person;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.hotspot.onTraerDatoListener;
import com.example.hotspot.onTraerDatosListener;
import com.example.hotspot.Juego;
import com.example.hotspot.Plantilla;
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
public class DataManagerPlantillas extends DataManager {
    public DataManagerPlantillas() {
        super();
    }

    public static void traerPlantillas(onTraerDatosListener listener) {
        ArrayList<Object> plantillas = new ArrayList<Object>();
        DataManager.getDb().collection("plantillas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        for (String url:personajesBase){
                            Personaje personaje=new Personaje("",url);
                            personajes.add(personaje);
                        }
                        String mailModerador= (String) document.getData().get("moderador");
                        for(String nombreCategoria:categoriasBase){
                            DataManagerCategoria.traerCategoria(nombreCategoria, new onTraerDatoListener() {
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
}