package com.example.login_crud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminElementosActivity extends AppCompatActivity  {
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_elementos);
        Button tarjetasYCategorias = (Button) findViewById(R.id.tarjetasYCategorias);
        Button personajes = (Button) findViewById(R.id.personajes);

        tarjetasYCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminElementosActivity.this, CrearJuegoActivity.class);
                startActivity(i);
            }
        });

        personajes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(AdminElementosActivity.this, IniciarJuegoActivity.class);
                        startActivity(i);
                    }
         });

    }

}
