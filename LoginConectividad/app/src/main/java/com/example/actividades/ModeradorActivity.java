package com.example.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.R;

import androidx.appcompat.app.AppCompatActivity;

public class ModeradorActivity extends AppCompatActivity {

    private Button crearJuego,iniciarJuego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderador);
        crearJuego = (Button) findViewById(R.id.crearJuego);
        iniciarJuego = (Button) findViewById(R.id.iniciarJuego);
        crearJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModeradorActivity.this, CrearJuegoActivity.class);
                startActivity(i);
            }
        });
        iniciarJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModeradorActivity.this, TraerJuegosActivity.class);
                startActivity(i);
            }
        });

    }
}
