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
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Listeners.onInsertarListener;
import com.example.Listeners.onTraerDatosListener;
import com.example.Objetos.Categoria;
import com.example.Objetos.Member;
import com.example.Objetos.Plantilla;
import com.example.Objetos.ViewHolder;
import com.example.simpleimagegallery.ImageDisplay;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CrearJuegoActivity extends AppCompatActivity  {
    Button mOrder;
    Button personajes;
    Button guardarPlantilla;
    TextView mItemSelected;
    TextView personajesElegidos;
    ArrayList<String> listItems;
    Boolean segundaVez;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    RecyclerView mRecyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    EditText nombreJuego;
    EditText cantidadEquipos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_crear_juego);
        super.onCreate(savedInstanceState);
        segundaVez = false;
        personajes = (Button) findViewById(R.id.botonPersonajes);
        mOrder = (Button) findViewById(R.id.btnOrder);
        guardarPlantilla = (Button) findViewById(R.id.guardarPlantilla);
        ArrayList<String> nombresCategoria = new ArrayList<>();
        mItemSelected = (TextView) findViewById(R.id.tvItemSelected);
        personajesElegidos = (TextView) findViewById(R.id.cantidadPersonajesElegidos);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Data");
        nombreJuego = (EditText) findViewById(R.id.completarNombreJuego);
        cantidadEquipos = (EditText) findViewById(R.id.completarCantidadEquipos);
        Plantilla plantilla = Plantilla.obtenerPlantilla();

        DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                System.out.println("traigo datos");
                if(!segundaVez) {
                    checkedItems = new boolean[datos.size()];
                }
                int i = 0;
//                Arrays.fill(checkedItems, true);
                for (Object dato : datos) {
                    Categoria categoria = (Categoria) dato;
                    nombresCategoria.add(categoria.getNombre());
                }
                listItems = nombresCategoria;
                plantilla.setCategorias(listItems);
            }
        });

        if( getIntent().getExtras() != null)
        {
            checkedItems = new boolean[plantilla.getCategorias().size()];
            segundaVez=true;
            ArrayList<String> cantidadPersonajesElegidos = getIntent().getStringArrayListExtra("personajes");
            plantilla.setUrls(cantidadPersonajesElegidos);
            nombreJuego.setText(plantilla.getNombrePlantilla());
            cantidadEquipos.setText(plantilla.getCantidadEquipos());
            String categorias= "";
            String categoriasAux;
            for(String categoria: plantilla.getCategoriasElegidas()){
                mUserItems.add(plantilla.getCategorias().indexOf(categoria.replace(" ","")));
                checkedItems[plantilla.getCategorias().indexOf(categoria.replace(" ",""))]=true;
//                categoriasAux = categoria;
//                if(categoria!=plantilla.getCategoriasElegidas().get(0)) {
//                    categorias = categorias + ", " + categoriasAux;
//                }
//                else{
//                    categorias = categoria;
//                }
            }
            String item = "";
            for (int i = 0; i < mUserItems.size(); i++) {
                item = item + plantilla.getCategorias().get(mUserItems.get(i));
                if (i != mUserItems.size() - 1) {
                    item = item + ", ";
                }
            }
            mItemSelected.setText(item);

//            mItemSelected.setText(categorias);
            personajesElegidos.setText("Eligió "+cantidadPersonajesElegidos.size()+  " personajes");
        }




        personajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    guardarDatos(plantilla, mItemSelected.getText().toString());
                    Intent intent = new Intent(CrearJuegoActivity.this, ImageDisplay.class);
                    intent.putExtra("personajes", plantilla.getUrls());
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



        guardarPlantilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarCamposCompletos()) {
                    guardarDatos(plantilla, mItemSelected.getText().toString());
                    DataManagerCategoria.insertarPlantilla(plantilla, new onInsertarListener() {
                        @Override
                        public void insertar(boolean insertado) {
                            if (insertado) {
                                Toast.makeText(CrearJuegoActivity.this, "Plantilla guardada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CrearJuegoActivity.this, "No se ha podido guardar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(CrearJuegoActivity.this, "Ingrese todos los datos antes de continuar", Toast.LENGTH_SHORT).show();

                }
            }
        });





}

//    private void traigoDatos() {
//        ArrayList<String> nombresCategoria = new ArrayList<>();
//        DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
//            @Override
//            public void traerDatos(ArrayList<Object> datos) {
//                System.out.println("traigo datos");
//                checkedItems = new boolean[datos.size()];
//                int i = 0;
////                Arrays.fill(checkedItems, true);
//                for (Object dato : datos) {
//                    Categoria categoria = (Categoria) dato;
//                    nombresCategoria.add(categoria.getNombre());
//                }
//                listItems = nombresCategoria;
//                System.out.println("list items" + listItems);
//            }
//        });
//    }
    private boolean verificarCamposCompletos() {
        if (nombreJuego.getText().toString().matches("") || cantidadEquipos.getText().toString().matches("") || mItemSelected.getText().toString().isEmpty() || personajesElegidos.getText().equals("Aún no ha seleccionado personajes ") || personajesElegidos.getText().equals("Eligió 0 personajes") ) {
            return false;
        }
        return true;
    }

    private void guardarDatos(Plantilla plantilla, String categoriasSeleccionadas) {
        plantilla.setNombrePlantilla(nombreJuego.getText().toString());
        plantilla.setCantidadEquipos(cantidadEquipos.getText().toString());
        ArrayList<String> categorias = new ArrayList<>(Arrays.asList(categoriasSeleccionadas.split(",")));
        plantilla.setCategoriasElegidas(categorias);

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




