package com.example.actividades;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.R;


public class AdminElementosActivity extends AppCompatActivity  {
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_elementos);
        CardView tarjetasYCategorias = (CardView) findViewById(R.id.tarjetasYCategorias);
        CardView personajes = (CardView) findViewById(R.id.personajes);

        tarjetasYCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminElementosActivity.this, CategoriasActivity.class);
                startActivity(i);
            }
        });

        personajes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(AdminElementosActivity.this, PersonajesActivity.class);
                        startActivity(i);
                    }
         });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleOnBackPressed() {
                Intent intent=new Intent(AdminElementosActivity.this,AdministradorActivity.class);
                startActivity(intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

    }

}
