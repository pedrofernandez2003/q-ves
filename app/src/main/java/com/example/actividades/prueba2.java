package com.example.actividades;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.example.objetos.Casillero;
import com.example.objetos.Categoria;
import com.example.objetos.GameContext;
import com.example.objetos.Mensaje;
import com.example.objetos.Tarjeta;
import com.example.R;
import com.example.objetos.manejoSockets.Write;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class prueba2 extends AppCompatActivity {
    private LinearLayout verCartas;
    private Tarjeta tarjetaElegida;
    private ArrayList<Casillero> casilleros= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero_template);
        verCartas = findViewById(R.id.verCartas);

        //ArrayList<Casillero> casilleros=GameContext.getPartidaActual().getCasilleros();
        //ArrayList<Categoria> categorias=GameContext.getJuego().getPlantilla().getCategorias();
        //HashSet<Tarjeta> tarjetasHashSet=GameContext.getEquipo().getTarjetas()
        ArrayList<Categoria> categorias= new ArrayList<>();
        HashSet<Tarjeta> tarjetasHashSet= new HashSet<>();
        ArrayList<Tarjeta> ejemploParaRellenar= new ArrayList<>();

        Tarjeta tarjeta1=new Tarjeta("Sufre mucho cuando blablabla ","probandoooooo 12312312312312312 12312312 123123 1231231 123123 1231 123 1231231 23 12312 312312 312 312 312 312 3123","Calle");
        Tarjeta tarjeta2=new Tarjeta("Besos por celular","probandoooooo 12312312312312312 12312312 123123 1231231 123123 1231 123 1231231 23 12312 312312 312 312 312 312 3123","Calle");
        Tarjeta tarjeta3=new Tarjeta("Las momias de este amor","probandoooooo 12312312312312312 12312312 123123 1231231 123123 1231 123 1231231 23 12312 312312 312 312 312 312 3123","Sentimietos");
        Tarjeta tarjeta4=new Tarjeta("Remontar el barrilete  en esta tempestad","probandoooooo 12312312312312312 12312312 123123 1231231 123123 1231 123 1231231 23 12312 312312 312 312 312 312 3123","Sentimietos");
        Tarjeta tarjeta5=new Tarjeta("Solo habra entender que ayer no es hoy","probandoooooo 12312312312312312 12312312 123123 1231231 123123 1231 123 1231231 23 12312 312312 312 312 312 312 3123","Sexualidad");
        Tarjeta tarjeta6=new Tarjeta("que hoy es hoy","probandoooooo 12312312312312312 12312312 123123 1231231 123123 1231 123 1231231 23 12312 312312 312 312 312 312 3123","Sexualidad");


        Categoria categoria1= new Categoria("Calle","AMARILLO",3,ejemploParaRellenar);
        Categoria categoria2= new Categoria("Sentimietos","AZUL",3,ejemploParaRellenar);
        Categoria categoria3= new Categoria("Sexualidad","VERDE",3,ejemploParaRellenar);

        categorias.add(categoria1);
        categorias.add(categoria2);
        categorias.add(categoria3);

