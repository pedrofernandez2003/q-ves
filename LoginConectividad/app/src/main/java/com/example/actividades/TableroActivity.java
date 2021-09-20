package com.example.actividades;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.example.objetos.Casillero;
import com.example.objetos.Categoria;
import com.example.objetos.Tarjeta;
import com.example.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class TableroActivity extends AppCompatActivity {
    private LinearLayout verCartas;
    private Tarjeta tarjetaElegida;
    private ArrayList<Casillero> casilleros= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero_creable);
        verCartas = findViewById(R.id.verCartas);




        //ArrayList<Casillero> casilleros=GameContext.getPartidaActual().getCasilleros();
        //ArrayList<Categoria> categorias=GameContext.getJuego().getPlantilla().getCategorias();
        //HashSet<Tarjeta> tarjetasHashSet=GameContext.getEquipo().getTarjetas()
        ArrayList<Categoria> categorias= new ArrayList<>();
        HashSet<Tarjeta> tarjetasHashSet= new HashSet<>();
        ArrayList<Tarjeta> ejemploParaRellenar= new ArrayList<>();

        Tarjeta tarjeta1=new Tarjeta("prueba1","probandoooooo","categoria1");
        Tarjeta tarjeta2=new Tarjeta("prueba2","probandoooooo","categoria1");
        Tarjeta tarjeta3=new Tarjeta("prueba3","probandoooooo","categoria2");
        Tarjeta tarjeta4=new Tarjeta("prueba4","probandoooooo","categoria2");
        Tarjeta tarjeta5=new Tarjeta("prueba5","probandoooooo","categoria3");
        Tarjeta tarjeta6=new Tarjeta("prueba6","probandoooooo","categoria3");


        Categoria categoria1= new Categoria("categoria1","AMARILLO",3,ejemploParaRellenar);
        Categoria categoria2= new Categoria("categoria2","AZUL",3,ejemploParaRellenar);
        Categoria categoria3= new Categoria("categoria3","VERDE",3,ejemploParaRellenar);

        categorias.add(categoria1);
        categorias.add(categoria2);
        categorias.add(categoria3);

        tarjetasHashSet.add(tarjeta1);
        tarjetasHashSet.add(tarjeta2);
        tarjetasHashSet.add(tarjeta3);
        tarjetasHashSet.add(tarjeta4);
        tarjetasHashSet.add(tarjeta5);
        tarjetasHashSet.add(tarjeta6);
        tarjetasHashSet.add(tarjeta6);

        Casillero casillero1=new Casillero(categoria1);
        Casillero casillero2=new Casillero(categoria2);
        Casillero casillero3=new Casillero(categoria3);

        casilleros.add(casillero1);
        casilleros.add(casillero2);
        casilleros.add(casillero3);

        int item = new Random().nextInt(tarjetasHashSet.size());
        System.out.println("Random: "+item);

        verCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater inflater = LayoutInflater.from(TableroActivity.this);
                View dialog_layout = inflater.inflate(R.layout.ver_cartas, null);
                AlertDialog.Builder db = new AlertDialog.Builder(TableroActivity.this);
                db.setView(dialog_layout);
                LinearLayout contenedorCartas=(LinearLayout) dialog_layout.findViewById(R.id.contenedorCartas);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int widthCarta = (width*5)/40;
                int heightCarta = (widthCarta*14)/10;
                int marginCarta = width/100;

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

                    CardView carta = crearTarjeta(widthCarta, heightCarta, marginCarta, color, nombreCategoria,tarjetaContenido,tarjetaYapa);
                    contenedorCartas.addView(carta);

                    carta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tarjetaElegida=tarjetaARevisar;
                            System.out.println(tarjetaARevisar.getContenido());
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Seleccionaste esa carta", Snackbar.LENGTH_SHORT);
                            View snackView = snack.getView();
                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackView.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            snackView.setLayoutParams(params);
                            snack.show();

                            //Drawable aaa=findViewById(R.drawable.border);
                            //carta.setBackground(aaa);
                            //cuando tenga las tarjetas correctas hacer esto de "Seleccionarlas"

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





            }
        });
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
                int heightCarta = (widthCarta*14)/10;
                int marginCarta = width/100;
                int color=casillero.getCategoria().getColor().getCodigo();
                String nombreCategoria=casillero.getCategoria().getNombre();
                String tarjetaContenido=tarjetaElegida.getContenido();
                String tarjetaYapa=tarjetaElegida.getYapa();

                CardView carta = crearTarjeta(widthCarta, heightCarta, marginCarta, color, nombreCategoria,tarjetaContenido,tarjetaYapa);

                prueba.addView(carta);

            }
            else{

            }
        }


        /*ArrayList<TextView> nombreCategoriasEnTablero=conseguirTextViews();

        for (TextView nombreCategoria:nombreCategoriasEnTablero) {
            System.out.println("Hola");
            if (tarjetaElegida.getCategoria().equals(nombreCategoria.getText())){
                String prueba="Meti carta de esto";
                nombreCategoria.setText(prueba);
            }
        }*/

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

    public CardView crearTarjeta(int width, int height, int margin, int color, String categoria, String contenido, String yapaContenido){

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

}
