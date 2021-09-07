package com.example.hotspot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TableroActivity extends AppCompatActivity {
    private LinearLayout verCartas;
    private TarjetaConCategoriaEmbebida tarjetaElegida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero_creable);
        verCartas = findViewById(R.id.verCartas);

        ArrayList<TarjetaConCategoriaEmbebida> tarjetasArray= new ArrayList<>();
        CategoriaSinTarjetas categoria1= new CategoriaSinTarjetas("categoria1","AMARILLO",3);
        CategoriaSinTarjetas categoria2= new CategoriaSinTarjetas("categoria2","AZUL",3);
        CategoriaSinTarjetas categoria3= new CategoriaSinTarjetas("categoria3","VERDE",3);
        TarjetaConCategoriaEmbebida tarjeta1=new TarjetaConCategoriaEmbebida("prueba1","probandoooooo",categoria1);
        TarjetaConCategoriaEmbebida tarjeta2=new TarjetaConCategoriaEmbebida("prueba2","probandoooooo",categoria1);
        TarjetaConCategoriaEmbebida tarjeta3=new TarjetaConCategoriaEmbebida("prueba3","probandoooooo",categoria2);
        TarjetaConCategoriaEmbebida tarjeta4=new TarjetaConCategoriaEmbebida("prueba4","probandoooooo",categoria2);
        TarjetaConCategoriaEmbebida tarjeta5=new TarjetaConCategoriaEmbebida("prueba5","probandoooooo",categoria3);
        TarjetaConCategoriaEmbebida tarjeta6=new TarjetaConCategoriaEmbebida("prueba6","probandoooooo",categoria3);
        tarjetasArray.add(tarjeta1);
        tarjetasArray.add(tarjeta2);
        tarjetasArray.add(tarjeta3);
        tarjetasArray.add(tarjeta4);
        tarjetasArray.add(tarjeta5);
        tarjetasArray.add(tarjeta6);

        verCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("tocaste mandar");



                LayoutInflater inflater = LayoutInflater.from(TableroActivity.this);
                View dialog_layout = inflater.inflate(R.layout.ver_cartas, null);
                AlertDialog.Builder db = new AlertDialog.Builder(TableroActivity.this);
                db.setView(dialog_layout);

                LinearLayout contenedorCartas=(LinearLayout) dialog_layout.findViewById(R.id.contenedorCartas);

                for (int i = 0; i < tarjetasArray.size(); i++) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT );
                    Button button = new Button(getApplicationContext());
                    button.setLayoutParams(lp);
                    button.setText(tarjetasArray.get(i).getContenido());
                    button.setBackgroundColor(tarjetasArray.get(i).getCategoria().getColor().getCodigo());
                    contenedorCartas.addView(button);
                    int finalI = i;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tarjetaElegida=tarjetasArray.get(finalI);
                            System.out.println(tarjetasArray.get(finalI).getContenido());

                            //button.setPadding(4,4,4,4);
                        }
                    });
                }

                db.setPositiveButton("Enviar al tablero", null);
                db.setNegativeButton("Atras", null);
                final AlertDialog a = db.create();
                a.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = a.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                insertarTarjetaEnTablero();
                                a.dismiss();

                            }
                        });
                    }
                });
                a.show();





            }
        });
    }

    public void insertarTarjetaEnTablero(){
        tarjetaElegida;

    }
}
