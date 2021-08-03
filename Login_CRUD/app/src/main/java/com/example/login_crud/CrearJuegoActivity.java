package com.example.login_crud;

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

import com.example.Listeners.onTraerDatosListener;
import com.example.Objetos.Categoria;
import com.example.Objetos.Member;
import com.example.Objetos.ViewHolder;
import com.example.simpleimagegallery.ImageDisplay;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class CrearJuegoActivity extends AppCompatActivity  {
    Button mOrder;
    Button personajes;
    TextView mItemSelected;
    TextView personajesElegidos;
    ArrayList<String> listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    RecyclerView mRecyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_crear_juego);
        Intent data = getIntent();
        ArrayList<String> cantidadPersonajesElegidos = data.getStringArrayListExtra("personajes");
        System.out.println("los pers son: "+cantidadPersonajesElegidos.size());
        super.onCreate(savedInstanceState);
        personajes = (Button) findViewById(R.id.botonPersonajes);
        mOrder = (Button) findViewById(R.id.btnOrder);
        ArrayList<String> nombresCategoria = new ArrayList<>();
        mItemSelected = (TextView) findViewById(R.id.tvItemSelected);
        personajesElegidos = (TextView) findViewById(R.id.cantidadPersonajesElegidos);
//        if(!cantidadPersonajesElegidos.equals(null)) {
//            personajesElegidos.setText("");
//        }
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Data");
        DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                checkedItems = new boolean[datos.size()];
                for (Object dato: datos) {
                    Categoria categoria= (Categoria) dato;
                    nombresCategoria.add(categoria.getNombre());
                }
                listItems = nombresCategoria;
            }
        });

        personajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrearJuegoActivity.this, ImageDisplay.class);
                startActivity(intent);
            }
        });
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CrearJuegoActivity.this);
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(listItems.toArray(new String[0]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mUserItems.add(position);
                        }else{
                            mUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + listItems.get(mUserItems.get(i));
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        mItemSelected.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                            mItemSelected.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });



}

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Member, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Member, ViewHolder>(Member.class,R.layout.image,ViewHolder.class,reference) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Member member, int i) {

                viewHolder.setDetails(getApplicationContext(),member.getImage());
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        }



}




