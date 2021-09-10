package com.example.personajesEnMiniaturas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listeners.onTraerPersonajesListener;
import com.example.actividades.CrearJuegoActivity;
import com.example.dataManagers.DataManager;
import com.example.R;
import com.example.personajesEnMiniaturas.fragments.PictureBrowserFragment;
import com.example.personajesEnMiniaturas.utils.MarginDecoration;
import com.example.personajesEnMiniaturas.utils.PicHolder;
import com.example.listeners.itemClickListener;
import com.example.personajesEnMiniaturas.utils.PictureFacer;
import com.example.personajesEnMiniaturas.utils.PictureAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<PictureFacer> allpictures;
    ProgressBar load;
    ArrayList<String> personajesElegidos = new ArrayList<>();

    public void sendMessage(View view) {
        Intent intent = new Intent(ImageDisplay.this, CrearJuegoActivity.class);
        intent.putExtra("personajes", personajesElegidos);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);


        if(allpictures.isEmpty()){
            load.setVisibility(View.VISIBLE);
            final ImageDisplay that  = this;
            ArrayList<PictureFacer> allpictures= new ArrayList<>();
            traerPersonajes(new onTraerPersonajesListener() {
                @Override
                public void traerPersonaje(ArrayList<PictureFacer> datos) {
                    for (PictureFacer personaje : datos) {
//                        if( getIntent().getExtras() != null)
//                        {
//                            personajesElegidos = getIntent().getStringArrayListExtra("personajes");
//                            if( getIntent().getStringArrayListExtra("personajes").contains(personaje.getPicturePath())){
//                                System.out.println("Imagen que viene de la clase: "+personaje.getPicturePath());
//
//                            }
//                        }
                        System.out.println(datos);

                        allpictures.add(personaje);
                    }
                    imageRecycler.setAdapter(new PictureAdapter(allpictures,ImageDisplay.this,that));
                    load.setVisibility(View.GONE);
                }
            });

        }

    }
    private int getCurrentItem(){
        return ((LinearLayoutManager)imageRecycler.getLayoutManager())
                .findFirstVisibleItemPosition();
    }


    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<PictureFacer> pics) {
        PictureBrowserFragment browser = PictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);
        FrameLayout imagenAgrandada = findViewById(R.id.displayContainer);
        Button seleccionarPersonaje = new Button(this);
        seleccionarPersonaje.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        int posicion = browser.getPosition();
        if(personajesElegidos.contains((String) pics.get(posicion).getPicturePath())){
            seleccionarPersonaje.setText("Deseleccionar");
        }
        else{
            seleccionarPersonaje.setText("Seleccionar");

        }
        imagenAgrandada.addView(seleccionarPersonaje);

        seleccionarPersonaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posicion = browser.getPosition();
                if(seleccionarPersonaje.getText()=="Seleccionar") {
                    getSupportFragmentManager().popBackStack();
                    personajesElegidos.add((String) pics.get(posicion).getPicturePath());
                    seleccionarPersonaje.setText("Deseleccionar");
                    seleccionarPersonaje.setVisibility(View.GONE);
                }
                else{
                    personajesElegidos.remove((String) pics.get(posicion).getPicturePath());
                    seleccionarPersonaje.setText("Seleccionar");
                    getSupportFragmentManager().popBackStack();
                    seleccionarPersonaje.setVisibility(View.GONE);
                }
            }

        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {

    }


    private void traerPersonajes(onTraerPersonajesListener listener) {
        ArrayList<PictureFacer> personajes = new ArrayList<>();
        DataManager.getDb().collection("personajes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PictureFacer pic = new PictureFacer();
                        pic.setPicturePath((String) document.getData().get("url"));
                        personajes.add(pic);
                    }
                    listener.traerPersonaje(personajes);
                }
            }
        });
    }



}
