package com.example.hotspot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.login_crud.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Jugar extends AppCompatActivity  {
//    private GameContext context;
    private Juego juego;
    private Partida partida;
    private TextView turno;

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("accion jugar: "+intent.getAction());
            switch (intent.getAction()){
                case "turno":
                    System.out.println("modifico el label turno");
                    turno = findViewById(R.id.turno);
                    turno.setVisibility(View.VISIBLE);
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
        System.out.println("creo el broadcast");
        registerReceiver(broadcastReceiver,intentFilter);
//        context=GameContext.getGameContext();
        juego= GameContext.getJuego();
        partida=GameContext.getPartidaActual();
        System.out.println(GameContext.getHijos().size());
        if (GameContext.getServers().size()==0){
            ArrayList<String> datos=new ArrayList<>();
//            System.out.println("turno de: "+GameContext.getNombresEquipos().get(partida.getTurno()));
//            System.out.println("hijos "+GameContext.getHijos().size());
            datos.add("{\"partida\": \""+partida.getTurno()+"\"}");
            Mensaje mensaje=new Mensaje("jugarListo",datos);
            String msg=mensaje.serializar();
            //byte[] bytesMsg = msg.getBytes();
            Write escribir = new Write();
            escribir.execute(msg, 0);
        }
        if (GameContext.getServers().size()>0){//para que solo haga esto el server
//            for (int i=0;i<GameContext.getHijos().size();i++) {
//                ArrayList<String> datos=new ArrayList<>();
//                System.out.println("turno de: "+GameContext.getNombresEquipos().get(partida.getTurno()));
//                System.out.println("hijos "+GameContext.getHijos().size());
//                datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(partida.getTurno())+"\"}");
//                Mensaje mensaje=new Mensaje("turno",datos);
//                String msg=mensaje.serializar();
//                byte[] bytesMsg = msg.getBytes();
//                Write escribir = new Write();
//                escribir.execute(bytesMsg, i);
//            }
        }
    }

    public void mostrarPlantillaEnXml(Plantilla plantilla, Context context) {
        ArrayList<CardView> espacioCartas = conseguirCardViews();
        ArrayList<TextView> espaciosTextos = conseguirTextViews();
        ArrayList<Categoria> categorias = plantilla.getCategorias();
        ImageView imageView = (ImageView) findViewById(R.id.personaje);
        Picasso.with(imageView.getContext()).load("https://firebasestorage.googleapis.com/v0/b/qves-ddf27.appspot.com/o/images%2Foutput-onlinejpgtools.jpg?alt=media&token=ebf53013-726c-4d13-bc6c-5f7bc7fbc47e"
        ).into(imageView);

        for (int i = 0; i < 10; i++) {
            System.out.println(categorias.get(i).getNombre());
            int codigoColor=categorias.get(i).getColor().getCodigo();
            String nombre= categorias.get(i).getNombre();

            CardView espacioCarta= espacioCartas.get(i);
            espacioCarta.setCardBackgroundColor(codigoColor);

            TextView espacioTexto = espaciosTextos.get(i);
            espacioTexto.setText(nombre);

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
}