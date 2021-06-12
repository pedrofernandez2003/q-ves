package com.example.login_crud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.Objetos.Tarjeta;
import com.example.Objetos.onCollectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public  abstract class DataManagerCategoria  {

//    public DataManagerCategoria() {
//        super();
//    }

//    public  static void traerCategorias(onCollectionListener listener) {
//        ArrayList<Categoria> categorias=new ArrayList<Categoria>();
//        FirebaseFirestore db=FirebaseFirestore.getInstance();
//        db.collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                String TAG = "";
//                if (task.isSuccessful()) { //ver como guardamos el tema del rgb, si como esta clase colorspace.rgc o como un hexadecimal
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        HashMap<String, String> colorInfo = (HashMap<String, String>) document.getData().get("color");
//                        Color color = new Color((String) colorInfo.get("nombre"), (String) colorInfo.get("codigo"));
//                        Categoria categoria=new Categoria((String) document.getData().get("nombre"),color);
//                        categorias.add(categoria);
//                    }
//                    listener.traeTodasLasColecciones(categorias);
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//            }
//        });
//    }
    public static void traerIdCategoria(String nombre, onCollectionListener listener){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("categorias").whereEqualTo("nombre", nombre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "";
                String id= "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id=document.getId();
                    }
                    listener.traerIdCategoria(id);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }});
    }

    public static void traerTarjetasCategoria(String id,onCollectionListener listener){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection("categorias").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<Tarjeta> tarjetas= new ArrayList<Tarjeta>();
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("tarjeta")) {
                                tarjetas= (ArrayList<Tarjeta>) entry.getValue();
                            }
                        }
                        listener.traerTarjetasCategoria(tarjetas);
                    }
                }
            }
        });
    }

}

//conseguir todas las categorias, conseguir una categoria segun color?, conseguir categoria segun nombre

