package com.example.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.objetos.Casillero;
import com.example.objetos.Categoria;
import com.example.objetos.GameContext;
import com.example.objetos.Juego;
import com.example.objetos.Mensaje;
import com.example.objetos.Partida;
import com.example.objetos.Tarjeta;
import com.example.objetos.TarjetaSinCategoria;
import com.example.objetos.manejoSockets.Write;
import com.example.R;
import com.example.objetos.Plantilla;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class JugarActivity extends AppCompatActivity  {
    //    private GameContext context;
    private Juego juego;
    private Partida partida;
    private ArrayList<Casillero> casilleros;
    private ArrayList<Categoria> categorias;
    private HashSet<Tarjeta> tarjetasHashSet;
    private TextView turno, ronda;
    private Button botonAgarrarCarta, botonPasarTurno;
    private LinearLayout botonVerCartas,botonAnularCarta;
    private Boolean puedeAgarrarCarta=true;

    Context appContext=this;
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "turno":
                    puedeAgarrarCarta=true;
                    LayoutInflater inflater=getLayoutInflater();
                    View Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                    TextView text = (TextView) Layout.findViewById(R.id.toastTextView);
                    text.setText("Es tu turno!");
                    Toast toast= new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM,0,0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(Layout);
                    toast.show();
                    break;

                case "reiniciar":
                    intent=new Intent(appContext,JugarActivity.class);
                    startActivity(intent);
                    break;

                case "actualizar":
                    insertarTarjetaEnTablero();
                    break;

                case "ganador":
                    System.out.println("llega el ganador");
                    LayoutInflater inflater2 = LayoutInflater.from(JugarActivity.this);
                    View dialog_layout = inflater2.inflate(R.layout.ganador, null);
                    AlertDialog.Builder db = new AlertDialog.Builder(context);
                    db.setView(dialog_layout);
                    db.setTitle("Juego terminado");
                    db.setMessage("el ganador es: "+intent.getStringExtra("ganador"));
                    final AlertDialog a = db.create();
                    a.show();
                    break;

                case "notificarModerador":
                    boolean anuladoCorrectamente;
                    //Le sale el alertdialog y si pone que si el boolean es true, si no es false :D
                    ArrayList<String> datos=new ArrayList<>();
                    datos.add(GameContext.getTarjetaElegida().serializar());
                    //datos.add("{\"anuladoCorrectamente\": \""+anuladoCorrectamente+"\"}"); //como poner booleano, no se :)
                    Mensaje mensaje=new Mensaje("notificarModeradorSobreAnulacion",datos);
                    String msg=mensaje.serializar();
                    System.out.println("mensaje enviado "+msg);
                    Write escribir = new Write();
                    escribir.execute(msg, 0);
                    break;

                case "anularCarta":
                    // en los otros va a sacar la tarjeta del tablero con esta funcion :D sacarTarjetaDelTablero();
                    sacarTarjetaDelTablero();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero_creable);
        mostrarPlantillaEnXml(GameContext.getJuego().getPlantilla(), this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("turno");
        intentFilter.addAction("reiniciar");
        intentFilter.addAction("actualizar");
        intentFilter.addAction("ganador");
        registerReceiver(broadcastReceiver,intentFilter);
        juego= GameContext.getJuego();
        partida=GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1);
        ronda=findViewById(R.id.textView2);
        casilleros=GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros();
        categorias=GameContext.getJuego().getPlantilla().getCategorias();

        System.out.println("ronda: " +GameContext.getRonda());
        ronda.setText("Ronda: "+GameContext.getRonda()+"/"+GameContext.getJuego().getPartidas().size());
        System.out.println(GameContext.getHijos().size());
        if (GameContext.getServer()==null){//para que solo lo hagan los equipos, esta bien?
//            tarjetasHashSet=GameContext.getEquipo().getTarjetas();
            ArrayList<String> datos=new ArrayList<>();
            Mensaje mensaje=new Mensaje("jugarListo",datos);
            String msg=mensaje.serializar();
            Write escribir = new Write();
            escribir.execute(msg, 0);
        }
        botonAgarrarCarta=(Button)findViewById(R.id.agarrarCarta);
        botonPasarTurno=(Button)findViewById(R.id.pasar);
        botonVerCartas = findViewById(R.id.verCartas);
        botonAnularCarta = findViewById(R.id.anularCarta);
        botonAnularCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GameContext.getServer()==null && GameContext.isEsMiTurno()){
                    LayoutInflater inflater = LayoutInflater.from(JugarActivity.this);
                    View dialog_layout = inflater.inflate(R.layout.anular_carta, null);
                    AlertDialog.Builder db = new AlertDialog.Builder(getApplicationContext());
                    db.setView(dialog_layout);
                    db.setTitle("Anular Tarjeta");
                    db.setPositiveButton("Enviar propuesta", null);
                    db.setNegativeButton("Atras", null);
                    final AlertDialog a = db.create();

                    a.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button si = a.getButton(AlertDialog.BUTTON_POSITIVE);
                            Button no = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    a.dismiss();
                                }
                            });
                            si.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //MANDARLE AL MODERADOR ESTO DE QUE SE QUIERE DEBIR ESTE TARJETA, QUE ESTA EN GAMECONTEXT.GETTARJETAELEJIDA
                                    ArrayList<String> datos=new ArrayList<>();
                                    datos.add(GameContext.getTarjetaElegida().serializar());
                                    datos.add("{\"idJugador\": \""+GameContext.getEquipo().getNombre()+"\"}");
                                    Mensaje mensaje=new Mensaje("notificarModeradorSobreAnulacion",datos);
                                    String msg=mensaje.serializar();
                                    System.out.println("mensaje enviado "+msg);
                                    Write escribir = new Write();
                                    escribir.execute(msg, 0);

                                    a.dismiss();
                                }
                            });
                        }
                    });
                    a.show();
                }
            }
        });
        botonAgarrarCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GameContext.getServer()==null && GameContext.isEsMiTurno()){
                    if (puedeAgarrarCarta){
                        ArrayList<String> datos=new ArrayList<>();
                        datos.add("{\"idJugador\": \""+GameContext.getEquipo().getNombre()+"\"}");
                        Mensaje mensaje=new Mensaje("agarrarCarta",datos);
                        String msg=mensaje.serializar();
                        System.out.println("mensaje enviado "+msg);
                        Write escribir = new Write();
                        escribir.execute(msg, 0);
                        puedeAgarrarCarta=false;
                    }
                    else{
                        LayoutInflater inflater=getLayoutInflater();
                        View Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                        TextView text = (TextView) Layout.findViewById(R.id.toastTextView);
                        text.setText("No podes agarrar mas cartas");
                        Toast toast= new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM,0,0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(Layout);
                        toast.show();
                    }

                }
                else{
                    LayoutInflater inflater=getLayoutInflater();
                    View Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                    TextView text = (TextView) Layout.findViewById(R.id.toastTextView);
                    text.setText("No es tu turno");
                    Toast toast= new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM,0,0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(Layout);
                    toast.show();
                }
            }
        });
        botonPasarTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GameContext.isEsMiTurno()){
                    ArrayList<String> datos=new ArrayList<>();
                    Mensaje mensaje=new Mensaje("pasarTurno",datos);
                    String msg=mensaje.serializar();
                    System.out.println("mensaje enviado "+msg);
                    Write escribir = new Write();
                    escribir.execute(msg, 0);
                    GameContext.setEsMiTurno(false);
                    //turno.setVisibility(View.INVISIBLE);
                }
                else{
                    LayoutInflater inflater=getLayoutInflater();
                    View Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                    TextView text = (TextView) Layout.findViewById(R.id.toastTextView);
                    text.setText("No es tu turno");
                    Toast toast= new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM,0,0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(Layout);
                    toast.show();

                    System.out.println("no es tu turno");//poner un toast que diga no es tu turno
                }
            }
        });
        botonVerCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GameContext.getServer()==null){

                    LayoutInflater inflater = LayoutInflater.from(JugarActivity.this);
                    View dialog_layout = inflater.inflate(R.layout.ver_cartas, null);
                    AlertDialog.Builder db = new AlertDialog.Builder(JugarActivity.this);
                    db.setView(dialog_layout);
                    LinearLayout contenedorCartas=(LinearLayout) dialog_layout.findViewById(R.id.contenedorCartas);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;
                    int widthCarta = (width*5)/40;
                    int heightCarta = (widthCarta*14)/10;
                    int marginCarta = width/100;

                    tarjetasHashSet=GameContext.getEquipo().getTarjetas();

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
                                GameContext.setTarjetaElegida(tarjetaARevisar);
                                System.out.println("Tarjeta seleccionda: "+tarjetaARevisar.getContenido());
                                Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Seleccionaste esa carta", Snackbar.LENGTH_SHORT);
                                View snackView = snack.getView();
                                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackView.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                snackView.setLayoutParams(params);
                                snack.show();

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
                                    if (GameContext.isEsMiTurno()){
                                        if (insertarTarjetaEnTablero()){
                                            ArrayList<String> datos=new ArrayList<>();
                                            datos.add(GameContext.getTarjetaElegida().serializar());//falta sacar la carta del mazo
                                            GameContext.getEquipo().getTarjetas().remove(GameContext.getTarjetaElegida());
                                            datos.add("{\"idJugador\": \""+GameContext.getEquipo().getNombre()+"\"}");
                                            Mensaje mensaje=new Mensaje("jugada",datos);
                                            String msg=mensaje.serializar();
                                            System.out.println("mensaje enviado "+msg);
                                            Write escribir = new Write();
                                            escribir.execute(msg, 0);
                                            GameContext.setEsMiTurno(false);
                                            //turno.setVisibility(View.INVISIBLE);
                                            System.out.println("tarjetas restantes "+GameContext.getEquipo().getTarjetas().size());
                                        }
                                        else{
                                            System.out.println("Casillero ocupado");
                                            LayoutInflater inflater=getLayoutInflater();
                                            View Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                                            TextView text = (TextView) Layout.findViewById(R.id.toastTextView);
                                            text.setText("Casillero ocupado");
                                            Toast toast= new Toast(getApplicationContext());
                                            toast.setGravity(Gravity.BOTTOM,0,0);
                                            toast.setDuration(Toast.LENGTH_LONG);
                                            toast.setView(Layout);
                                            toast.show();
                                        }
                                    }
                                    else{

                                        LayoutInflater inflater=getLayoutInflater();
                                        View Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                                        TextView text = (TextView) Layout.findViewById(R.id.toastTextView);
                                        text.setText("No es tu turno");
                                        Toast toast= new Toast(getApplicationContext());
                                        toast.setGravity(Gravity.BOTTOM,0,0);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setView(Layout);
                                        toast.show();

                                        System.out.println("no es tu turno");//poner un toast que diga no es tu turno
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

    public boolean insertarTarjetaEnTablero() {

        boolean insertoLaTarjeta=false;

        for (Casillero casillero : casilleros) {
            if (casillero.getTarjeta() == null && casillero.getCategoria().getNombre().equals(GameContext.getTarjetaElegida().getCategoria())) {
                casillero.setTarjeta(GameContext.getTarjetaElegida());

                CardView prueba = (CardView) findViewById(casillero.getId());
                prueba.removeAllViews();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int widthCarta = (width * 4) / 40;
                int heightCarta = (widthCarta * 14) / 10;
                int marginCarta = width / 100;
                int color = casillero.getCategoria().getColor().getCodigo();
                String nombreCategoria = casillero.getCategoria().getNombre();
                String tarjetaContenido = GameContext.getTarjetaElegida().getContenido();
                String tarjetaYapa = GameContext.getTarjetaElegida().getYapa();

                CardView carta = crearTarjeta(widthCarta, heightCarta, marginCarta, color, nombreCategoria, tarjetaContenido, tarjetaYapa);

                prueba.addView(carta);

                insertoLaTarjeta=true;

            }

        }
        return insertoLaTarjeta;
    }

    public void sacarTarjetaDelTablero() {
        GameContext.getTarjetaElegida().getCategoria();
        for (Casillero casillero:casilleros) {
            if (casillero.getCategoria().getNombre().equals(GameContext.getTarjetaElegida().getCategoria())) {

                CardView prueba = (CardView) findViewById(casillero.getId());
                prueba.removeAllViews();

                //aca hacer parte diseño para que sea como antes
                LinearLayout categoria;
                //prueba.addView(categoria);
            }
        }
    }
//    public void conseguirCartaDelMazoYPonerlaEnTusCartas() {
//        HashSet<Tarjeta> misTarjetas=GameContext.getEquipo().getTarjetas();
//        HashSet<Tarjeta> mazo=GameContext.getJuego().getMazo();
//
//        int size = mazo.size();
//        int item = new Random().nextInt(size);
//        int i = 0;
//
//        for (Tarjeta obj : mazo) {
//            if (i == item) {
//                misTarjetas.add(obj);
//                mazo.remove(obj);
//                break;
//            }
//            i++;
//        }
//
//        GameContext.getEquipo().setTarjetas(misTarjetas);
//        GameContext.getJuego().setMazo(mazo);
//
//    }


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

    public void mostrarPlantillaEnXml(Plantilla plantilla, Context context) {
        ArrayList<CardView> espacioCartas = conseguirCardViews();
        ArrayList<TextView> espaciosTextos = conseguirTextViews();
        ArrayList<Categoria> categorias = plantilla.getCategorias();
        ImageView imageView = (ImageView) findViewById(R.id.personaje);
        byte[] imageBytes;
//Image String es una entrada del array de cosas en b64
        imageBytes = Base64.decode(plantilla.getPersonajes().get(GameContext.getRonda()-1).getNombre(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(decodedImage);
//        Picasso.with(imageView.getContext()).load("https://firebasestorage.googleapis.com/v0/b/qves-ddf27.appspot.com/o/images%2Foutput-onlinejpgtools.jpg?alt=media&token=ebf53013-726c-4d13-bc6c-5f7bc7fbc47e"
//        ).into(imageView);

        for (int i = 0; i < 10; i++) {
            System.out.println(categorias.get(i).getNombre());
            int codigoColor=categorias.get(i).getColor().getCodigo();
            String nombre= categorias.get(i).getNombre();

            CardView espacioCarta= espacioCartas.get(i);
            espacioCarta.setCardBackgroundColor(codigoColor);

            TextView espacioTexto = espaciosTextos.get(i);
            espacioTexto.setText(nombre);

            GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().get(i).setId(espacioCarta.getId());
        }
    }

    public ArrayList<CardView> conseguirCardViews() {
        ArrayList<CardView> espaciosCartas = new ArrayList<>();
        espaciosCartas.add(findViewById(R.id.cardView));
        espaciosCartas.add(findViewById(R.id.cardView2));
        espaciosCartas.add(findViewById(R.id.cardView3));
        espaciosCartas.add(findViewById(R.id.cardView4));
        espaciosCartas.add(findViewById(R.id.cardView5));
        espaciosCartas.add(findViewById(R.id.cardView6));
        espaciosCartas.add(findViewById(R.id.cardView7));
        espaciosCartas.add(findViewById(R.id.cardView8));
        espaciosCartas.add(findViewById(R.id.cardView9));
        espaciosCartas.add(findViewById(R.id.cardView10));
        return espaciosCartas;
    }

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
    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}