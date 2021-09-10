package com.example.login_crud;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Listeners.onModificarListener;
import com.example.Objetos.Categoria;
import com.example.Objetos.Color;
import com.example.Objetos.Tarjeta;
import com.example.Listeners.onTraerDatoListener;
import com.example.Listeners.onTraerDatosListener;
import com.example.R;

import java.util.ArrayList;

public class PruebaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        getSupportActionBar().hide();

        Tarjeta tarjetaAntigua= new Tarjeta("J","K");
        Tarjeta tarjetaModificada= new Tarjeta("A","B");

        DataManagerCategoria.modificarDatosTarjeta("3", tarjetaModificada, tarjetaAntigua, new onModificarListener() {
            @Override
            public void modificar(boolean modificado) {
                System.out.println("Se actualizo?"+modificado);
            }
        });

        /*DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
            @Override
            public void traerDatos( ArrayList<Object> datos) {
            //casteo a categoria
                for (Object categoria:datos) {
                    Categoria pruebacategoria=(Categoria) categoria;
                    System.out.println("rgb color: " + Color.valueOf(pruebacategoria.getColor()).getCodigo() + pruebacategoria.getNombre());
                }
            }
        });

      DataManagerCategoria.traerIdCategoria("calle", new onTraerDatoListener() {
          @Override
          public void traer(Object id) {
              System.out.println("el id es: "+ (String) id);
          }
      });

      DataManagerCategoria.traerTarjetasCategoria("1", new onTraerDatosListener() {
          @Override
          public void traerDatos(ArrayList<Object> datos) {
              for (Object tarjetas:datos) {
                  Tarjeta tarjeta=(Tarjeta) tarjetas;
                  System.out.println("Contenido: " + tarjeta.getContenido() + "Yapa: " + tarjeta.getYapa());
              }
          }
      });*/
      /*String color="AMARILLO";
      Categoria categoria = new Categoria("Lol",Color.VIOLETA);
      DataManagerCategoria.traerIdCategoria("Lol",new onTraerDatoListener() {
                @Override
                public void traer(Object id) {
                    DataManagerCategoria.modificarDatosCategoria((String) id,categoria, new onModificarListener() {
                        @Override
                        public void modificar(boolean modificado) {
                            System.out.println("Se modifico? " + modificado);

                        }
                    });
                }
        });*/



    }
}
