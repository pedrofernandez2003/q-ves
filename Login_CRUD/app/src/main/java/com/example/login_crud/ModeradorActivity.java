package com.example.login_crud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                Intent i = new Intent(ModeradorActivity.this, IniciarJuegoActivity.class);
                startActivity(i);
            }
        });

    }
}
