package com.example.login_crud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent data = getIntent();
        ArrayList<String> personajesElegidos = data.getStringArrayListExtra("personajes");
        System.out.println("los pers son: "+personajesElegidos);
        Button iniciarSesion = (Button) findViewById(R.id.iniciarSesion);
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("On click");
                Intent login = new Intent(MainActivity.this, LoginActivity.class);

                startActivity(login);
            }
        });
    }




}