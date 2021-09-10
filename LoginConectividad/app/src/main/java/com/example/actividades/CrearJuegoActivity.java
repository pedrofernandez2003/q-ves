package com.example.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.R;

import com.example.listeners.onInsertarListener;
import com.example.listeners.onTraerDatosListener;
import com.example.objetos.CategoriaSinTarjetas;
import com.example.objetos.Member;
import com.example.objetos.PlantillaNueva;
import com.example.objetos.ViewHolder;
import com.example.dataManagers.DataManagerCategoria;
import com.example.dataManagers.DataManagerPlantillas;
import com.example.personajesEnMiniaturas.ImageDisplay;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Arrays;

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
        PlantillaNueva plantillaNueva = PlantillaNueva.obtenerPlantilla();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                if(plantillaNueva.getCategorias().equals(null)) {
                    checkedItems = new boolean[datos.size()];
                }
                int i = 0;
                for (Object dato : datos) {
                    CategoriaSinTarjetas categoria = (CategoriaSinTarjetas) dato;
                    nombresCategoria.add(categoria.getNombre());
                }
                listItems = nombresCategoria;
                plantillaNueva.setCategorias(listItems);
            }
        });

        if( getIntent().getExtras() != null)
        {
            checkedItems = new boolean[plantillaNueva.getCategorias().size()];
            segundaVez=true;
            ArrayList<String> cantidadPersonajesElegidos = getIntent().getStringArrayListExtra("personajes");
            plantillaNueva.setUrls(cantidadPersonajesElegidos);
            nombreJuego.setText(plantillaNueva.getNombrePlantilla());
            cantidadEquipos.setText(plantillaNueva.getCantidadEquipos());
            for(String categoria: plantillaNueva.getCategoriasElegidas()){
                mUserItems.add(plantillaNueva.getCategorias().indexOf(categoria.replace(" ","")));
                checkedItems[plantillaNueva.getCategorias().indexOf(categoria.replace(" ",""))]=true;
            }
            mItemSelected.setText(escribirCategorias(mUserItems, plantillaNueva.getCategorias()));
            personajesElegidos.setText("Eligió "+cantidadPersonajesElegidos.size()+  " personajes");

        }




        personajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    guardarDatos(plantillaNueva, mItemSelected.getText().toString());
                    Intent intent = new Intent(CrearJuegoActivity.this, ImageDisplay.class);
                    intent.putExtra("personajes", plantillaNueva.getUrls());
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
                        mItemSelected.setText(escribirCategorias(mUserItems,listItems));
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
                    plantillaNueva.setUsuario(user.getEmail());
                    guardarDatos(plantillaNueva, mItemSelected.getText().toString());
                    DataManagerPlantillas.insertarPlantilla(plantillaNueva, new onInsertarListener() {
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

    private String escribirCategorias(ArrayList<Integer> categoriasSeleccionadas, ArrayList<String> listaCategorias) {
        String item = "";
        for (int i = 0; i < categoriasSeleccionadas.size(); i++) {
            item = item + listaCategorias.get(categoriasSeleccionadas.get(i));
            if (i != categoriasSeleccionadas.size() - 1) {
                item = item + ",";
            }
        }
        return item;
    }

    private boolean verificarCamposCompletos() {
        if (nombreJuego.getText().toString().matches("") || cantidadEquipos.getText().toString().matches("") || mItemSelected.getText().toString().isEmpty() || personajesElegidos.getText().equals("Aún no ha seleccionado personajes ") || personajesElegidos.getText().equals("Eligió 0 personajes") ) {
            return false;
        }
        return true;
    }

    private void guardarDatos(PlantillaNueva plantillaNueva, String categoriasSeleccionadas) {
        plantillaNueva.setNombrePlantilla(nombreJuego.getText().toString());
        plantillaNueva.setCantidadEquipos(cantidadEquipos.getText().toString());
        ArrayList<String> categorias = new ArrayList<>(Arrays.asList(categoriasSeleccionadas.split(",")));
        plantillaNueva.setCategoriasElegidas(categorias);

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




