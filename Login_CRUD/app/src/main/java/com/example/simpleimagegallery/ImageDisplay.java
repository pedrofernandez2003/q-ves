package com.example.simpleimagegallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.login_crud.CrearJuegoActivity;
import com.example.login_crud.R;
import com.example.simpleimagegallery.fragments.pictureBrowserFragment;
import com.example.simpleimagegallery.utils.MarginDecoration;
import com.example.simpleimagegallery.utils.PicHolder;
import com.example.simpleimagegallery.utils.itemClickListener;
import com.example.simpleimagegallery.utils.pictureFacer;
import com.example.simpleimagegallery.utils.picture_Adapter;
import com.bumptech.glide.request.SingleRequest;
import com.example.login_crud.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Author CodeBoy722
 *
 * This Activity get a path to a folder that contains images from the MainActivity Intent and displays
 * all the images in the folder inside a RecyclerView
 */

public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
    ProgressBar load;
    ArrayList<String> personajesElegidos = new ArrayList<>();

    public void sendMessage(View view) {
        Intent intent = new Intent(ImageDisplay.this, CrearJuegoActivity.class);
        System.out.println("antes de mandar: "+personajesElegidos);
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
            allpictures = getAllImagesByFolder();
            imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,this));
            load.setVisibility(View.GONE);
        }

    }

    /**
     *
     * @param holder The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     * @param pics An ArrayList of all the items in the Adapter
     */
    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {
        pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);
        System.out.println("posicion "+position);
        FrameLayout imagenAgrandada = findViewById(R.id.displayContainer);
        System.out.println("figura clickeada");
        Button seleccionarPersonaje = new Button(this);
        seleccionarPersonaje.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        seleccionarPersonaje.setText("Elegir personaje");
        imagenAgrandada.addView(seleccionarPersonaje);

        seleccionarPersonaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personajesElegidos.add((String) pics.get(position).getPicturePath());
            }

        });

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position+"picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {

    }


    /**
     * This Method gets all the images in the folder paths passed as a String to the method and returns
     * and ArrayList of pictureFacer a custom object that holds data of a given image
     */
    public ArrayList<pictureFacer> getAllImagesByFolder() {
        ArrayList<pictureFacer> images = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("personajes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                System.out.println("Entre complete");
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        System.out.println(document.getData());
//                        pictureFacer pic = new pictureFacer();
//                        pic.setPicturePath((String) document.getData().get("url"));
//                        images.add(pic);
//                    }
//                }
//            }
//        });
        pictureFacer pic1 = new pictureFacer();
        pictureFacer pic2 = new pictureFacer();
        pic1.setPicturePath("https://firebasestorage.googleapis.com/v0/b/qves-ddf27.appspot.com/o/images%2Fc14267dd-5350-4a27-b275-ea42b663f37a?alt=media&token=4699843f-e812-4c77-88e7-b554dbf85d0e");
        images.add(pic1);
        pic2.setPicturePath("https://firebasestorage.googleapis.com/v0/b/qves-ddf27.appspot.com/o/images%2F47b6a178-2e4f-41cc-b7d9-cde1961f2ded?alt=media&token=d37297e7-9665-41ed-9c5f-84b4719cb621");
        images.add(pic2);
        System.out.println("Tamaño: " + images.size());
        return images;
    }


}
