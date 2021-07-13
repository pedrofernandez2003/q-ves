package com.example.login_crud;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Listeners.onInsertarListener;
import com.example.Listeners.onModificarListener;
import com.example.Listeners.onTraerDatoListener;
import com.example.Listeners.onTraerDatosListener;
import com.example.Objetos.Categoria;
import com.example.Objetos.Color;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoriasActivity extends FragmentActivity {

    public boolean ModoModificar=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this.getApplicationContext();
        ArrayList<String> coloresUsados = traerCategorias(context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjetas_y_categorias);
        Button aniadirCategoria = (Button) findViewById(R.id.aniadirCategoria);
        aniadirCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("on click nueva categoria");
                aniadirCategoria(coloresUsados);
                LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                llBotonera.removeAllViews();
                traerCategorias(context);
            }
        });
        Button modificarCategoria= (Button) findViewById(R.id.modificarCategoria);
        modificarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModoModificar=!ModoModificar;
            }
        });
    }

    public void aniadirCategoria(ArrayList<String> coloresYaSeleccionados) {

        //Las primeas cuatro lineas crean la alerta que te sale ya con el xml que preparamos adentro
        LayoutInflater inflater = LayoutInflater.from(CategoriasActivity.this);
        View dialog_layout = inflater.inflate(R.layout.activity_aniadir_categoria, null);
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setView(dialog_layout);

        //Empiezo a conseguir diferentes cosas de adentro de la alerta
        EditText nombre = dialog_layout.findViewById(R.id.nombreCategoria);
        TextView colorRepetido = dialog_layout.findViewById(R.id.colorRepetido);

        Spinner colores = (Spinner) dialog_layout.findViewById(R.id.opcionesColor);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.Colores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colores.setAdapter(adapter);


        db.setTitle("Nueva categoria");
        db.setPositiveButton("Añadir", null);
        final AlertDialog a = db.create();
        a.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = a.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nombreCategoria = nombre.getText().toString();
                        String colorCategoria = String.valueOf(colores.getSelectedItem());
                        if (true) {
                            Categoria categoria=new Categoria(nombreCategoria,colorCategoria,0);
                            insertarCategoria(categoria);

                            a.dismiss();
                        } else {
                            System.out.println("Entre algo mal");
                            if (nombreCategoria.equals("") || colorCategoria.equals("")) {
                                Toast.makeText(CategoriasActivity.this, "Ningún campo puede quedar vacío", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CategoriasActivity.this, "El color ya está siendo usado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        a.show();

    }

    private void insertarCategoria(Categoria categoria){
        DataManagerCategoria.insertarCategoria(categoria, new onInsertarListener() {
            @Override
            public void insertar(boolean insertado) {
                if (insertado){
                    Toast.makeText(CategoriasActivity.this, "Insertado Correctamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(CategoriasActivity.this, "Fallo en la base", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void modificarCategoria(Categoria categoria, String nombreAnterior){
        DataManagerCategoria.modificarDatosCategoria(nombreAnterior, categoria, new onModificarListener() {
            @Override
            public void modificar(boolean modificado) {
                if (modificado){
                    Toast.makeText(CategoriasActivity.this, "Modificado Correctamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(CategoriasActivity.this, "Fallo en la base", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<String> traerCategorias(Context context)  {
        ArrayList<String> colores = new ArrayList<>();
        DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                for (Object CategoriaObject:datos) {
                    Categoria categoria= (Categoria) CategoriaObject;
                    LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                    colores.add(categoria.getColor().toString());
                    Button button = new Button(context);
                    button.setLayoutParams(lp);
                    button.setText(categoria.getNombre()+" "+categoria.getCantidadTarjetas());
                    button.setBackgroundColor(0);
                    llBotonera.addView(button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!ModoModificar){

                                Intent irATarjetas = new Intent(CategoriasActivity.this, TarjetasActivity.class);
                                irATarjetas.putExtra("Color",categoria.getColor().getCodigo());
                                irATarjetas.putExtra("Nombre",categoria.getNombre());
                                startActivity(irATarjetas);
                            }
                            else{

                                //Las primeas cuatro lineas crean la alerta que te sale ya con el xml que preparamos adentro
                                LayoutInflater inflater = LayoutInflater.from(CategoriasActivity.this);
                                View dialog_layout = inflater.inflate(R.layout.activity_aniadir_categoria, null);
                                AlertDialog.Builder db = new AlertDialog.Builder(CategoriasActivity.this);
                                db.setView(dialog_layout);

                                //Empiezo a conseguir diferentes cosas de adentro de la alerta
                                EditText nombre = dialog_layout.findViewById(R.id.nombreCategoria);
                                nombre.setText(categoria.getNombre());
                                TextView colorRepetido = dialog_layout.findViewById(R.id.colorRepetido);

                                Spinner colores = (Spinner) dialog_layout.findViewById(R.id.opcionesColor);
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CategoriasActivity.this,
                                        R.array.Colores, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                colores.setAdapter(adapter);

                                db.setTitle("Modificar Categoria");
                                db.setPositiveButton("Modificar", null);
                                final AlertDialog a = db.create();
                                a.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        Button b = a.getButton(AlertDialog.BUTTON_POSITIVE);
                                        b.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String nombreCategoria = nombre.getText().toString();
                                                String colorCategoria = String.valueOf(colores.getSelectedItem());
                                                Categoria categoriaNueva= new Categoria(nombreCategoria,colorCategoria,0);
                                                DataManagerCategoria.traerIdCategoria(categoria.getNombre(), new onTraerDatoListener() {
                                                    @Override
                                                    public void traer(Object dato) {
                                                        modificarCategoria(categoriaNueva, (String) dato);
                                                        llBotonera.removeAllViews();
                                                        traerCategorias(context);
                                                    }
                                                });

                                                a.dismiss();
                                            }
                                        });
                                    }
                                });
                                a.show();
                            }

                        }
                    });
                }
            }
        });
        return colores;
    }

}

//CHARLAR CON TINCHO TEMA COLORES