package com.example.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.R;
import com.example.objetos.Juego;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;


public class AdministradorActivity extends AppCompatActivity {

    private CardView crearJuego,iniciarJuego, administrarElementos;
    private FloatingActionButton cerrarSesion;
    FirebaseAuth firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        firebase = FirebaseAuth.getInstance();
        cerrarSesion = findViewById(R.id.fab);
        cerrarSesion.bringToFront();
        crearJuego = (CardView) findViewById(R.id.crearJuego);
        iniciarJuego = (CardView) findViewById(R.id.iniciarJuego);
        administrarElementos = (CardView) findViewById(R.id.adminElementos);
        crearJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministradorActivity.this, CrearJuegoActivity.class);
                startActivity(i);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(AdministradorActivity.this);

                View dialog_layout = inflater.inflate(R.layout.confirmacion_accion, null);
                TextView seleccionado = dialog_layout.findViewById(R.id.confirmarSelección);
                seleccionado.setText("¿Quiere cerrar la sesión?");
                AlertDialog.Builder db = new AlertDialog.Builder(AdministradorActivity.this);
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
                                Intent intent = new Intent(AdministradorActivity.this, MainActivity.class);
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