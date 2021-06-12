package com.example.login_crud;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Objetos.Categoria;
import com.example.Objetos.Tarjeta;
import com.example.Objetos.onCollectionListener;

import java.util.ArrayList;
import java.util.Map;

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

      /*DataManagerCategoria.traerIdCategoria("calle", new onCollectionListener() {
          @Override
          public void traeTodasLasColecciones(ArrayList<Categoria> categorias) {

          }

          @Override
          public void traerIdCategoria(String id) {
              System.out.println("el id es: "+ id);
          }
      });*/
      DataManagerCategoria.traerTarjetasCategoria("1", new onCollectionListener() {
          @Override
          public void traeTodasLasColecciones(ArrayList<Categoria> categorias) {

          }

          @Override
          public void traerIdCategoria(String id) {

          }

          @Override
          public void traerTarjetasCategoria(ArrayList<Tarjeta> Tarjetas) {
              System.out.println("Hola");
          } //Funciono, tengo el arrayList de Tarjetas :))))))
      });
    }


}
