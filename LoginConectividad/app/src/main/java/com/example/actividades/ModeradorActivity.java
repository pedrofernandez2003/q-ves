package com.example.actividades;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ModeradorActivity extends AppCompatActivity {

    private CardView crearJuego,iniciarJuego, administrarPlantillas;
    private FloatingActionButton cerrarSesion;
    FirebaseAuth firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderador);
        firebase = FirebaseAuth.getInstance();
        cerrarSesion = findViewById(R.id.fab);
        crearJuego = (CardView) findViewById(R.id.crearJuego);
        iniciarJuego = (CardView) findViewById(R.id.iniciarJuego);
        administrarPlantillas = (CardView) findViewById(R.id.adminElementos);
        crearJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModeradorActivity.this, CrearJuegoActivity.class);
                startActivity(i);
            }
        });
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(ModeradorActivity.this);

                View dialog_layout = inflater.inflate(R.layout.confirmacion_accion, null);
                TextView seleccionado = dialog_layout.findViewById(R.id.confirmarSelección);
                seleccionado.setText("¿Quiere cerrar la sesión?");
                AlertDialog.Builder db = new AlertDialog.Builder(ModeradorActivity.this);
                db.setView(dialog_layout);
                db.setTitle("Cerrar sesión");
                db.setPositiveButton("Sí", null);
                db.setNegativeButton("Cancelar", null);
                final AlertDialog a = db.create();

                a.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button si = a.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button no = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                a.dismiss();
                            }
                        });
                        si.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick (View view){
                                firebase.signOut();
                                Intent intent = new Intent(ModeradorActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
                a.show();

            }


        });
        iniciarJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModeradorActivity.this, TraerJuegosActivity.class);
                startActivity(i);
            }
        });

        administrarPlantillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ModeradorActivity.this, AdministrarPlantillasActivity.class);
                startActivity(i);
            }
        });

    }
}
