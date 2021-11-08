package com.example.objetos;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;
import com.example.interfaces.conectarCallback;
import com.example.objetos.manejoSockets.ClientClass;
import com.example.objetos.manejoSockets.HTTPServer;
import com.example.objetos.manejoSockets.ThreadedEchoServer;
import com.example.objetos.manejoSockets.Write;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class ServicioJuego extends Service {
    private ThreadedEchoServer server;
    private HTTPServer httpServer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("unirse");
        intentFilter.addAction("crear server");
        intentFilter.addAction("enviar_anular_carta");
        registerReceiver(broadcastReceiver,intentFilter);
    }
    Context contexto=this;
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "unirse":
                    ClientClass clientClass = new ClientClass(intent.getStringExtra("codigo"));
                    clientClass.start();
                    clientClass.callbackMensaje = new conectarCallback() {
                        @Override
                        public void conectar(int estado, String buffer) {
                            if (estado == 1) {
                                try {
                                    Gson json = new Gson();
                                    Mensaje mensaje = json.fromJson(buffer, Mensaje.class);
                                    switch (mensaje.getAccion()) {
                                        case "comenzar"://setea el juego, el equipo y manda el intent
                                            try {
                                                Juego juego = json.fromJson(mensaje.getDatos().get(0), Juego.class);
                                                GameContext.setJuego(juego);
                                                GameContext.setRonda(1);
                                                Equipo equipo = new Equipo(juego.getMazo(), GameContext.getNombresEquipos().get(0));
                                                juego.getEquipos().clear();
                                                juego.getEquipos().add(equipo);//para que solo tenga su equipo
                                                GameContext.setEquipo(equipo);
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            Intent intent2 = new Intent();
                                            intent2.setAction("comenzar");
                                            contexto.sendBroadcast(intent2);
                                            break;

                                        case "conectar": //para avisarle que ya esta conectado
                                            ArrayList<String> datos = new ArrayList<>();
                                            datos.add(GameContext.getNombresEquipos().get(0));
                                            mensaje = new Mensaje("conectado", datos);
                                            String msg = mensaje.serializar();
                                            Write escribir = new Write();
                                            escribir.execute(msg, 0);
                                            break;

                                        case "turno": //llega el turno y chequea que sea suyo
                                            HashMap<String, String> mapDatos = new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(0), HashMap.class);
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            if (mapDatos.get("idJugador").equals(GameContext.getNombresEquipos().get(0))) {
                                                GameContext.setEsMiTurno(true);
                                                Intent intent = new Intent();
                                                intent.setAction("turno");
                                                contexto.sendBroadcast(intent);
                                            } else {
                                                GameContext.setEsMiTurno(false);
                                            }
                                            break;

                                        case "actualizacion_tablero": //setea la tarjeta para despues actualizar el tablero
                                            Tarjeta tarjeta = json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            mapDatos = new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(1), HashMap.class);
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            GameContext.setTarjetaElegida(tarjeta);
                                            Intent intent = new Intent();
                                            intent.putExtra("equipo", mapDatos.get("idJugador"));
                                            intent.setAction("actualizar");
                                            contexto.sendBroadcast(intent);
                                            break;

                                        case "actualizacion_tablero_anulacion": //setea la tarjeta que se va a anular
                                            mapDatos = new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(1), HashMap.class);
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            tarjeta = json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            GameContext.setTarjetaAnulada(tarjeta);
                                            String equipo = mapDatos.get("idJugador");
                                            Boolean anuladoCorrectamente = Boolean.parseBoolean(mapDatos.get("anuladoCorrectamente"));
                                            intent = new Intent();
                                            intent.putExtra("equipoDeCartaAnulada", equipo);
                                            intent.putExtra("anuladoCorrectamente", anuladoCorrectamente);
                                            intent.setAction("anularCartaJugar");
                                            contexto.sendBroadcast(intent);
                                            break;

                                        case "tarjetaMazo": //le agrega una tarjeta al mazo
                                            Tarjeta tarjetaNueva = json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            GameContext.getEquipo().getTarjetas().add(tarjetaNueva);
                                            intent = new Intent();
                                            intent.setAction("nuevaTarjeta");
                                            contexto.sendBroadcast(intent);
                                            break;

                                        case "partida_nueva": //incrementamos la ronda y reiniciamos la actividad
                                            GameContext.setRonda(GameContext.getRonda() + 1);
                                            intent = new Intent();
                                            intent.setAction("reiniciar");
                                            contexto.sendBroadcast(intent);
                                            break;

                                        case "terminar_juego": //enviamos la cantidad de cartas restantes
                                            datos = new ArrayList<>();
                                            datos.add("{\"cantidadTarjetas\": \"" + GameContext.getEquipo().getTarjetas().size() + "\",\"idJugador\": \"" + GameContext.getNombresEquipos().get(0) + "\"}");
                                            mensaje = new Mensaje("misCartas", datos);
                                            msg = mensaje.serializar();
                                            escribir = new Write();
                                            escribir.execute(msg, 0);
                                            break;

                                        case "ganador"://envio por intent el ganador
                                            mapDatos = new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(0), HashMap.class);
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            String nombreEquipo = mapDatos.get("ganador");
                                            boolean mazoVacio = Boolean.parseBoolean(mapDatos.get("mazoVacio"));
                                            intent = new Intent();
                                            intent.putExtra("ganador", nombreEquipo);
                                            if (mazoVacio) {
                                                intent.putExtra("motivoGanador", "El mazo se ha quedado sin cartas");
                                            } else {
                                                intent.putExtra("motivoGanador", "Juego terminado");
                                            }
                                            intent.setAction("ganador");
                                            contexto.sendBroadcast(intent);
                                            break;
                                        case "pausar":
                                            intent = new Intent();
                                            intent.setAction("pausa");
                                            contexto.sendBroadcast(intent);
                                            GameContext.setPausa(true);
                                        break;
                                        case "reanudarPartida":
                                            intent = new Intent();
                                            intent.setAction("reanudar");
                                            contexto.sendBroadcast(intent);
                                            GameContext.setPausa(false);
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    break;
                case "crear server":
                    if (server == null) {
                        try {
                            httpServer = new HTTPServer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server = new ThreadedEchoServer();
                        server.start();
                        GameContext.setServer(server);
                        server.callbackMensaje = new conectarCallback() {
                            @Override
                            public void conectar(int estado, String buffer) {
                                if (estado == 1) {
                                    try {
                                        Gson json = new Gson();
                                        Mensaje mensaje = json.fromJson(buffer, Mensaje.class);
                                        switch (mensaje.getAccion()) {
                                            case "conectado":
                                                String nombreEquipo=mensaje.getDatos().get(0);
                                                GameContext.getNombresEquipos().add(nombreEquipo);
                                                if(GameContext.getEquiposRetirados().size()==0){
                                                    Intent intent2 = new Intent();
                                                    intent2.setAction("nuevo equipo");
                                                    contexto.sendBroadcast(intent2);
                                                }
                                                else{
                                                    System.out.println("volvio "+nombreEquipo);
                                                    GameContext.getEquiposRetirados().remove(nombreEquipo);//ya entro asi que lo sacamos de los retirados
                                                    System.out.println("faltan :"+GameContext.getEquiposRetirados().size());
                                                    if (GameContext.getEquiposRetirados().size()==0){
                                                        for (int i = 0; i < GameContext.getHijos().size(); i++) {
//                                                            if (GameContext.getNombresEquipos().get(i).equals(nombreEquipo)) {
                                                                System.out.println("le mando el turno "+GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno())+" a "+nombreEquipo);
                                                                ArrayList<String> datos = new ArrayList<>();
                                                                datos.add("{\"idJugador\": \"" + GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno()) + "\"}");
                                                                mensaje = new Mensaje("turno", datos);
                                                                String msg = mensaje.serializar();
                                                                Write escribir = new Write();
                                                                escribir.execute(msg, i);
                                                                datos = new ArrayList<>();
                                                                mensaje = new Mensaje("reanudarPartida", datos);
                                                                msg = mensaje.serializar();
                                                                escribir = new Write();
                                                                escribir.execute(msg, i);
//                                                            }
                                                        }
                                                    }
                                                    else{//si vuelve y faltan jugadores
                                                        ArrayList<String> datos = new ArrayList<>();
                                                        mensaje = new Mensaje("pausar", datos);
                                                        String msg = mensaje.serializar();
                                                        Write escribir = new Write();
                                                        escribir.execute(msg, GameContext.getHijos().size()-1);
                                                    }
                                                }

                                                break;
                                            case "jugarListo":
                                                for (int i = 0; i < GameContext.getHijos().size(); i++) {//manda el turno a todos
                                                    ArrayList<String> datos = new ArrayList<>();
                                                    datos.add("{\"idJugador\": \"" + GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno()) + "\"}");
                                                    mensaje = new Mensaje("turno", datos);
                                                    String msg = mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                                break;

                                            case "jugada"://cuando el equipo tira una carta
                                                Tarjeta tarjeta = json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                                HashMap<String, String> mapDatos = new HashMap<>();
                                                try {
                                                    mapDatos = json.fromJson(mensaje.getDatos().get(1), HashMap.class);
                                                } catch (JsonSyntaxException e) {
                                                    e.printStackTrace();
                                                }
                                                nombreEquipo = mapDatos.get("idJugador");
                                                if (GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno() == GameContext.getJuego().getEquipos().size() - 1) {
                                                    GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).setTurno(0);
                                                } else {
                                                    GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).setTurno(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno() + 1);//no se si esta bien aca
                                                }
                                                GameContext.setTarjetaElegida(tarjeta);
                                                Intent intent = new Intent();
                                                intent.setAction("actualizar");
                                                contexto.sendBroadcast(intent);
                                                for (int i = 0; i < GameContext.getHijos().size(); i++) {
                                                    if (!GameContext.getNombresEquipos().get(i).equals(nombreEquipo)) { //para que no se lo mande al que jugo
                                                        ArrayList<String> datos = new ArrayList<>();
                                                        datos.add(tarjeta.serializar());
                                                        datos.add("{\"idJugador\": \"" + nombreEquipo + "\"}");
                                                        mensaje = new Mensaje("actualizacion_tablero", datos);
                                                        String msg = mensaje.serializar();
                                                        Write escribir = new Write();
                                                        escribir.execute(msg, i);
                                                    }
                                                }
                                                boolean terminarPartida = true;
                                                for (Casillero casillero : GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getCasilleros()) {//recorre todos los casilleros para ver que haya al menos uno vacio
                                                    if (casillero.getTarjeta() == null) {
                                                        terminarPartida = false;
                                                        break;
                                                    }
                                                }
                                                if (!terminarPartida) {
                                                    for (int i = 0; i < GameContext.getHijos().size(); i++) {//envia el turno si no termino la partida
                                                        ArrayList<String> datos = new ArrayList<>();
                                                        datos.add("{\"idJugador\": \"" + GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno()) + "\"}");
                                                        mensaje = new Mensaje("turno", datos);
                                                        String msg = mensaje.serializar();
                                                        Write escribir = new Write();
                                                        escribir.execute(msg, i);
                                                    }
                                                } else if (GameContext.getRonda() < GameContext.getJuego().getPartidas().size()) {//si se termino la partida
                                                    for (int i = 0; i < GameContext.getHijos().size(); i++) {
                                                        ArrayList<String> datos = new ArrayList<>();
                                                        mensaje = new Mensaje("partida_nueva", datos);
                                                        String msg = mensaje.serializar();
                                                        Write escribir = new Write();
                                                        escribir.execute(msg, i);
                                                    }
                                                    GameContext.setRonda(GameContext.getRonda() + 1);
                                                    intent = new Intent();
                                                    intent.setAction("reiniciar");
                                                    contexto.sendBroadcast(intent);
                                                } else {//si ya se jugaron todas las partidas
                                                    for (int i = 0; i < GameContext.getHijos().size(); i++) {
                                                        ArrayList<String> datos = new ArrayList<>();
                                                        mensaje = new Mensaje("terminar_juego", datos);
                                                        String msg = mensaje.serializar();
                                                        Write escribir = new Write();
                                                        escribir.execute(msg, i);
                                                    }
                                                }
                                                break;

                                            case "notificarModeradorSobreAnulacion":
                                                Tarjeta tarjetaAnulada = json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                                mapDatos = new HashMap<>();
                                                try {
                                                    mapDatos = json.fromJson(mensaje.getDatos().get(1), HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                                } catch (JsonSyntaxException e) {
                                                    e.printStackTrace();
                                                }

                                                nombreEquipo = mapDatos.get("idJugador");
                                                GameContext.setTarjetaAnulada(tarjetaAnulada);
                                                intent = new Intent();
                                                intent.putExtra("equipoDeCartaAnulada", nombreEquipo);
                                                intent.setAction("mostrarDialog");
                                                contexto.sendBroadcast(intent);
                                                break;

                                            case "misCartas"://cuando los equipos mandan la cantidad de tarjetas
                                                mapDatos = new HashMap<>();
                                                try {
                                                    mapDatos = json.fromJson(mensaje.getDatos().get(0), HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                                } catch (JsonSyntaxException e) {
                                                    e.printStackTrace();
                                                }

                                                int cantidadCartas = Integer.parseInt(mapDatos.get("cantidadTarjetas"));
                                                String nombreJugador = mapDatos.get("idJugador");
                                                GameContext.getResultados().put(nombreJugador, cantidadCartas);
                                                GameContext.setCantMensajesRecibidos(GameContext.getCantMensajesRecibidos() + 1);
                                                if (GameContext.getCantMensajesRecibidos() == GameContext.getJuego().getEquipos().size()) {
                                                    String ganador = obtenerGanador();
                                                    for (int i = 0; i < GameContext.getHijos().size(); i++) {//le manda a todos quien es el ganador
                                                        ArrayList<String> datos = new ArrayList<>();
                                                        datos.add("{\"ganador\": \"" + ganador + "\",\"mazoVacio\":\"" + (GameContext.getJuego().getMazo().size() <= 0) + "\"}");
                                                        mensaje = new Mensaje("ganador", datos);
                                                        String msg = mensaje.serializar();
                                                        Write escribir = new Write();
                                                        escribir.execute(msg, i);
                                                    }
                                                    intent = new Intent();
                                                    intent.putExtra("ganador", ganador);
                                                    if (GameContext.getJuego().getMazo().size() <= 0) {
                                                        intent.putExtra("motivoGanador", "El mazo se ha quedado sin cartas");
                                                    } else {
                                                        intent.putExtra("motivoGanador", "Juego terminado");
                                                    }
                                                    intent.setAction("ganador");
                                                    contexto.sendBroadcast(intent);
                                                }
                                                break;

                                            case "pasarTurno":
                                                if (GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno() == GameContext.getJuego().getEquipos().size() - 1) {
                                                    GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).setTurno(0);
                                                } else {
                                                    GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).setTurno(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno() + 1);
                                                }
                                                for (int i = 0; i < GameContext.getHijos().size(); i++) {
                                                    ArrayList<String> datos = new ArrayList<>();
                                                    datos.add("{\"idJugador\": \"" + GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno()) + "\"}");
                                                    mensaje = new Mensaje("turno", datos);
                                                    String msg = mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                                break;

                                            case "agarrarCarta":
                                                mapDatos = new HashMap<>();
                                                try {
                                                    mapDatos = json.fromJson(mensaje.getDatos().get(0), HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                                } catch (JsonSyntaxException e) {
                                                    e.printStackTrace();
                                                }
                                                nombreEquipo = mapDatos.get("idJugador");
                                                Tarjeta tarjetaARepartir = conseguirCarta();
                                                if (tarjetaARepartir.getContenido().equals("")) {
                                                    for (int i = 0; i < GameContext.getHijos().size(); i++) {
                                                        ArrayList<String> datos = new ArrayList<>();
                                                        mensaje = new Mensaje("terminar_juego", datos);
                                                        String msg = mensaje.serializar();
                                                        Write escribir = new Write();
                                                        escribir.execute(msg, i);
                                                    }
                                                } else {
                                                    for (int i = 0; i < GameContext.getHijos().size(); i++) {
                                                        if (GameContext.getNombresEquipos().get(i).equals(nombreEquipo)) {
                                                            ArrayList<String> datos = new ArrayList<>();
                                                            datos.add(tarjetaARepartir.serializar());
                                                            mensaje = new Mensaje("tarjetaMazo", datos);
                                                            String msg = mensaje.serializar();
                                                            Write escribir = new Write();
                                                            escribir.execute(msg, i);
                                                        }
                                                    }
                                                }
                                                break;
                                            case "salir":
                                                //deberia pausarse la partida
                                                mapDatos = new HashMap<>();
                                                try {
                                                    mapDatos = json.fromJson(mensaje.getDatos().get(0), HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                                } catch (JsonSyntaxException e) {
                                                    e.printStackTrace();
                                                }
                                                nombreEquipo = mapDatos.get("idJugador");
                                                if (GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno()>= GameContext.getHijos().size()){//por si se van 2 o mas
                                                    GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).setTurno(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno() - 1);
                                                }
                                                else if (GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno()).equals(nombreEquipo)) {//si es el turno del que se fue
                                                    GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).setTurno(GameContext.getHijos().size()-1);
                                                } else if(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno()>GameContext.getNombresEquipos().indexOf(nombreEquipo)){
                                                    GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).setTurno(GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno() - 1);
                                                }
                                                System.out.println("el turno paso a: "+GameContext.getJuego().getPartidas().get(GameContext.getRonda() - 1).getTurno());
                                                for (int i = 0; i < GameContext.getHijos().size(); i++) {
                                                    if (!GameContext.getNombresEquipos().get(i).equals(nombreEquipo)) {
                                                        System.out.println("mando pausar a"+GameContext.getNombresEquipos().get(i));
                                                        ArrayList<String> datos = new ArrayList<>();
                                                        mensaje = new Mensaje("pausar", datos);
                                                        String msg = mensaje.serializar();
                                                        Write escribir = new Write();
                                                        escribir.execute(msg, i);
                                                    }
                                                }
                                                GameContext.getEquiposRetirados().add(nombreEquipo);
                                                GameContext.getHijos().remove(GameContext.getHijos().get(GameContext.getNombresEquipos().indexOf(nombreEquipo)));//lo sacamos de los hijos porque se va de la partida
                                                GameContext.getNombresEquipos().remove(nombreEquipo);
                                            break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };
                        break;
                    }
            }
        }
    };

    public Tarjeta conseguirCarta() {
        HashSet<Tarjeta> mazo=GameContext.getJuego().getMazo();

        int size = mazo.size();
        if (size>0){
            int item = new Random().nextInt(size);
            int i = 0;

            for (Tarjeta obj : mazo) {
                if (i == item) {
                    GameContext.getJuego().getMazo().remove(obj);
                    return obj;
                }
                i++;
            }
        }
        return new Tarjeta();
    }

    public String obtenerGanador(){
        Object[] nombreEquipos = GameContext.getResultados().keySet().toArray();
        Object[] cantidadDeCartas = GameContext.getResultados().values().toArray();
        String nombreEquipo = (String) nombreEquipos[0];
        Integer cartas = (Integer) cantidadDeCartas[0];
        for(Map.Entry<String, Integer> equipo: GameContext.getResultados().entrySet()){
            if(equipo.getValue()<cartas){
                nombreEquipo = equipo.getKey();
                cartas = equipo.getValue();
            }

        }
        return nombreEquipo;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        try {
            httpServer.stop();
            httpServer.closeAllConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}