//        tarjetasHashSet.add(tarjeta1);
//        tarjetasHashSet.add(tarjeta2);
//        tarjetasHashSet.add(tarjeta3);
//        tarjetasHashSet.add(tarjeta4);
//        tarjetasHashSet.add(tarjeta5);
//        tarjetasHashSet.add(tarjeta6);
//        tarjetasHashSet.add(tarjeta6);

        Casillero casillero1=new Casillero(categoria1);
        Casillero casillero2=new Casillero(categoria2);
        Casillero casillero3=new Casillero(categoria3);

        casilleros.add(casillero1);
        casilleros.add(casillero2);
        casilleros.add(casillero3);


        verCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSystemUI();
                LayoutInflater inflater = LayoutInflater.from(prueba2.this);
                View dialog_layout = inflater.inflate(R.layout.ver_cartas, null);
                AlertDialog.Builder db = new AlertDialog.Builder(prueba2.this);
                db.setView(dialog_layout);
                LinearLayout contenedorCartas=(LinearLayout) dialog_layout.findViewById(R.id.contenedorCartas);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int widthDialog = (width*9)/10;
                int height = displayMetrics.heightPixels;
                int heightDialog = (height*9)/10;
                int heightCarta = (heightDialog*8)/10;
                int widthCarta = (heightCarta*16)/20;
                int marginCarta = width/60;

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (heightDialog*8)/10);
                contenedorCartas.setLayoutParams(params);

                TextView noCartas = new TextView(prueba2.this);
                noCartas.setText("No hay mas cartas en tu mazo");



                for (Tarjeta tarjetaARevisar:tarjetasHashSet) {

                    String nombreCategoria=tarjetaARevisar.getCategoria();
                    String tarjetaContenido=tarjetaARevisar.getContenido();
                    String tarjetaYapa=tarjetaARevisar.getYapa();
                    int color=0; //si o si parece que tenia que iniciarlizarlo

                    for (int j=0; j < categorias.size(); j++){
                        Categoria categoriaARevisar=categorias.get(j);
                        if (categoriaARevisar.getNombre().equals(tarjetaARevisar.getCategoria())){
                            color=categoriaARevisar.getColor().getCodigo();
                        }
                    }

                    MaterialCardView carta = crearTarjeta(widthCarta, heightCarta, marginCarta, color, nombreCategoria,tarjetaContenido,tarjetaYapa);
                    contenedorCartas.addView(carta);

                    carta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tarjetaElegida=tarjetaARevisar;
                            System.out.println(tarjetaARevisar.getContenido());
                            cambiarColorBordes(contenedorCartas);
                            carta.setStrokeColor(getResources().getColor(R.color.green_light));
                            carta.setStrokeWidth(7);

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
                a.getWindow().setLayout((width*9)/10, (height*19)/20);





            }
        });



        CardView prueba = (CardView) findViewById(R.id.cardView2);
        prueba.removeAllViews();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int widthCarta = width / 7;
        int heightCarta = widthCarta;
        int marginCarta = width / 45;
        int color = categoria1.getColor().getCodigo();
        String nombreCategoria = categoria1.getNombre();
        String tarjetaContenido = tarjeta2.getContenido();
        String tarjetaYapa = tarjeta2.getYapa();
        if (tarjetaYapa.length()>15){
            tarjetaYapa="...";
        }

        CardView carta = crearTarjeta2(widthCarta, heightCarta, marginCarta, color, nombreCategoria, tarjetaContenido, tarjetaYapa,tarjeta2.getYapa());

        prueba.addView(carta);
    }

    public void cambiarColorBordes(LinearLayout contenedorCartas){
        int childCount = contenedorCartas.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if(contenedorCartas.getChildAt(i) instanceof  MaterialCardView){
                MaterialCardView cartaACambiar = ((MaterialCardView) contenedorCartas.getChildAt(i));
                cartaACambiar.setStrokeWidth(0);
                cartaACambiar.invalidate();
            }
        }
    }

    private void hideSystemUI() {//para poner full screen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void insertarTarjetaEnTablero(){
        // IDEA: deberia buscar entre todos los text para ver cual corresponde
        // con la categoria y cambiar algo como para decir "Estoy aca :D"


        for (Casillero casillero:casilleros) {
            if (casillero.getTarjeta() == null && casillero.getCategoria().getNombre().equals(tarjetaElegida.getCategoria())){
                casillero.setTarjeta(tarjetaElegida);


                System.out.println(findViewById(R.id.cardView).getId());
                System.out.println(findViewById(R.id.cardView2).getId());
                System.out.println(findViewById(R.id.cardView3).getId());
                System.out.println(findViewById(R.id.cardView4).getId());
                System.out.println(findViewById(R.id.cardView5).getId());
                System.out.println(findViewById(R.id.cardView6).getId());
                System.out.println(findViewById(R.id.cardView7).getId());
                System.out.println(findViewById(R.id.cardView8).getId());
                System.out.println(findViewById(R.id.cardView9).getId());
                System.out.println(findViewById(R.id.cardView10).getId());

                CardView prueba= (CardView) findViewById(R.id.cardView2);//casillero.getId()
                prueba.removeAllViews();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int widthCarta = (width*4)/40;
                int heightCarta = (widthCarta*16)/11;
                int marginCarta = width/100;
                int color=casillero.getCategoria().getColor().getCodigo();
                String nombreCategoria=casillero.getCategoria().getNombre();
                String tarjetaContenido=tarjetaElegida.getContenido();
                String tarjetaYapa=tarjetaElegida.getYapa();

                CardView carta = crearTarjeta(widthCarta, heightCarta, marginCarta, color, nombreCategoria,tarjetaContenido,tarjetaYapa);

                prueba.addView(carta);

            }

        }




    }

    //total despues esto va en Jugar asi que esto no importa
    public ArrayList<TextView> conseguirTextViews() {
        ArrayList<TextView> espaciosTexto = new ArrayList<>();
        espaciosTexto.add(findViewById(R.id.icon_name_1));
        espaciosTexto.add(findViewById(R.id.icon_name_2));
        espaciosTexto.add(findViewById(R.id.icon_name_3));
        espaciosTexto.add(findViewById(R.id.icon_name_4));
        espaciosTexto.add(findViewById(R.id.icon_name_5));
        espaciosTexto.add(findViewById(R.id.icon_name_6));
        espaciosTexto.add(findViewById(R.id.icon_name_7));
        espaciosTexto.add(findViewById(R.id.icon_name_8));
        espaciosTexto.add(findViewById(R.id.icon_name_9));
        espaciosTexto.add(findViewById(R.id.icon_name_10));
        return espaciosTexto;
    }

    public MaterialCardView crearTarjeta(int width, int height, int margin, int color, String categoria, String contenido, String yapaContenido){

        // Crear la base
        MaterialCardView carta = new MaterialCardView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.setMargins(5,0,5,0);
        carta.setLayoutParams(params);
        carta.setBackgroundColor(-1644568);
        carta.setRadius(0);

        // Crear el constraint layout
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        params = new FrameLayout.LayoutParams(width, height);
        constraintLayout.setLayoutParams(params);
        constraintLayout.setId(ViewCompat.generateViewId());
        carta.addView(constraintLayout);



        // Crear el borde de arriba
        CardView bordeTop = new CardView(this);
        params = new FrameLayout.LayoutParams(width, height/8);
        bordeTop.setLayoutParams(params);
        bordeTop.setBackgroundColor(color);
        bordeTop.setId(ViewCompat.generateViewId());
        constraintLayout.addView(bordeTop);


        // Crear el borde de abajo
        CardView bordeBot = new CardView(this);
        params = new FrameLayout.LayoutParams(width, (height*3)/50);
        bordeBot.setLayoutParams(params);
        bordeBot.setBackgroundColor(color);
        bordeBot.setId(ViewCompat.generateViewId());
        constraintLayout.addView(bordeBot);


        //TextView "Yapa para discutir en grupo"
        TextView yapaParaDiscutirEnGrupo = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        yapaParaDiscutirEnGrupo.setLayoutParams(params);
        yapaParaDiscutirEnGrupo.setText(getResources().getString(R.string.mensaje_yapa));
        yapaParaDiscutirEnGrupo.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/16));
        yapaParaDiscutirEnGrupo.setTypeface(Typeface.DEFAULT_BOLD);
        yapaParaDiscutirEnGrupo.setId(ViewCompat.generateViewId());
        yapaParaDiscutirEnGrupo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        yapaParaDiscutirEnGrupo.setTextColor(getResources().getColor(R.color.texto_primary));
        constraintLayout.addView(yapaParaDiscutirEnGrupo);



        //Crear el textview con la categoria
        TextView textoCategoria = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textoCategoria.setLayoutParams(params);
        textoCategoria.setTextColor(getResources().getColor(R.color.texto_primary));
        textoCategoria.setText(categoria);
        textoCategoria.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/10);
        textoCategoria.setTypeface(ResourcesCompat.getFont(this, R.font.poertsen_one_regular));
        textoCategoria.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoCategoria);

        //Crear el textview para el contenido
        TextView textoContenido = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(7, 7, 7, 7);
        textoContenido.setLayoutParams(params);
        textoContenido.setText(contenido);
        textoContenido.setTextColor(getResources().getColor(R.color.texto_primary));
        textoContenido.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/17));
        textoContenido.setGravity(Gravity.CENTER);
        textoContenido.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoContenido);

        //Crear la yapa
        TextView yapa = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        yapa.setLayoutParams(params);
        yapa.setText(yapaContenido);
        yapa.setTextColor(getResources().getColor(R.color.texto_primary));
        yapa.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/17));
        yapa.setGravity(Gravity.CENTER);
        yapa.setId(ViewCompat.generateViewId());
        constraintLayout.addView(yapa);

        //Constraints
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        set.connect(bordeTop.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        set.connect(bordeBot.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(textoCategoria.getId(), ConstraintSet.TOP, bordeTop.getId(), ConstraintSet.BOTTOM);
        set.connect(textoCategoria.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoCategoria.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoContenido.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.TOP, textoCategoria.getId(), ConstraintSet.BOTTOM,height/50);
        set.connect(yapa.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(yapa.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(yapa.getId(), ConstraintSet.BOTTOM, bordeBot.getId(), ConstraintSet.TOP);
        set.connect(yapaParaDiscutirEnGrupo.getId(), ConstraintSet.BOTTOM, yapa.getId(), ConstraintSet.TOP);
        set.connect(yapaParaDiscutirEnGrupo.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT);
        set.connect(yapaParaDiscutirEnGrupo.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT);
        set.applyTo(constraintLayout);



        return carta;

    }

    public ConstraintLayout crearConstraintTarjeta(int width, int height, int margin, int color, String categoria, String contenido, String yapaContenido){

        // Crear la base
        CardView carta = new CardView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.setMargins(margin,margin,margin,margin);
        carta.setLayoutParams(params);
        carta.setBackgroundColor(-1644568);

        // Crear el constraint layout
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        params = new FrameLayout.LayoutParams(width, height);
        constraintLayout.setLayoutParams(params);
        constraintLayout.setId(ViewCompat.generateViewId());
        constraintLayout.setBackgroundColor(-1644568);
        carta.addView(constraintLayout);

        // Crear el borde de arriba
        CardView bordeTop = new CardView(this);
        params = new FrameLayout.LayoutParams(width, height/8);
        bordeTop.setLayoutParams(params);
        bordeTop.setBackgroundColor(color);
        bordeTop.setId(ViewCompat.generateViewId());
        constraintLayout.addView(bordeTop);


        // Crear el borde de abajo
        CardView bordeBot = new CardView(this);
        params = new FrameLayout.LayoutParams(width, (height*3)/50);
        bordeBot.setLayoutParams(params);
        bordeBot.setBackgroundColor(color);
        bordeBot.setId(ViewCompat.generateViewId());
        constraintLayout.addView(bordeBot);

        //Crear el textview con la categoria
        TextView textoCategoria = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textoCategoria.setLayoutParams(params);
        textoCategoria.setText(categoria);
        textoCategoria.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/10);
        textoCategoria.setTypeface(ResourcesCompat.getFont(this, R.font.hlsimple));
        textoCategoria.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoCategoria);

        //Crear el textview para el contenido
        TextView textoContenido = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        textoContenido.setLayoutParams(params);
        textoContenido.setText(contenido);
        textoContenido.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/15));
        textoContenido.setGravity(Gravity.CENTER);
        textoContenido.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoContenido);

        //Crear la yapa
        TextView yapa = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        yapa.setLayoutParams(params);
        yapa.setText(yapaContenido);
        yapa.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/20));
        yapa.setGravity(Gravity.CENTER);
        yapa.setId(ViewCompat.generateViewId());
        constraintLayout.addView(yapa);

        //Constraints
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        set.connect(bordeTop.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 0);
        set.connect(bordeBot.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(textoCategoria.getId(), ConstraintSet.TOP, bordeTop.getId(), ConstraintSet.BOTTOM);
        set.connect(textoCategoria.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoCategoria.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoContenido.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.TOP, textoCategoria.getId(), ConstraintSet.BOTTOM,height/50);
        set.connect(yapa.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(yapa.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(yapa.getId(), ConstraintSet.BOTTOM, bordeBot.getId(), ConstraintSet.TOP);
        set.applyTo(constraintLayout);

        return constraintLayout;

    }

    public CardView crearTarjeta2(int width2, int height, int margin, int color, String categoria, String contenido, String yapaContenido, String yapaPosta){
        int width=0;
        // Crear la base
        CardView carta = new CardView(this);
        width = findViewById(R.id.cardView).getWidth() - 20;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.setMargins( 0,margin, 0,margin);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        carta.setLayoutParams(params);
        carta.setBackgroundColor(-1644568);

        // Crear el constraint layout
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        params = new FrameLayout.LayoutParams(width, height);
        constraintLayout.setLayoutParams(params);
        constraintLayout.setId(ViewCompat.generateViewId());
        carta.addView(constraintLayout);

        //Crear el textview con la categoria
        TextView textoCategoria = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textoCategoria.setLayoutParams(params);
        textoCategoria.setText(categoria);
        textoCategoria.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/6);
        textoCategoria.setTypeface(ResourcesCompat.getFont(this, R.font.poertsen_one_regular));
        textoCategoria.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoCategoria);

        //Crear el textview para el contenido
        TextView textoContenido = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        textoContenido.setLayoutParams(params);
        textoContenido.setText(contenido);
        textoContenido.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/8));
        textoContenido.setGravity(Gravity.CENTER);
        textoContenido.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoContenido);

        //Crear la yapa
        TextView yapa = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        yapa.setLayoutParams(params);
        yapa.setText(yapaContenido);
        yapa.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/10));
        yapa.setGravity(Gravity.CENTER);
        yapa.setId(ViewCompat.generateViewId());
        constraintLayout.addView(yapa);
        if (yapa.getText().equals("...")){
            yapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(prueba2.this);
                    View dialog_layout = inflater.inflate(R.layout.prueba, null);
                    final Dialog dialog= new Dialog(prueba2.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    dialog.setContentView(dialog_layout);

                    MaterialCardView tarjeta=(MaterialCardView) dialog.findViewById(R.id.tarjeta);
                    TextView categoriaView = (TextView) dialog.findViewById(R.id.categoria);
                    TextView contenidoView = (TextView) dialog.findViewById(R.id.contenido);
                    TextView yapaView = (TextView) dialog.findViewById(R.id.yapa);
                    CardView parteAbajoView = (CardView) dialog.findViewById(R.id.parteAbajo);
                    CardView parteArribaView = (CardView) dialog.findViewById(R.id.parteArriba);

                    categoriaView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/5);
                    categoriaView.setText(categoria);
                    contenidoView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/6);
                    contenidoView.setText(contenido);
                    yapaView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/16)*2);
                    yapaView.setText(yapaPosta);

                    tarjeta.setCardBackgroundColor(-1644568);
                    categoriaView.setTextColor(color);
                    parteArribaView.setBackgroundColor(color);
                    parteAbajoView.setBackgroundColor(color);

                    MaterialCardView.LayoutParams params = new MaterialCardView.LayoutParams((width2*5)/2, height*3);
                    tarjeta.setLayoutParams(params);

                    dialog.show();
                }
            });
        }

        //Constraints
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        set.connect(textoCategoria.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP,0);
        set.connect(textoCategoria.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoCategoria.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoContenido.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.TOP, textoCategoria.getId(), ConstraintSet.BOTTOM,height/50);
        set.connect(yapa.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(yapa.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(yapa.getId(), ConstraintSet.BOTTOM,  constraintLayout.getId(), ConstraintSet.BOTTOM,0);
        set.applyTo(constraintLayout);

        return carta;

    }

    public CardView crearTarjetaAnular(int width, int height, int margin, int color, String categoria, String contenido, String yapaContenido){

        // Crear la base
        CardView carta = new CardView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.setMargins(margin,margin,margin,margin);
        carta.setLayoutParams(params);
        carta.setBackgroundColor(-1644568);

        // Crear el constraint layout
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        params = new FrameLayout.LayoutParams(width, height);
        constraintLayout.setLayoutParams(params);
        constraintLayout.setId(ViewCompat.generateViewId());
        carta.addView(constraintLayout);

        // Crear el borde de arriba
        CardView bordeTop = new CardView(this);
        params = new FrameLayout.LayoutParams(width, height/8);
        bordeTop.setLayoutParams(params);
        bordeTop.setBackgroundColor(color);
        bordeTop.setId(ViewCompat.generateViewId());
        constraintLayout.addView(bordeTop);


        // Crear el borde de abajo
        CardView bordeBot = new CardView(this);
        params = new FrameLayout.LayoutParams(width, (height*3)/50);
        bordeBot.setLayoutParams(params);
        bordeBot.setBackgroundColor(color);
        bordeBot.setId(ViewCompat.generateViewId());
        constraintLayout.addView(bordeBot);

        //Crear el textview con la categoria
        TextView textoCategoria = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textoCategoria.setLayoutParams(params);
        textoCategoria.setText(categoria);
        textoCategoria.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/6);
        textoCategoria.setTypeface(ResourcesCompat.getFont(this, R.font.poertsen_one_regular));
        textoCategoria.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoCategoria);

        //Crear el textview para el contenido
        TextView textoContenido = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        textoContenido.setLayoutParams(params);
        textoContenido.setText(contenido);
        textoContenido.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/8));
        textoContenido.setGravity(Gravity.CENTER);
        textoContenido.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoContenido);

        //Crear la yapa
        TextView yapa = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        yapa.setLayoutParams(params);
        yapa.setText(yapaContenido);
        yapa.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/10));
        yapa.setGravity(Gravity.CENTER);
        yapa.setId(ViewCompat.generateViewId());
        constraintLayout.addView(yapa);

        //Constraints
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        set.connect(bordeTop.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 0);
        set.connect(bordeBot.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(textoCategoria.getId(), ConstraintSet.TOP, bordeTop.getId(), ConstraintSet.BOTTOM);
        set.connect(textoCategoria.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoCategoria.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(textoContenido.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(textoContenido.getId(), ConstraintSet.TOP, textoCategoria.getId(), ConstraintSet.BOTTOM,height/50);
        set.connect(yapa.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        set.connect(yapa.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        set.connect(yapa.getId(), ConstraintSet.BOTTOM, bordeBot.getId(), ConstraintSet.TOP);
        set.applyTo(constraintLayout);

        return carta;

    }


}