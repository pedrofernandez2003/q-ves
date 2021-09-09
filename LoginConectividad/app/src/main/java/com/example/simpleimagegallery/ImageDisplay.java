package com.example.simpleimagegallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Listeners.onTraerPersonajesListener;
import com.example.login_crud.CrearJuegoActivity;
import com.example.login_crud.DataManager;
import com.example.login_crud.R;
import com.example.simpleimagegallery.fragments.pictureBrowserFragment;
import com.example.simpleimagegallery.utils.MarginDecoration;
import com.example.simpleimagegallery.utils.OnSwipeTouchListener;
import com.example.simpleimagegallery.utils.PicHolder;
import com.example.simpleimagegallery.utils.itemClickListener;
import com.example.simpleimagegallery.utils.pictureFacer;
import com.example.simpleimagegallery.utils.picture_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
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
        getSupportActionBar().hide();

        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);


        if(allpictures.isEmpty()){
            load.setVisibility(View.VISIBLE);
            final ImageDisplay that  = this;
            ArrayList<pictureFacer> allpictures= new ArrayList<>();
            traerPersonajes(new onTraerPersonajesListener() {
                @Override
                public void traerPersonaje(ArrayList<pictureFacer> datos) {
                    for (pictureFacer personaje : datos) {
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
                    imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,that));
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
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {
        pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);
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
        ArrayList<pictureFacer> personajes = new ArrayList<>();
        DataManager.getDb().collection("personajes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        pictureFacer pic = new pictureFacer();
                        pic.setPicturePath((String) document.getData().get("url"));
                        personajes.add(pic);
                    }
                    listener.traerPersonaje(personajes);
                }
            }
        });
    }



}
