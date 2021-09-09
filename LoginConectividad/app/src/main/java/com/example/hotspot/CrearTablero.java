package com.example.hotspot;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CrearTablero extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero);

        mostrarPlantillas();

    }

    private void mostrarPlantillas()  {
        DataManagerPlantillas.traerPlantillas(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                for (Object PlantillaObject : datos) {
                    Plantilla plantilla = (Plantilla) PlantillaObject;
                    for (Categoria categoria : plantilla.getCategorias()) {
                        categoria.getColor();
                        categoria.getNombre();
                        System.out.println(categoria.getNombre());
                    }
                }
            }
        });
    }
}

