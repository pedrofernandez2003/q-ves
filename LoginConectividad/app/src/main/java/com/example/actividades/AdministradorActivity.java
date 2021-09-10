package com.example.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.R;


public class AdministradorActivity extends AppCompatActivity {

    private Button crearJuego,iniciarJuego, administrarElementos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        crearJuego = (Button) findViewById(R.id.crearJuego);
        iniciarJuego = (Button) findViewById(R.id.iniciarJuego);
        administrarElementos = (Button) findViewById(R.id.adminElementos);
        crearJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministradorActivity.this, CrearJuegoActivity.class);
                startActivity(i);
            }
        });
        iniciarJuego.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent elegirPlantilla = new Intent(AdministradorActivity.this, TraerJuegosActivity.class);
                        startActivity(elegirPlantilla);
                    }
         });
         administrarElementos.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent i = new Intent(AdministradorActivity.this, AdminElementosActivity.class);
                         startActivity(i);
                     }
          });
              
    }




}