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
    public static void traerCategoria(String nombre, onTraerDatoListener listener) {
        DataManager.getDb().collection("categorias").whereEqualTo("nombre", nombre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                String id = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Color color_a_utilizar=Color.AMARILLO;
                        for(Color color:Color.values()){
                            if (document.getData().get("color").equals(color.toString())){
                                color_a_utilizar=color;
                            }
                        }
                        List<Map<String, String>> tarjetas = (List<Map<String, String>>) document.getData().get("tarjeta");
                        Categoria categoria = new Categoria((String) document.getData().get("nombre"), color_a_utilizar,tarjetas.size());
                        listener.traer((Object) categoria);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
