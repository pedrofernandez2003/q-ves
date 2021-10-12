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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;

import com.example.listeners.onEliminarListener;
import com.example.listeners.onInsertarListener;
import com.example.listeners.onModificarListener;
import com.example.listeners.onTraerDatoListener;
import com.example.listeners.onTraerDatosListener;
import com.example.objetos.CategoriaSinTarjetas;
import com.example.dataManagers.DataManagerCategoria;
import com.example.objetos.Usuario;

import java.util.ArrayList;

public class CategoriasActivity extends AppCompatActivity {

    private boolean ModoModificar=false;
    private ArrayList<CategoriaSinTarjetas> categoriasUsadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this.getApplicationContext();
        traerCategorias(context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        CardView aniadirCategoria = (CardView) findViewById(R.id.aniadirCategoría);
        aniadirCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("on click nueva categoria");
                aniadirCategoria();
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleOnBackPressed() {//que apague el hotspot y despues vaya para atras
                Intent intent=new Intent(CategoriasActivity.this,AdminElementosActivity.class);
                startActivity(intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CardView crearCartaCategoria(int height, int width, int margin, String nombreCategoria, int numCartas, int color){

        CardView cardView = new CardView(this);
        LayoutParams params = new LayoutParams(width, height);
        params.setMargins(margin, margin, margin, margin);
        cardView.setRadius(50.0f);
        cardView.setCardBackgroundColor(color);
        cardView.setLayoutParams(params);

        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        constraintLayout.setLayoutParams(params);
        constraintLayout.setId(ViewCompat.generateViewId());
        cardView.addView(constraintLayout);

        TextView categoria = new TextView(this);
        params = new LayoutParams((width*3)/5, height/2);
        categoria.setGravity(Gravity.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            categoria.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        params.setMargins(0, 15,0,0);
        categoria.setGravity(Gravity.CENTER);
        categoria.setText(nombreCategoria);
        categoria.setTextColor(getResources().getColor(R.color.white));
        categoria.setTypeface(getResources().getFont(R.font.poertsen_one_regular));
        categoria.setLayoutParams(params);
        categoria.setId(ViewCompat.generateViewId());
        constraintLayout.addView(categoria);

        ImageView cartitas = new ImageView(this);
        params = new LayoutParams((height)/3, (height)/3);
        cartitas.setLayoutParams(params);
        cartitas.setImageDrawable(getResources().getDrawable(R.drawable.ic_cartas));
        cartitas.setColorFilter(getResources().getColor(R.color.white));
        cartitas.setId(ViewCompat.generateViewId());
        constraintLayout.addView(cartitas);

        TextView cantCartas = new TextView(this);
        params = new LayoutParams((height)/2, (height)/2);
        params.setMargins(0, 0, width/10, 0);
        cantCartas.setTextColor(getResources().getColor(R.color.white));
        cantCartas.setTypeface(getResources().getFont(R.font.poertsen_one_regular));
        cantCartas.setText(""+numCartas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cantCartas.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        cantCartas.setLayoutParams(params);
        cantCartas.setId(ViewCompat.generateViewId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            categoria.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        constraintLayout.addView(cantCartas);

        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        set.connect(categoria.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        set.connect(categoria.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(categoria.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT);
        set.connect(categoria.getId(), ConstraintSet.RIGHT, cartitas.getId(), ConstraintSet.LEFT);

        set.connect(cartitas.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        set.connect(cartitas.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(cartitas.getId(), ConstraintSet.RIGHT, cantCartas.getId(), ConstraintSet.LEFT);
//        set.connect(cartitas.getId(), ConstraintSet.LEFT, categoria.getId(), ConstraintSet.RIGHT);
//
        set.connect(cantCartas.getId(), ConstraintSet.TOP, cartitas.getId(), ConstraintSet.TOP);
        set.connect(cantCartas.getId(), ConstraintSet.BOTTOM, cartitas.getId(), ConstraintSet.BOTTOM);
        set.connect(cantCartas.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT);

        set.applyTo(constraintLayout);

        return cardView;



    }

    //modificar
    public void aniadirCategoria() {

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

                        Boolean sePuedeCrear=true;
                        String nombreCategoria = nombre.getText().toString();
                        String colorCategoria = String.valueOf(colores.getSelectedItem());

                        if (!colorCategoria.equals("")) {
                            for (CategoriaSinTarjetas categoriaAComparar:categoriasUsadas) {
                                    if (categoriaAComparar.getColor().toString().equals(colorCategoria) || categoriaAComparar.getNombre().equals(nombreCategoria)){
                                        sePuedeCrear=false;
                                        Toast.makeText(CategoriasActivity.this, "Color o nombre ya elegido, intente con otro", Toast.LENGTH_SHORT).show();
                                    }
                            }
                        }
                        if (sePuedeCrear){
                            CategoriaSinTarjetas categoriaNueva = new CategoriaSinTarjetas(nombreCategoria, colorCategoria, 0);
                            System.out.println("Se puede crear");
                            CategoriaSinTarjetas categoria=new CategoriaSinTarjetas(nombreCategoria,colorCategoria,0);
                            insertarCategoria(categoria);
                            a.dismiss();

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
        traerCategorias(this.getApplicationContext());
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

    private void traerCategorias(Context context)  {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int widthCarta = (width*4)/5;
        int heightCarta = (widthCarta*12)/35;
        int marginCarta = width/50;
        categoriasUsadas=new ArrayList<>();
        DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                for (Object CategoriaObject:datos) {
                    CategoriaSinTarjetas categoria= (CategoriaSinTarjetas) CategoriaObject;
                    categoriasUsadas.add(categoria);

                    LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                    CardView cartaCategoria = crearCartaCategoria(heightCarta, widthCarta, marginCarta, categoria.getNombre(), categoria.getCantidadTarjetas(), categoria.getColor().getCodigo());
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
                                colores.setSelection(adapter.getPosition(categoria.getColor().name()));

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
                                                            traerCategorias(context);
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
                                                Boolean sePuedeModificar=true;
                                                String nombreCategoria = nombre.getText().toString();
                                                String colorCategoria = String.valueOf(colores.getSelectedItem());
                                                CategoriaSinTarjetas categoriaNueva = new CategoriaSinTarjetas(nombreCategoria, colorCategoria, 0);
                                                if (!colorCategoria.equals("")) {
                                                    for (CategoriaSinTarjetas categoriaAComparar:categoriasUsadas) {
                                                        if (categoria!=categoriaAComparar){
                                                            if (categoriaAComparar.getColor().toString().equals(colorCategoria) || categoriaAComparar.getNombre().equals(nombreCategoria)){
                                                                sePuedeModificar=false;
                                                                Toast.makeText(CategoriasActivity.this, "Color o nombre ya elegido, intente con otro", Toast.LENGTH_SHORT).show();
                                                            }

                                                            }
                                                        }
                                                    }
                                                if (sePuedeModificar){
                                                    System.out.println("Se puede modificar");
                                                    DataManagerCategoria.traerIdCategoria(categoria.getNombre(), new onTraerDatoListener() {
                                                        @Override
                                                        public void traer(Object dato) {
                                                            modificarCategoria(categoriaNueva, (String) dato);
                                                            llBotonera.removeAllViews();
                                                            traerCategorias(context);
                                                        }
                                                    });
                                                    }
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
    }

}

