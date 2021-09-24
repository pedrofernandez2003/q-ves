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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServicioJuego extends Service {
    private ThreadedEchoServer server;

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
        registerReceiver(broadcastReceiver,intentFilter);
    }
    Context contexto=this;
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("accion: "+intent.getAction());
            switch (intent.getAction()){
                case "unirse":
                    ClientClass clientClass = new ClientClass(intent.getStringExtra("codigo"));
                    clientClass.start();
                    clientClass.callbackMensaje=new conectarCallback() {
                        @Override
                        public void conectar(int estado, String buffer) {
                            if (estado==1){
                                System.out.println("mensaje recibido "+buffer);
                                try {
                                    Gson json = new Gson();
                                    Mensaje mensaje = json.fromJson(buffer, Mensaje.class);
                                    switch (mensaje.getAccion()){
                                        case "comenzar":
                                            try {
                                                Juego juego = json.fromJson(mensaje.getDatos().get(0), Juego.class);
                                                GameContext.setJuego(juego);
                                                GameContext.setRonda(1);
                                                Equipo equipo= new Equipo(juego.getMazo(),GameContext.getNombresEquipos().get(0));
                                                GameContext.setEquipo(equipo);
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            Intent intent2= new Intent();
                                            intent2.setAction("comenzar");
                                            contexto.sendBroadcast(intent2);
                                            break;

                                        case "conectar":
                                            System.out.println("me llego conectar");
                                            ArrayList<String> datos=new ArrayList<>();
                                            datos.add(GameContext.getNombresEquipos().get(0));
                                            mensaje=new Mensaje("conectado",datos);
                                            String msg=mensaje.serializar();
                                            Write escribir = new Write();
                                            escribir.execute(msg, 0);
                                            break;

                                        case "turno":
                                            HashMap<String, String> mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(0),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            if (mapDatos.get("idJugador").equals(GameContext.getNombresEquipos().get(0))){
                                                GameContext.setEsMiTurno(true);
                                                Intent intent= new Intent();
                                                intent.setAction("turno");
                                                contexto.sendBroadcast(intent);
                                            }
                                            else{
                                                GameContext.setEsMiTurno(false);
                                            }
                                            break;

                                        case "actualizacion_tablero":
                                            Tarjeta tarjeta= json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(1),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            GameContext.setTarjetaElegida(tarjeta);
                                            Intent intent= new Intent();
                                            intent.setAction("actualizar");
                                            contexto.sendBroadcast(intent);
                                            System.out.println(mapDatos.get("idJugador")+" tiro esta carta "+tarjeta.getContenido());
                                            break;

                                        case "tarjetaMazo":
                                            Tarjeta tarjetaNueva= json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            System.out.println("me llego la tarjeta "+tarjetaNueva.getCategoria());
                                            GameContext.getEquipo().getTarjetas().add(tarjetaNueva);
                                            break;

                                        case "partida_nueva":
                                            GameContext.setRonda(GameContext.getRonda()+1);
                                            intent= new Intent();
                                            intent.setAction("reiniciar");
                                            contexto.sendBroadcast(intent);
                                            break;

                                        case "terminar_juego":
                                            datos=new ArrayList<>();
                                            datos.add("{\"cantidadTarjetas\": \""+GameContext.getEquipo().getTarjetas().size()+"\",\"idJugador\": \""+GameContext.getNombresEquipos().get(0)+"\"}");
                                            mensaje=new Mensaje("misCartas",datos);
                                            msg=mensaje.serializar();
                                            escribir = new Write();
                                            escribir.execute(msg, 0);
                                            break;

                                        case "ganador":
                                            mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(0),HashMap.class);
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            String nombreEquipo= mapDatos.get("ganador");
                                            intent= new Intent();
                                            intent.putExtra("ganador",nombreEquipo);
                                            intent.setAction("ganador");
                                            contexto.sendBroadcast(intent);
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
                    System.out.println("IP mod: " + getIPAddress(true));
                    try {
                        HTTPServer httpServer=new HTTPServer();
                        httpServer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server= new ThreadedEchoServer();
                    server.start();
                    GameContext.setServer(server);
                    server.callbackMensaje=new conectarCallback() {
                        @Override
                        public void conectar(int estado, String buffer) {
                            if (estado==1){
                                try {
                                    Gson json = new Gson();
                                    Mensaje mensaje = json.fromJson(buffer, Mensaje.class);
                                    switch (mensaje.getAccion()){
                                        case "conectado":
                                            GameContext.getNombresEquipos().add(mensaje.getDatos().get(0));
                                            System.out.println(mensaje.getAccion()+" "+mensaje.getDatos());
                                            Intent intent2= new Intent();
                                            intent2.setAction("nuevo equipo");
                                            contexto.sendBroadcast(intent2);
                                            break;
                                        case "jugarListo":
                                            for (int i=0;i<GameContext.getHijos().size();i++) {
                                                ArrayList<String> datos=new ArrayList<>();
                                                datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getTurno())+"\"}");
                                                mensaje=new Mensaje("turno",datos);
                                                String msg=mensaje.serializar();
                                                Write escribir = new Write();
                                                escribir.execute(msg, i);
                                            }
                                            break;



                                        case "jugada":
                                            System.out.println("buffer "+buffer +"\n mensaje"+mensaje.getDatos().get(0));
                                            Tarjeta tarjeta= json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            HashMap<String, String> mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(1),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            String nombreEquipo= mapDatos.get("idJugador");
                                            if (GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getTurno()==GameContext.getJuego().getEquipos().size()-1){
                                                GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).setTurno(0);
                                            }
                                            else{
                                                GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).setTurno(GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getTurno()+1);//no se si esta bien aca
                                            }
                                            GameContext.setTarjetaElegida(tarjeta);
                                            Intent intent= new Intent();
                                            intent.setAction("actualizar");
                                            contexto.sendBroadcast(intent);
                                            for (int i=0;i<GameContext.getHijos().size();i++){
                                                if(!GameContext.getNombresEquipos().get(i).equals(nombreEquipo)){ //para que no se lo mande al que jugo
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    datos.add(tarjeta.serializar());
                                                    datos.add("{\"idJugador\": \""+nombreEquipo+"\"}");
                                                    mensaje=new Mensaje("actualizacion_tablero",datos);
                                                    System.out.println("mande actualizacion");
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                            }
                                            boolean terminarPartida=true;
                                            System.out.println("casilleros "+GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros().size());
                                            for (Casillero casillero:GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getCasilleros()) {
                                                if(casillero.getTarjeta()==null){
                                                    terminarPartida=false;
                                                    break;
                                                }
                                            }
                                            System.out.println("condicion terminar part: "+ GameContext.getRonda() + " " + GameContext.getJuego().getPartidas().size()+1);
                                            System.out.println(GameContext.getJuego().getPartidas().size()+1);
                                            System.out.println("terminar partida: "+terminarPartida);
                                            if (!terminarPartida){
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getTurno())+"\"}");
                                                    mensaje=new Mensaje("turno",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                            }
                                            else if (GameContext.getRonda()<GameContext.getJuego().getPartidas().size()){
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    mensaje=new Mensaje("partida_nueva",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                                System.out.println("termina la ronda");
                                                GameContext.setRonda(GameContext.getRonda()+1);
                                                intent= new Intent();
                                                intent.setAction("reiniciar");
                                                contexto.sendBroadcast(intent);
                                            }
                                            else{
                                                System.out.println("deberia terminar el juego");
                                                for (int i=0;i<GameContext.getHijos().size();i++) {
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    mensaje=new Mensaje("terminar_juego",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                                System.out.println("termina el juego");
                                            }
                                            break;

                                        case "notificarModeradorSobreAnulacion":
                                            Tarjeta tarjetaAAnular= json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            intent2= new Intent();
                                            //intent2.putExtra("", ganador); como poner tarjeta no se
                                            intent2.setAction("notificarModerador");
                                            contexto.sendBroadcast(intent2);
                                            break;

                                        case "anular_carta":
                                            tarjetaAAnular= json.fromJson(mensaje.getDatos().get(0), Tarjeta.class);
                                            mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(1),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            nombreEquipo= mapDatos.get("idJugador");
                                            break;
                                        case "misCartas":
                                            System.out.println("me llegaron cartas");
                                            mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(0),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }

                                            int cantidadCartas= Integer.parseInt(mapDatos.get("cantidadTarjetas"));
                                            String nombreJugador= mapDatos.get("idJugador");
                                            GameContext.getResultados().put(nombreJugador,cantidadCartas);
                                            GameContext.setCantMensajesRecibidos(GameContext.getCantMensajesRecibidos()+1);
                                            if(GameContext.getCantMensajesRecibidos()==GameContext.getJuego().getEquipos().size()){
                                                String ganador= obtenerGanador();
                                                //ver esto
                                                for (int i=0;i<GameContext.getHijos().size();i++) {//le manda a todos quien es el ganador
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    datos.add("{\"ganador\": \""+ganador+"\"}");
                                                    mensaje=new Mensaje("ganador",datos);
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                                intent2= new Intent();
                                                intent2.putExtra("ganador", ganador);
                                                intent2.setAction("ganador");
                                                contexto.sendBroadcast(intent2);
                                            }
                                            break;

                                        case "pasarTurno":
                                            if (GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getTurno()==GameContext.getJuego().getEquipos().size()-1){
                                                GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).setTurno(0);
                                            }
                                            else{
                                                GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).setTurno(GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getTurno()+1);//no se si esta bien aca
                                            }
                                            for (int i=0;i<GameContext.getHijos().size();i++) {
                                                ArrayList<String> datos=new ArrayList<>();
                                                datos.add("{\"idJugador\": \""+GameContext.getNombresEquipos().get(GameContext.getJuego().getPartidas().get(GameContext.getRonda()-1).getTurno())+"\"}");
                                                mensaje=new Mensaje("turno",datos);
                                                String msg=mensaje.serializar();
                                                Write escribir = new Write();
                                                escribir.execute(msg, i);
                                            }

                                        case "agarrarCarta":
                                            mapDatos=new HashMap<>();
                                            try {
                                                mapDatos = json.fromJson(mensaje.getDatos().get(0),HashMap.class);//ponemos 0 porque sabemos que solo llega 1, modificarlo para los demas
                                            } catch (JsonSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                            nombreEquipo= mapDatos.get("idJugador");
                                            System.out.println("nombre equipo "+nombreEquipo);
                                            Tarjeta tarjetaARepartir=conseguirCarta();
                                            for (int i=0;i<GameContext.getHijos().size();i++){
                                                if(GameContext.getNombresEquipos().get(i).equals(nombreEquipo)){
                                                    ArrayList<String> datos=new ArrayList<>();
                                                    datos.add(tarjetaARepartir.serializar());
                                                    mensaje=new Mensaje("tarjetaMazo",datos);
                                                    System.out.println("mande una tarjeta del mazo");
                                                    String msg=mensaje.serializar();
                                                    Write escribir = new Write();
                                                    escribir.execute(msg, i);
                                                }
                                            }
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
    };

    public Tarjeta conseguirCarta() {
        HashSet<Tarjeta> mazo=GameContext.getJuego().getMazo();

        int size = mazo.size();
        int item = new Random().nextInt(size);
        int i = 0;

        for (Tarjeta obj : mazo) {
            if (i == item) {
                GameContext.getJuego().getMazo().remove(obj);
                return obj;
            }
            i++;
        }
        return new Tarjeta();
    }

    public String obtenerGanador(){
        Object[] nombreEquipos = GameContext.getResultados().keySet().toArray();
        Object[] cantidadDeCartas = GameContext.getResultados().values().toArray();
        String nombreEquipo = (String) nombreEquipos[0];
        Integer cartas = (Integer) cantidadDeCartas[0];
        for(Map.Entry<String, Integer> equipo: GameContext.getResultados().entrySet()){
            System.out.println("entre al for");
            if(equipo.getValue()<cartas){
                System.out.println("entre al if");
                nombreEquipo = equipo.getKey();
                cartas = equipo.getValue();
            }

        }
        System.out.println("el ganador es: "+nombreEquipo);
        return nombreEquipo;
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}