package com.example.personajesEnMiniaturas;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    TextView cantidadPersonajesElegidos;

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
        cantidadPersonajesElegidos = findViewById(R.id.textView3);
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
                    System.out.println(browser.getImage());
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);

                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    browser.getImage().setColorFilter(filter);
                    personajesElegidos.add((String) pics.get(posicion).getPicturePath());
                    seleccionarPersonaje.setText("Deseleccionar");
                    seleccionarPersonaje.setVisibility(View.GONE);
                    cantidadPersonajesElegidos.setText("Personajes seleccionados: " + personajesElegidos.size());
                }
                else{
                    personajesElegidos.remove((String) pics.get(posicion).getPicturePath());
                    seleccionarPersonaje.setText("Seleccionar");
                    getSupportFragmentManager().popBackStack();
                    seleccionarPersonaje.setVisibility(View.GONE);
                    cantidadPersonajesElegidos.setText("Personajes seleccionados: " + personajesElegidos.size());

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
