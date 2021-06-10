package com.example.login_crud;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Objetos.onCollectionListener;

public class PruebaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        /*DataManagerCategoria.traerCategorias(new onCollectionListener() {
            @Override
            public void traeTodasLasColecciones(ArrayList<Categoria> categorias) {
                for (Categoria categoria:categorias) {
                    System.out.println("rgb color: " + categoria.getColor().getRgb() + categoria.getnombre());
                }
            }

        });*/

      DataManagerCategoria.traerIdCategoria("calle", new onCollectionListener() {
          @Override
          public void traerIdCategoria(String id) {
              System.out.println("el id es: "+ id);
          }
      });
    }


}
