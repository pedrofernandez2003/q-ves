package com.example.hotspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Jugar extends AppCompatActivity  {
    private GameContext context;

    public class Write extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                context.getHijos().get((Integer) objects[1]).write((byte[]) objects[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero);
        context=GameContext.getGameContext();
        System.out.println(context.getHijos().size());
        ArrayList<String> datos=new ArrayList<>();
        datos.add("\"aloo\"");
        Mensaje mensaje=new Mensaje("comenzar",datos);
        String msg=mensaje.serializar();
//        System.out.println(msg);
        byte[] bytesMsg = msg.getBytes();
//        Write escribir = new Write();
//        escribir.execute(bytesMsg,0);
    }
}