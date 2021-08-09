package com.example.hotspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class Jugar extends AppCompatActivity  {
    private GameContext context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero);
        ArrayList<String> datos=new ArrayList<>();
        datos.add("\"aloo\"");
        Mensaje mensaje=new Mensaje("comenzar",datos);
        String msg=mensaje.serializar();
        System.out.println(msg);
        byte[] bytesMsg = msg.getBytes();
        Write escribir = new Write();
        escribir.execute(bytesMsg,0);
//        context.getHijos().get(0).write();

    }
}