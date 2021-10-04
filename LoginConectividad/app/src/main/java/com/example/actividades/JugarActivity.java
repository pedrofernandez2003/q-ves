package com.example.actividades;

import androidx.annotation.RequiresApi;
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
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.objetos.Casillero;
import com.example.objetos.Categoria;
import com.example.objetos.GameContext;
import com.example.objetos.Juego;
import com.example.objetos.Mensaje;
import com.example.objetos.Partida;
import com.example.objetos.Tarjeta;
import com.example.objetos.TarjetaSinCategoria;
import com.example.objetos.Usuario;
import com.example.objetos.manejoSockets.Write;
import com.example.R;
import com.example.objetos.Plantilla;
//import com.facebook.internal.ImageRequest;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import fi.iki.elonen.NanoHTTPD;

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
    private String ultimoEquipoQueTiroCarta;
    private WifiManager wifiManager;
    private DhcpInfo d;



    Context appContext=this;
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                    ultimoEquipoQueTiroCarta=intent.getStringExtra("equipo");
                    // crear variable que sea jugador que tiro carta y actualizarlo aca con el que jugp

                    break;

                case "ganador":
                    System.out.println("llega el ganador");
                    LayoutInflater inflater2 = LayoutInflater.from(JugarActivity.this);
                    View dialog_layout = inflater2.inflate(R.layout.ganador, null);
                    AlertDialog.Builder db = new AlertDialog.Builder(context);
                    db.setView(dialog_layout);
                    db.setTitle("Juego terminado");
                    db.setMessage("El ganador es: "+intent.getStringExtra("ganador"));
                    db.setPositiveButton("Volver a inicio", null);
                    final AlertDialog a = db.create();
                    a.show();
                    Button volverAInicio = a.getButton(AlertDialog.BUTTON_POSITIVE);
                    volverAInicio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent;
                            if (GameContext.getServer()==null){
                                intent = new Intent(JugarActivity.this, MainActivity.class);
                            }
                            else if (Usuario.getUsuario().getRol().equals("administrador")){
                                intent = new Intent(JugarActivity.this, AdminElementosActivity.class);
                            }
                            else {
                                intent = new Intent(JugarActivity.this, ModeradorActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;

                case "mostrarDialog":
                    //Le sale el alertdialog y si pone que si el boolean es true, si no es false :D
                    System.out.println("Soy el moderador que va validar la anulacion");
                    ultimoEquipoQueTiroCarta=intent.getStringExtra("equipoDeCartaAnulada");
                    crearAlertDialogSobreAnulacion();
                    break;

                case "anularCartaJugar":
                    System.out.println("llegue anularCarta");
                    ultimoEquipoQueTiroCarta=intent.getStringExtra("equipoDeCartaAnulada");
                    boolean anuladoCorrectamente=intent.getBooleanExtra("anuladoCorrectamente",true);
                    // en los otros va a sacar la tarjeta del tablero con esta funcion :D sacarTarjetaDelTablero();
                    sacarTarjetaDelTablero();
                    if (GameContext.getServer()==null){
                        System.out.println("anulado correctamente "+anuladoCorrectamente);
                        if (anuladoCorrectamente && GameContext.getEquipo().getNombre().equals(ultimoEquipoQueTiroCarta)) {
                            System.out.println("mando agarrar carta");
                            ArrayList<String> datos=new ArrayList<>();
                            datos.add("{\"idJugador\": \""+GameContext.getEquipo().getNombre()+"\"}");
                            Mensaje mensaje=new Mensaje("agarrarCarta",datos);
                            String msg=mensaje.serializar();
                            System.out.println("mensaje enviado "+msg);
                            Write escribir = new Write();
                            escribir.execute(msg, 0);
                            puedeAgarrarCarta=false;
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero_template);
        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        d=wifiManager.getDhcpInfo();
        mostrarPlantillaEnXml(GameContext.getJuego().getPlantilla(), this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("turno");
        intentFilter.addAction("reiniciar");
        intentFilter.addAction("actualizar");
        intentFilter.addAction("ganador");
        intentFilter.addAction("mostrarDialog");
        intentFilter.addAction("anularCartaJugar");
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
                    //hacer que tambien este aca la tarjeta a la izquierda del texto para que este lindo :D

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;
                    int widthCarta = (width*9)/35;
                    int heightCarta = (widthCarta*7)/6;
                    int marginCarta = width/30;

                    int color=0;

                    for (int j=0; j < GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().size(); j++){
                        Casillero casilleroARevisar=GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().get(j);
                        if (casilleroARevisar.getCategoria().getNombre().equals(GameContext.getTarjetaElegida().getCategoria())){
                            color=casilleroARevisar.getCategoria().getColor().getCodigo();
                        }
                    }

                    LayoutInflater inflater = LayoutInflater.from(JugarActivity.this);
                    View dialog_layout = inflater.inflate(R.layout.anular_carta, null);
                    AlertDialog.Builder db = new AlertDialog.Builder(JugarActivity.this);
                    db.setView(dialog_layout);
                    db.setTitle("Anular Tarjeta");
                    db.setPositiveButton("Enviar propuesta", null);
                    db.setNegativeButton("Atras", null);
                    LinearLayout prueba=(LinearLayout) dialog_layout.findViewById(R.id.carta);
                    prueba.addView(crearTarjeta(widthCarta, heightCarta, marginCarta,color, GameContext.getTarjetaElegida().getCategoria(), GameContext.getTarjetaElegida().getContenido(), GameContext.getTarjetaElegida().getYapa()));
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
                                    System.out.println("Soy el usuario que va a intentar anular");
                                    GameContext.setTarjetaAnulada(GameContext.getTarjetaElegida());
                                    //MANDARLE AL MODERADOR ESTO DE QUE SE QUIERE DEBIR ESTE TARJETA, QUE ESTA EN GAMECONTEXT.GETTARJETAELEJIDA
                                    ArrayList<String> datos=new ArrayList<>();
                                    datos.add(GameContext.getTarjetaElegida().serializar());
                                    datos.add("{\"idJugador\": \""+ultimoEquipoQueTiroCarta+"\"}");
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

                    hideSystemUI();
                    LayoutInflater inflater = LayoutInflater.from(JugarActivity.this);
                    View dialog_layout = inflater.inflate(R.layout.ver_cartas, null);
                    AlertDialog.Builder db = new AlertDialog.Builder(JugarActivity.this);
                    db.setView(dialog_layout);
                    LinearLayout contenedorCartas=(LinearLayout) dialog_layout.findViewById(R.id.contenedorCartas);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;
                    int widthCarta = width/6;
                    int heightCarta = (widthCarta*20)/16;
                    int marginCarta = width/60;

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

                        CardView carta = crearTarjetaVerCartas(widthCarta, heightCarta, marginCarta, color, nombreCategoria,tarjetaContenido,tarjetaYapa);
                        contenedorCartas.addView(carta);

                        carta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                GameContext.setTarjetaElegida(tarjetaARevisar);
                                //poner que le cambie el color del borde en vez de mostrar ese modal
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
//                                            GameContext.setTarjetaAnulada(GameContext.getTarjetaElegida());
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

    public void traerImagen(Plantilla plantilla) {
        System.out.println("entre a la funcion");
        ImageView imageView = (ImageView) findViewById(R.id.personaje);
        String url="";
        if(GameContext.getServer()==null){// si es el server es su propia direccion
            url = "http://"+ Formatter.formatIpAddress(d.gateway)+":5880/imagenes?imagen="+plantilla.getPersonajes().get(GameContext.getRonda()-1).getNombre();
        }
        else{
            url = "http://"+ Formatter.formatIpAddress(d.ipAddress)+":5880/imagenes?imagen="+plantilla.getPersonajes().get(GameContext.getRonda()-1).getNombre();
        }
        ImageRequest imageRequest = new ImageRequest(url,new Response.Listener<Bitmap>() { // Bitmap listener
            @Override
            public void onResponse(Bitmap response) {
                // Do something with response
                System.out.println("trajo la imagen "+response.toString());
                response = Bitmap.createScaledBitmap(response, 400, 400, false);
                imageView.setImageBitmap(response);
            }
        },
                0, // Image width
                0, // Image height
                null, // Image scale type
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with error response
                        error.printStackTrace();
                        System.out.println("error "+error.toString());
                    }
                }
        );
        // Add ImageRequest to the RequestQueue
        Volley.newRequestQueue(this).add(imageRequest);
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
                int widthCarta = width / 7;
                int heightCarta = widthCarta;
                int marginCarta = width / 45;
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
        for (Casillero casillero:casilleros) {
            if (casillero.getCategoria().getNombre().equals(GameContext.getTarjetaAnulada().getCategoria())) {
                casillero.setTarjeta(null);
                CardView prueba = (CardView) findViewById(casillero.getId());
                prueba.removeAllViews();

                // deberia funcionar, o eso espero :(
                LinearLayout categoria=new LinearLayout(JugarActivity.this);
                categoria.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                categoria.setGravity(Gravity.CENTER);

                TextView categoriaTxt=new TextView(JugarActivity.this);
                categoriaTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                categoriaTxt.setGravity(Gravity.CENTER);
                Typeface typeface = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { //un miedo esto
                    typeface = getResources().getFont(R.font.poertsen_one_regular);
                }
                categoriaTxt.setTypeface(typeface);
                categoriaTxt.setTextColor(getResources().getColor(R.color.white));
                categoriaTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,23);
                categoriaTxt.setText(casillero.getCategoria().getNombre());
                categoria.addView(categoriaTxt);
                prueba.addView(categoria);

            }
        }
    }

    public void crearAlertDialogSobreAnulacion(){
        System.out.println("cree el dialog");


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int widthCarta = (width*9)/35;
        int heightCarta = (widthCarta*7)/6;
        int marginCarta = width/30;
        int color=0;

        for (int j=0; j < GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().size(); j++){
            Casillero casilleroARevisar=GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().get(j);
            if (casilleroARevisar.getCategoria().getNombre().equals(GameContext.getTarjetaAnulada().getCategoria())){
                color=casilleroARevisar.getCategoria().getColor().getCodigo();
            }
        }


        LayoutInflater inflater = LayoutInflater.from(JugarActivity.this);
        View dialog_layout = inflater.inflate(R.layout.anular_carta, null);
        AlertDialog.Builder db = new AlertDialog.Builder(JugarActivity.this);
        db.setView(dialog_layout);
        db.setTitle("Anular Tarjeta");
        db.setPositiveButton("Aceptar propuesta", null);
        db.setNegativeButton("Rechazar propuesta", null);
        LinearLayout prueba=(LinearLayout) dialog_layout.findViewById(R.id.carta);
        prueba.addView(crearTarjeta(widthCarta, heightCarta, marginCarta,color, GameContext.getTarjetaElegida().getCategoria(), GameContext.getTarjetaElegida().getContenido(), GameContext.getTarjetaElegida().getYapa()));
        final AlertDialog a = db.create();

        a.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button si = a.getButton(AlertDialog.BUTTON_POSITIVE);
                Button no = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i=0;i<GameContext.getHijos().size();i++) {
                            ArrayList<String> datos=new ArrayList<>();
                            datos.add(GameContext.getTarjetaAnulada().serializar());
                            datos.add("{\"anuladoCorrectamente\": \""+false+"\",\"idJugador\": \""+ultimoEquipoQueTiroCarta+"\"}");
                            Mensaje mensaje=new Mensaje("actualizacion_tablero_anulacion",datos);
                            String msg=mensaje.serializar();
                            System.out.println("mensaje enviado "+msg);
                            Write escribir = new Write();
                            escribir.execute(msg, i);
                        }
                        Intent intent= new Intent();
                        intent.putExtra("equipoDeCartaAnulada", ultimoEquipoQueTiroCarta);
                        intent.putExtra("anuladoCorrectamente", false);
                        intent.setAction("anularCartaJugar");
                        appContext.sendBroadcast(intent);
                        System.out.println("Soy el moderador que rechazo la anulacion");
                        a.dismiss();
                    }
                });
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //charlar con pepo si es un msg o un intent
                        System.out.println("Soy el moderador que avalo la anulacion");
                        for (int i=0;i<GameContext.getHijos().size();i++) {
                            ArrayList<String> datos=new ArrayList<>();
                            datos.add(GameContext.getTarjetaAnulada().serializar());
                            datos.add("{\"anuladoCorrectamente\": \""+true+"\",\"idJugador\": \""+ultimoEquipoQueTiroCarta+"\"}");
                            Mensaje mensaje=new Mensaje("actualizacion_tablero_anulacion",datos);
                            String msg=mensaje.serializar();
                            System.out.println("mensaje enviado "+msg);
                            Write escribir = new Write();
                            escribir.execute(msg, i);
                        }
                        Intent intent= new Intent();
                        intent.putExtra("equipoDeCartaAnulada", ultimoEquipoQueTiroCarta);
                        intent.putExtra("anuladoCorrectamente", true);
                        intent.setAction("anularCartaJugar");
                        appContext.sendBroadcast(intent);
                        a.dismiss();
                    }
                });
            }
        });
        a.show();
    }

    public CardView crearTarjetaVerCartas(int width, int height, int margin, int color, String categoria, String contenido, String yapaContenido){

        // Crear la base
        CardView carta = new CardView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        //params.setMargins(0,0,0,0);
        carta.setLayoutParams(params);
        carta.setBackgroundColor(-1644568);

        View lineaHorizontal= new View(this);
        FrameLayout.LayoutParams paramsBorderHorizontal = new FrameLayout.LayoutParams(width, height/34);
        lineaHorizontal.setLayoutParams(paramsBorderHorizontal);
        lineaHorizontal.setId(ViewCompat.generateViewId());
        lineaHorizontal.setBackgroundColor(Color.GRAY);


        View lineaHorizontal2= new View(this);
        lineaHorizontal2.setLayoutParams(paramsBorderHorizontal);
        lineaHorizontal2.setId(ViewCompat.generateViewId());
        lineaHorizontal2.setBackgroundColor(Color.GRAY);

        View lineaVertical= new View(this);
        FrameLayout.LayoutParams paramsBorderVertical = new FrameLayout.LayoutParams(width/34, height);
        lineaVertical.setLayoutParams(paramsBorderVertical);
        lineaVertical.setId(ViewCompat.generateViewId());
        lineaVertical.setBackgroundColor(Color.GRAY);

        // Crear el constraint layout
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        params = new FrameLayout.LayoutParams(width, height);
        constraintLayout.setLayoutParams(params);
        constraintLayout.setId(ViewCompat.generateViewId());
        //constraintLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.border));
        carta.addView(constraintLayout);

        carta.addView(lineaVertical);
        carta.addView(lineaHorizontal);
        carta.addView(lineaHorizontal2);


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
        textoCategoria.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/8);
        textoCategoria.setTypeface(ResourcesCompat.getFont(this, R.font.poertsen_one_regular));
        textoCategoria.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoCategoria);

        //Crear el textview para el contenido
        TextView textoContenido = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        textoContenido.setLayoutParams(params);
        textoContenido.setText(contenido);
        textoContenido.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/12));
        textoContenido.setGravity(Gravity.CENTER);
        textoContenido.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoContenido);

        //Crear la yapa
        TextView yapa = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        yapa.setLayoutParams(params);
        yapa.setText(yapaContenido);
        yapa.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/15));
        yapa.setGravity(Gravity.CENTER);
        yapa.setId(ViewCompat.generateViewId());
        constraintLayout.addView(yapa);

        //Constraints
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        set.connect(bordeTop.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);

        //set.connect(lineaHorizontal.getId(),ConstraintSet.BOTTOM, bordeTop.getId(), ConstraintSet.TOP);

        set.connect(bordeBot.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);

        //set.connect(lineaVertical.getId(),ConstraintSet.END, constraintLayout.getId(), ConstraintSet.START);
        //set.connect(lineaVertical.getId(),ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        //set.connect(lineaHorizontal2.getId(),ConstraintSet.BOTTOM, bordeBot.getId(), ConstraintSet.BOTTOM);

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
//        CardView bordeTop = new CardView(this);
//        params = new FrameLayout.LayoutParams(width, height/8);
//        bordeTop.setLayoutParams(params);
//        bordeTop.setBackgroundColor(color);
//        bordeTop.setId(ViewCompat.generateViewId());
//        constraintLayout.addView(bordeTop);


        // Crear el borde de abajo
//        CardView bordeBot = new CardView(this);
//        params = new FrameLayout.LayoutParams(width, (height*3)/50);
//        bordeBot.setLayoutParams(params);
//        bordeBot.setBackgroundColor(color);
//        bordeBot.setId(ViewCompat.generateViewId());
//        constraintLayout.addView(bordeBot);

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
//        set.connect(bordeTop.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 0);
//        set.connect(bordeBot.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
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
        textoCategoria.setTypeface(ResourcesCompat.getFont(this, R.font.poertsen_one_regular));
        textoCategoria.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoCategoria);

        //Crear el textview para el contenido
        TextView textoContenido = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        textoContenido.setLayoutParams(params);
        textoContenido.setText(contenido);
        textoContenido.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/12));
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
        traerImagen(plantilla);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
        else{
            showSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}