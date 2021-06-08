package com.example.login_crud;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Objetos.Categoria;
import com.example.Objetos.onCollectionListener;

import java.util.ArrayList;

public class PruebaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        DataManagerCategoria.traerCategorias(new onCollectionListener() {
            @Override
            public void onComplete(ArrayList<Categoria> categorias) {
                for (Categoria categoria:categorias) {
                    System.out.println(categoria.getColor() + categoria.getnombre());
                }
            }
        });
    }


}
