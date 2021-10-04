package com.example.actividades;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import com.example.listeners.onEliminarListener;
import com.example.listeners.onInsertarListener;
import com.example.listeners.onModificarListener;
import com.example.listeners.onTraerDatoListener;
import com.example.listeners.onTraerDatosListener;
import com.example.objetos.CategoriaSinTarjetas;
import com.example.dataManagers.DataManagerCategoria;

import java.util.ArrayList;

public class CategoriasActivity extends AppCompatActivity {

    private boolean ModoModificar=false;
    private ArrayList<String> coloresUsados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this.getApplicationContext();
        coloresUsados = traerCategorias(context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        CardView aniadirCategoria = (CardView) findViewById(R.id.aniadirCategoría);
        aniadirCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("on click nueva categoria");
                aniadirCategoria(coloresUsados);
            }
        });
        CardView modificarCategoria= (CardView) findViewById(R.id.modificarCategoria);
        TextView textoEditarCategoria= findViewById(R.id.textoEditarCategoria);
        modificarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ModoModificar=!ModoModificar;
                if (ModoModificar){
                    textoEditarCategoria.setText("Salir del modo editable");
                }
                else{
                    textoEditarCategoria.setText("Editar categoria");
                }
            }
        });


    }

    public CardView crearCartaCategoria(int height, int width, int margin, String nombreCategoria, int color){

        CardView cardView = new CardView(this);
        LayoutParams params = new LayoutParams(width, height);
        params.setMargins(margin, margin, margin, margin);
        cardView.setRadius(50.0f);
        cardView.setCardBackgroundColor(color);
        cardView.setLayoutParams(params);

        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        constraintLayout.setLayoutParams(params);

        TextView categoria = new TextView(this);
        params = new LayoutParams((width*3)/5, height/2);
        categoria.setGravity(Gravity.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            categoria.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        categoria.setTypeface(ResourcesCompat.getFont(this,R.font.poertsen_one_regular));
        params.setMargins(height/4, 15,0,0);

        TextView texto = new TextView(this);
        params = new LayoutParams(width, height);
        texto.setTextSize(height/14);
        texto.setGravity(Gravity.CENTER);
        texto.setTypeface(ResourcesCompat.getFont(this, R.font.poertsen_one_regular));
        texto.setText(nombreCategoria);
        texto.setLayoutParams(params);

        ImageView cartitas = new ImageView(this);
        params = new LayoutParams((height)/5, (height)/5);
        cartitas.setImageDrawable(getResources().getDrawable(R.drawable.ic_cartas));
        cartitas.setColorFilter(getResources().getColor(R.color.white));

        TextView cantCartas = new TextView(this);
        params = new LayoutParams((height*7)/5, (height*7)/5);
        cantCartas.setTextColor(getResources().getColor(R.color.white));

        cardView.addView(cartitas);
        cardView.addView(texto);
        return cardView;



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
                        if (!coloresYaSeleccionados.contains(colorCategoria)) {
                            CategoriaSinTarjetas categoria=new CategoriaSinTarjetas(nombreCategoria,colorCategoria,0);
                            insertarCategoria(categoria);

                            a.dismiss();
                        } else {
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

    private void insertarCategoria(CategoriaSinTarjetas categoria){
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
        LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
        llBotonera.removeAllViews();
        coloresUsados=traerCategorias(this.getApplicationContext());
    }
    private void modificarCategoria(CategoriaSinTarjetas categoria, String nombreAnterior){
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
        ArrayList<String> coloresElegidos = new ArrayList<>();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int widthCarta = (width*4)/5;
        int heightCarta = (widthCarta*12)/35;
        int marginCarta = width/50;
        DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                for (Object CategoriaObject:datos) {
                    CategoriaSinTarjetas categoria= (CategoriaSinTarjetas) CategoriaObject;
                    System.out.println(categoria.getNombre());
                    System.out.println(categoria.getColor());
                    LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                    coloresElegidos.add(categoria.getColor().toString());
                    CardView cartaCategoria = crearCartaCategoria(heightCarta, widthCarta, marginCarta, categoria.getNombre()+" "+categoria.getCantidadTarjetas(), categoria.getColor().getCodigo());
                    llBotonera.addView(cartaCategoria);
                    cartaCategoria.setOnClickListener(new View.OnClickListener() {
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
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CategoriasActivity.this, R.array.Colores, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                colores.setAdapter(adapter);
                                db.setTitle("Modificar Categoria");
                                db.setPositiveButton("Aceptar", null);
                                db.setNegativeButton("Eliminar", null);
                                final AlertDialog a = db.create();
                                a.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        Button aceptarModificacion = a.getButton(AlertDialog.BUTTON_POSITIVE);
                                        Button eliminarCategoria = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                                        eliminarCategoria.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DataManagerCategoria.eliminarCategoria(categoria.getNombre(), new onEliminarListener() {
                                                    @Override
                                                    public void eliminar(boolean eliminado) {
                                                        if(eliminado) {
                                                            llBotonera.removeAllViews();
                                                            coloresUsados = traerCategorias(context);
                                                            a.dismiss();
                                                        }
                                                        else{
                                                            Toast.makeText(CategoriasActivity.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        aceptarModificacion.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Boolean modificoSoloNombre = false;
                                                String nombreCategoria = nombre.getText().toString();
                                                String colorCategoria = String.valueOf(colores.getSelectedItem());
                                                CategoriaSinTarjetas categoriaNueva = new CategoriaSinTarjetas(nombreCategoria, colorCategoria, 0);
                                                if (!colorCategoria.equals("")) {
                                                    if (coloresElegidos.contains(colorCategoria)) {
                                                        if (colorCategoria.equals(categoria.getColor().toString())) { // cambia solo el nombre
                                                            DataManagerCategoria.traerIdCategoria(categoria.getNombre(), new onTraerDatoListener() {
                                                                @Override
                                                                public void traer(Object dato) {
                                                                    modificarCategoria(categoriaNueva, (String) dato);
                                                                    llBotonera.removeAllViews();
                                                                    coloresUsados = traerCategorias(context);
                                                                }
                                                            });
                                                            modificoSoloNombre = true;
                                                        }
                                                        if (!modificoSoloNombre) {
                                                            Toast.makeText(CategoriasActivity.this, "Color ya elegido, intente con otro", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        DataManagerCategoria.traerIdCategoria(categoria.getNombre(), new onTraerDatoListener() {
                                                            @Override
                                                            public void traer(Object dato) {
                                                                modificarCategoria(categoriaNueva, (String) dato);
                                                                llBotonera.removeAllViews();
                                                                coloresUsados = traerCategorias(context);
                                                            }
                                                        });

                                                        a.dismiss();
                                                    }

                                                }
                                                else{
                                                    Toast.makeText(CategoriasActivity.this, "Ingrese el color de la categoria", Toast.LENGTH_SHORT).show();

                                                }
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
        return coloresElegidos;
    }

}

//CHARLAR CON TINCHO TEMA COLORES