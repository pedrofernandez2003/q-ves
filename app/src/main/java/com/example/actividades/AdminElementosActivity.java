package com.example.actividades;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import com.example.R;
import com.example.objetos.Plantilla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;


public class AdminElementosActivity extends AppCompatActivity  {

    private static final int REQUEST_EXTERNAL_STORAGe = 1;
    private static String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
@Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_elementos);
        CardView tarjetasYCategorias = (CardView) findViewById(R.id.tarjetasYCategorias);
        CardView personajes = (CardView) findViewById(R.id.personajes);
        CardView plantillas=  findViewById(R.id.adminElementos);
    System.out.println("window" + getWindow().getDecorView().getRootView());

    View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

    tarjetasYCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminElementosActivity.this, CategoriasActivity.class);
                startActivity(i);
            }
        });

        personajes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(AdminElementosActivity.this, PersonajesActivity.class);
                        startActivity(i);
                    }
         });

    plantillas.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(AdminElementosActivity.this, AdministrarPlantillasActivity.class);
            startActivity(i);
        }
    });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleOnBackPressed() {
                Intent intent=new Intent(AdminElementosActivity.this,AdministradorActivity.class);
                startActivity(intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

    store(getScreenShot(rootView),"prueba");
}

    public  Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bitmap, String filename) {
        String path = Environment.getExternalStorageDirectory().toString() + "/" + filename;
        OutputStream out = null;
        File imageFile = new File(path);

        try {
            out = new FileOutputStream(imageFile);
            // choose JPEG format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (out != null) {
                    out.close();
                }

            } catch (Exception exc) {
                exc.printStackTrace();
            }


        }
    }

}
