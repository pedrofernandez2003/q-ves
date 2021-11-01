package com.example.actividades;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.objetos.Casillero;
import com.example.objetos.Categoria;
import com.example.objetos.DatosPartida;
import com.example.objetos.Equipo;
import com.example.objetos.GameContext;
import com.example.objetos.Juego;
import com.example.objetos.Mensaje;
import com.example.objetos.Partida;
import com.example.objetos.Tarjeta;
import com.example.objetos.Usuario;
import com.example.objetos.manejoSockets.Write;
import com.example.R;
import com.example.objetos.Plantilla;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;


public class JugarActivity extends AppCompatActivity  {
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
    private FloatingActionButton indicadorTurno;
    private boolean partidaReaunudada = false;

    Context appContext=this;
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "turno":
                    puedeAgarrarCarta=true;
                    indicadorTurno.setVisibility(ImageView.VISIBLE);
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

                case "reiniciar":// vuelve a cargar la actividad
                    if(GameContext.getServer()!=null){
                        takeScreenshot();
                    }
                    intent=new Intent(appContext,JugarActivity.class);
                    startActivity(intent);
                    break;

                case "actualizar":
                    insertarTarjetaEnTablero();
                    ultimoEquipoQueTiroCarta=intent.getStringExtra("equipo");
                    break;

                case "ganador":
                    if(GameContext.getServer()!=null){
                        takeScreenshot();
                    }
                    LayoutInflater inflater2 = LayoutInflater.from(JugarActivity.this);
                    View dialog_layout = inflater2.inflate(R.layout.ganador, null);
                    AlertDialog.Builder db = new AlertDialog.Builder(context);
                    db.setView(dialog_layout);
                    db.setTitle(intent.getStringExtra("motivoGanador"));
                    db.setMessage("El ganador es: "+intent.getStringExtra("ganador"));
                    db.setPositiveButton("Volver", null);
                    final AlertDialog a = db.create();
                    a.show();
                    if (partidaReaunudada){
                        System.out.println("se borro "+borrarAutoguardado());
                    }
                    Button volverAInicio = a.getButton(AlertDialog.BUTTON_POSITIVE);
                    volverAInicio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent;
                            if (GameContext.getServer()==null){
                                intent = new Intent(JugarActivity.this, MainActivity.class);
                            }
                            else if (Usuario.getUsuario().getRol().equals("administrador")){
                                intent = new Intent(JugarActivity.this, AdministradorActivity.class);
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
                    ultimoEquipoQueTiroCarta=intent.getStringExtra("equipoDeCartaAnulada");
                    crearAlertDialogSobreAnulacion();
                    break;

                case "anularCartaJugar":
                    ultimoEquipoQueTiroCarta=intent.getStringExtra("equipoDeCartaAnulada");
                    boolean anuladoCorrectamente=intent.getBooleanExtra("anuladoCorrectamente",true);
                    sacarTarjetaDelTablero();
                    if(GameContext.isEsMiTurno()){
                        inflater=getLayoutInflater();
                        Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                        text = (TextView) Layout.findViewById(R.id.toastTextView);
                        text.setText("Es tu turno!");
                        toast= new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM,0,0);
                        toast.setView(Layout);
                        toast.show();
                    }
                    if (GameContext.getServer()==null){
                        if (anuladoCorrectamente && GameContext.getEquipo().getNombre().equals(ultimoEquipoQueTiroCarta)) {
                            ArrayList<String> datos=new ArrayList<>();
                            datos.add("{\"idJugador\": \""+GameContext.getEquipo().getNombre()+"\"}");
                            Mensaje mensaje=new Mensaje("agarrarCarta",datos);
                            String msg=mensaje.serializar();
                            Write escribir = new Write();
                            escribir.execute(msg, 0);
                            puedeAgarrarCarta=false;
                        }
                    }
                    break;
                case "nuevaTarjeta":
                    botonVerCartas.performClick();
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
        if(getIntent().getBooleanExtra("reanudar",false)){
            File file = new File(Environment.getExternalStorageDirectory().toString()+"/plantillas/Autoguardado.qves");
            FileInputStream fin = null;
            String ret=null;
            try {
                fin = new FileInputStream(file);
                ret = convertStreamToString(fin);
                fin.close();
                Gson json = new Gson();
                DatosPartida datosPartida=json.fromJson(ret, DatosPartida.class);
                GameContext.setRonda(Integer.parseInt(datosPartida.getRonda()));
                Juego juego = json.fromJson(datosPartida.getJuego(),Juego.class);
                GameContext.setJuego(juego);
                Intent intent= new Intent();
                intent.setAction("unirse");
                intent.putExtra("codigo", Formatter.formatIpAddress(d.gateway));
                appContext.sendBroadcast(intent);
                GameContext.setEquipo(new Equipo(juego.getMazo(),juego.getEquipos().get(0).getNombre()));
                GameContext.getNombresEquipos().add(juego.getEquipos().get(0).getNombre()); //ver esto
                System.out.println("Entre");
                GameContext.getEquipo().setTarjetas(juego.getMazo());
            } catch (Exception e) {
                e.printStackTrace();
            }
            partidaReaunudada=true;
        }

