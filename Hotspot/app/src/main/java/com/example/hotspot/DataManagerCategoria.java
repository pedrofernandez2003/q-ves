package com.example.hotspot;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.example.hotspot.onTraerDatosListener;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class DataManagerCategoria extends DataManager {
    public DataManagerCategoria() {
        super();
    }
//    db.collection("cities")
//            .whereEqualTo("capital", true)
//        .get()
//        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//        @Override
//        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    Log.d(TAG, document.getId() + " => " + document.getData());
//                }
//            } else {
//                Log.d(TAG, "Error getting documents: ", task.getException());
//            }
//        }
//    });
    public static void traerCategoria(String nombre, onTraerDatoListener listener) {
        DataManager.getDb().collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                String id = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Color color_a_utilizar=Color.AMARILLO;
                        int cantidadTarjetas=0;
//                        for(Color color:Color.values()){
//                            if (document.getData().get("color").equals(color.toString())){
//                                color_a_utilizar=color;
//                            }
//                        }
                        List<Map<String, String>> tarjetas = (List<Map<String, String>>) document.getData().get("tarjeta");
                        if(tarjetas!=null){
                            cantidadTarjetas=tarjetas.size();
                        }
                        Categoria categoria = new Categoria((String) document.getData().get("nombre"), (String) document.getData().get("color"),cantidadTarjetas);
                        if(categoria.getNombre().equals(nombre)){
                            listener.traer((Object) categoria);
                        }
                        else{
                            listener.traer(null);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
