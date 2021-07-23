package com.example.login_crud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.Listeners.onTraerDatosListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class AdministradorActivity extends AppCompatActivity {

    private Button crearJuego,iniciarJuego, administrarElementos;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        Toast.makeText(AdministradorActivity.this,currentUser.getDisplayName(),Toast.LENGTH_SHORT).show();

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
                        Intent i = new Intent(AdministradorActivity.this, IniciarJuegoActivity.class);
                        startActivity(i);
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