        mostrarPlantillaEnXml(GameContext.getJuego().getPlantilla(), this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("turno");
        intentFilter.addAction("reiniciar");
        intentFilter.addAction("actualizar");
        intentFilter.addAction("ganador");
        intentFilter.addAction("mostrarDialog");
        intentFilter.addAction("anularCartaJugar");
        intentFilter.addAction("nuevaTarjeta");
        registerReceiver(broadcastReceiver,intentFilter);
        indicadorTurno= findViewById(R.id.indicadorTurno);
        juego= GameContext.getJuego();
        partida=GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1);
        ronda=findViewById(R.id.textView2);
        casilleros=GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros();
        categorias=GameContext.getJuego().getPlantilla().getCategorias();
        ronda.setText("Ronda: "+GameContext.getRonda()+"/"+GameContext.getJuego().getPartidas().size());
        if (GameContext.getServer()==null && !partidaReaunudada){//si no es el server
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
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;
                    int widthCarta = (width*9)/35;
                    int heightCarta = (widthCarta*7)/6;
                    int marginCarta = width/30;

                    int color=0;

                    for (Categoria categoria:GameContext.getJuego().getPlantilla().getCategorias()) {
                        if (categoria.getNombre().equals(GameContext.getTarjetaElegida().getCategoria())){
                            color=categoria.getColor().getCodigo();
                        }
                    }
                    for (int j=0; j < GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().size(); j++){
                        Casillero casilleroARevisar=GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().get(j);
                        if (casilleroARevisar.getCategoria().getNombre().equals(GameContext.getTarjetaElegida().getCategoria())){
                            color=casilleroARevisar.getCategoria().getColor().getCodigo();
                        }
                    }

                    for (int j=0; j < categorias.size(); j++){
                        Categoria categoriaARevisar=categorias.get(j);
                        if (categoriaARevisar.getNombre().equals(GameContext.getTarjetaElegida().getCategoria())){
                            color=categoriaARevisar.getColor().getCodigo();
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
                    prueba.addView(crearTarjetaVerCartas(widthCarta, heightCarta, marginCarta,color, GameContext.getTarjetaElegida().getCategoria(), GameContext.getTarjetaElegida().getContenido(), GameContext.getTarjetaElegida().getYapa()));
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
                                    GameContext.setTarjetaAnulada(GameContext.getTarjetaElegida());
                                    ArrayList<String> datos=new ArrayList<>();
                                    datos.add(GameContext.getTarjetaElegida().serializar());
                                    datos.add("{\"idJugador\": \""+ultimoEquipoQueTiroCarta+"\"}");
                                    Mensaje mensaje=new Mensaje("notificarModeradorSobreAnulacion",datos);
                                    String msg=mensaje.serializar();
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
                        LayoutInflater inflater=getLayoutInflater();
                        View Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                        TextView text = (TextView) Layout.findViewById(R.id.toastTextView);
                        text.setText("Agarraste una carta");
                        Toast toast= new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM,0,0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(Layout);
                        toast.show();
                        ArrayList<String> datos=new ArrayList<>();
                        datos.add("{\"idJugador\": \""+GameContext.getEquipo().getNombre()+"\"}");
                        Mensaje mensaje=new Mensaje("agarrarCarta",datos);
                        String msg=mensaje.serializar();
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
                    LayoutInflater inflater=getLayoutInflater();
                    View Layout= inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                    TextView text = (TextView) Layout.findViewById(R.id.toastTextView);
                    text.setText("Pasaste el turno");
                    Toast toast= new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM,0,0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(Layout);
                    toast.show();
                    ArrayList<String> datos=new ArrayList<>();
                    Mensaje mensaje=new Mensaje("pasarTurno",datos);
                    String msg=mensaje.serializar();
                    Write escribir = new Write();
                    escribir.execute(msg, 0);
                    GameContext.setEsMiTurno(false);
                    indicadorTurno.setVisibility(View.INVISIBLE);
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
                    int widthDialog = (width*9)/10;
                    int height = displayMetrics.heightPixels;
                    int heightDialog = (height*9)/10;
                    int heightCarta = (heightDialog*8)/10;
                    int widthCarta = (heightCarta*16)/20;
                    int marginCarta = width/60;

                    tarjetasHashSet=GameContext.getEquipo().getTarjetas();

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (heightDialog*8)/10);
                    contenedorCartas.setLayoutParams(params);

                    for (Tarjeta tarjetaARevisar:tarjetasHashSet) {
                        String nombreCategoria=tarjetaARevisar.getCategoria();
                        String tarjetaContenido=tarjetaARevisar.getContenido();
                        String tarjetaYapa=tarjetaARevisar.getYapa();
                        int color=0;

                        for (int j=0; j < categorias.size(); j++){
                            Categoria categoriaARevisar=categorias.get(j);
                            if (categoriaARevisar.getNombre().equals(tarjetaARevisar.getCategoria())){
                                color=categoriaARevisar.getColor().getCodigo();
                            }
                        }

                        MaterialCardView carta = crearTarjetaVerCartas(widthCarta, heightCarta, marginCarta, color, nombreCategoria,tarjetaContenido,tarjetaYapa);
                        contenedorCartas.addView(carta);

                        carta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                GameContext.setTarjetaElegida(tarjetaARevisar);
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
                                    if (GameContext.isEsMiTurno()){
                                        if (insertarTarjetaEnTablero()){
                                            ArrayList<String> datos=new ArrayList<>();
                                            datos.add(GameContext.getTarjetaElegida().serializar());
                                            GameContext.getEquipo().getTarjetas().remove(GameContext.getTarjetaElegida());
                                            datos.add("{\"idJugador\": \""+GameContext.getEquipo().getNombre()+"\"}");
                                            Mensaje mensaje=new Mensaje("jugada",datos);
                                            String msg=mensaje.serializar();
                                            Write escribir = new Write();
                                            escribir.execute(msg, 0);
                                            GameContext.setEsMiTurno(false);
                                            indicadorTurno.setVisibility(ImageView.INVISIBLE);
                                        }
                                        else{
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

                                    }
                                    a.dismiss();
                                }
                            });
                        }
                    });
                    a.show();
                    a.getWindow().setLayout((width*9)/10, (height*19)/20);
                }

            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleOnBackPressed() {
            }
        };

        this.getOnBackPressedDispatcher().addCallback(this, callback);
        rellenarTablero();
    }

    public void rellenarTablero(){
        CardView cardView = findViewById(R.id.cardView);
        cardView.post(new Runnable() {
            @Override
            public void run() {
                HashSet<Tarjeta> cartasTiradas=new HashSet<>();
                for (Casillero casillero: casilleros){// tenemos que vaciar el tablero por como esta hecha la funcion
                    System.out.println("id casillero "+casillero.getId());
                    if (casillero.getTarjeta()!=null){
                        cartasTiradas.add(casillero.getTarjeta());
                        casillero.setTarjeta(null);
                    }
                }
                for (Tarjeta tarjeta:cartasTiradas){//insertamos las tarjetas viejas en el tablero
                    GameContext.setTarjetaElegida(tarjeta);
                    insertarTarjetaEnTablero();
                }
            }
        });
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

    public void traerImagen(Plantilla plantilla) {
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
                    }
                }
        );
        Volley.newRequestQueue(this).add(imageRequest);
    }

    public boolean insertarTarjetaEnTablero() {
        boolean insertoLaTarjeta=false;
        for (Casillero casillero : casilleros) {
            if (casillero.getTarjeta() == null && casillero.getCategoria().getNombre().equals(GameContext.getTarjetaElegida().getCategoria())) {
                casillero.setTarjeta(GameContext.getTarjetaElegida());

                CardView prueba = (CardView) findViewById(casillero.getId());
                prueba.removeAllViews();
                System.out.println("width cardView prueba "+prueba.getWidth());//esto da 0 cuando inserta las viejas y 388 con las nuevas


                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                System.out.println("width "+width);
                int widthCarta = width / 7;
                int heightCarta = widthCarta;
                int marginCarta = width / 45;
                int color = casillero.getCategoria().getColor().getCodigo();
                String nombreCategoria = casillero.getCategoria().getNombre();
                String tarjetaContenido = GameContext.getTarjetaElegida().getContenido();
                String tarjetaYapa = GameContext.getTarjetaElegida().getYapa();
                CardView carta = crearTarjeta(widthCarta, heightCarta, marginCarta, color, nombreCategoria, tarjetaContenido, tarjetaYapa,true);
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
                LinearLayout categoria=new LinearLayout(JugarActivity.this);
                categoria.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                categoria.setGravity(Gravity.CENTER);

                TextView categoriaTxt=new TextView(JugarActivity.this);
                categoriaTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                categoriaTxt.setGravity(Gravity.CENTER);
                Typeface typeface = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int widthCarta = (width*9)/35;
        int heightCarta = (widthCarta*7)/8;
        int marginCarta = width/30;
        int color=0;

        for (Categoria categoria:GameContext.getJuego().getPlantilla().getCategorias()) {
            if (categoria.getNombre().equals(GameContext.getTarjetaAnulada().getCategoria())){
                color=categoria.getColor().getCodigo();
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
        prueba.addView(crearTarjetaVerCartas(widthCarta, heightCarta, marginCarta,color, GameContext.getTarjetaElegida().getCategoria(), GameContext.getTarjetaElegida().getContenido(), GameContext.getTarjetaElegida().getYapa()));
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
                            Write escribir = new Write();
                            escribir.execute(msg, i);
                        }
                        Intent intent= new Intent();
                        intent.putExtra("equipoDeCartaAnulada", ultimoEquipoQueTiroCarta);
                        intent.putExtra("anuladoCorrectamente", false);
                        intent.setAction("anularCartaJugar");
                        appContext.sendBroadcast(intent);
                        a.dismiss();
                    }
                });
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i=0;i<GameContext.getHijos().size();i++) {
                            ArrayList<String> datos=new ArrayList<>();
                            datos.add(GameContext.getTarjetaAnulada().serializar());
                            datos.add("{\"anuladoCorrectamente\": \""+true+"\",\"idJugador\": \""+ultimoEquipoQueTiroCarta+"\"}");
                            Mensaje mensaje=new Mensaje("actualizacion_tablero_anulacion",datos);
                            String msg=mensaje.serializar();
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

    public MaterialCardView crearTarjetaVerCartas(int width, int height, int margin, int color, String categoria, String contenido, String yapaContenido){
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

    public CardView crearTarjeta(int widthCartaGrande, int height, int margin, int color, String categoria, String contenido, String yapaContenido, boolean hacerCartaGrande){
        // Crear la base
        CardView carta = new CardView(this);
        int width = findViewById(R.id.cardView).getWidth() - 20;
        System.out.println("width crearTarjeta "+width);
        System.out.println("width cardView "+findViewById(R.id.cardView).getWidth());
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
        if (contenido.length()>60){
            textoContenido.setText("...");
        }
        else{
            textoContenido.setText(contenido);
        }
        textoContenido.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/8));
        textoContenido.setGravity(Gravity.CENTER);
        textoContenido.setId(ViewCompat.generateViewId());
        constraintLayout.addView(textoContenido);

        //Crear la yapa
        TextView yapa = new TextView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        yapa.setLayoutParams(params);
        yapa.setText("Yapa para discutir en grupo: ...");
        yapa.setTextSize(TypedValue.COMPLEX_UNIT_PX, (height/8));
        yapa.setGravity(Gravity.CENTER);
        yapa.setId(ViewCompat.generateViewId());
        constraintLayout.addView(yapa);

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

        if (hacerCartaGrande){
            carta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(JugarActivity.this);
                    View dialog_layout = inflater.inflate(R.layout.tarjeta, null);
                    final Dialog dialog= new Dialog(JugarActivity.this);
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
                    yapaView.setText(yapaContenido);

                    tarjeta.setCardBackgroundColor(-1644568);
                    //categoriaView.setTextColor(color);
                    parteArribaView.setBackgroundColor(color);
                    parteAbajoView.setBackgroundColor(color);

                    MaterialCardView.LayoutParams params = new MaterialCardView.LayoutParams((widthCartaGrande*5)/2, height*3);
                    tarjeta.setLayoutParams(params);

                    dialog.show();
                }
            });
        }
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

    public void mostrarPlantillaEnXml(Plantilla plantilla, Context context) {
        ArrayList<CardView> espacioCartas = conseguirCardViews();
        ArrayList<TextView> espaciosTextos = conseguirTextViews();
        ArrayList<Categoria> categorias = plantilla.getCategorias();
        traerImagen(plantilla);
        for (int i = 0; i < 10; i++) {
            int codigoColor=categorias.get(i).getColor().getCodigo();
            String nombre= categorias.get(i).getNombre();
            CardView espacioCarta= espacioCartas.get(i);
            espacioCarta.setCardBackgroundColor(codigoColor);
            TextView espacioTexto = espaciosTextos.get(i);
            espacioTexto.setText(nombre);
            GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().get(i).setId(espacioCarta.getId());
        }
    }

    private void takeScreenshot() {

        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/qves/" + GameContext.getJuego().getPlantilla().getNombre()+ now + ".jpg";
            File filebase = new File(Environment.getExternalStorageDirectory().toString(), "qves");
            filebase.mkdirs();


            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            v1.layout(0, 0, width, height);

            v1.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            e.printStackTrace();
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

    private void hideSystemUI() {//para poner full screen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {//para sacar el full screen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private boolean borrarAutoguardado(){//funciona?
        String fullPath = Environment.getExternalStorageDirectory().toString()+"/plantillas";
        try {
            File file = new File(fullPath, "Autoguardado.qves");
            if(file.exists()) {
                return file.delete();
            }
        }
        catch (Exception e) {
            Log.e("App", "Exception while deleting file " + e.getMessage());
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (GameContext.getServer()==null){
            String tarjetas="[";
            ArrayList<String> datos=new ArrayList<>();
            datos.add("{\"idJugador\": \""+GameContext.getEquipo().getNombre()+"\"}");
            Mensaje mensaje=new Mensaje("salir",datos);
            String msg=mensaje.serializar();
            Write escribir = new Write();
            escribir.execute(msg, 0);
            for (Casillero casillero:casilleros){
                if (casillero.getTarjeta()!=null){
                    tarjetas+=casillero.getTarjeta().serializar();
                }
            }
            tarjetas+="]";
            try {
                // image naming and path  to include sd card  appending name you choose for file
                String mPath = Environment.getExternalStorageDirectory().toString() + "/plantillas/Autoguardado.qves";
                File filebase = new File(Environment.getExternalStorageDirectory().toString(), "plantillas");
                filebase.mkdirs();
                File imageFile = new File(mPath);
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                DatosPartida datosPartida= new DatosPartida(String.valueOf(GameContext.getRonda()), juego.serializar(),tarjetas);
                msg=datosPartida.serializar();
                outputStream.write(msg.getBytes());
                outputStream.flush();
                outputStream.close();

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}