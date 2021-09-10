package com.example.hotspot;

import android.app.AlertDialog;
import android.content.Context;
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

import com.example.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TableroActivity extends AppCompatActivity {
    private LinearLayout verCartas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero_creable);
        verCartas = findViewById(R.id.verCartas);


        verCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("tocaste mandar");

                ArrayList<TarjetaConCategoriaEmbebida> tarjetasArray= new ArrayList<>();
                CategoriaSinTarjetas categoria1= new CategoriaSinTarjetas("categoria1","AMARILLO",3);
                CategoriaSinTarjetas categoria2= new CategoriaSinTarjetas("categoria2","AZUL",3);
                CategoriaSinTarjetas categoria3= new CategoriaSinTarjetas("categoria3","VERDE",3);
                TarjetaConCategoriaEmbebida tarjeta1=new TarjetaConCategoriaEmbebida("prueba","probandoooooo",categoria1);
                TarjetaConCategoriaEmbebida tarjeta2=new TarjetaConCategoriaEmbebida("prueba","probandoooooo",categoria1);
                TarjetaConCategoriaEmbebida tarjeta3=new TarjetaConCategoriaEmbebida("prueba","probandoooooo",categoria2);
                TarjetaConCategoriaEmbebida tarjeta4=new TarjetaConCategoriaEmbebida("prueba","probandoooooo",categoria2);
                TarjetaConCategoriaEmbebida tarjeta5=new TarjetaConCategoriaEmbebida("prueba","probandoooooo",categoria3);
                TarjetaConCategoriaEmbebida tarjeta6=new TarjetaConCategoriaEmbebida("prueba","probandoooooo",categoria3);
                tarjetasArray.add(tarjeta1);
                tarjetasArray.add(tarjeta2);
                tarjetasArray.add(tarjeta3);
                tarjetasArray.add(tarjeta4);
                tarjetasArray.add(tarjeta5);
                tarjetasArray.add(tarjeta6);

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
                }




            }
        });
    }
}
