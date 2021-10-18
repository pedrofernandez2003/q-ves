package com.example.objetos.manejoSockets;


import com.example.interfaces.conectarCallback;
import com.example.interfaces.mensajeCallback;
import com.example.objetos.GameContext;
import com.example.objetos.Mensaje;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadedEchoServer extends Thread{
    static final int PORT = 7028;
    public conectarCallback callbackMensaje;

    public void run() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
            }
            SendReceive nuevoHijo=new SendReceive(socket);
            nuevoHijo.callbackMensaje= new mensajeCallback() {
                @Override
                public void mensajeRecibido(int estado, String buffer) {
                    callbackMensaje.conectar(estado,buffer);
                }
            };
            GameContext.agregarHijo(nuevoHijo);
            nuevoHijo.start();
            ArrayList<String> datos=new ArrayList<>();
            Mensaje mensaje=new Mensaje("conectar",datos);
            String msg=mensaje.serializar();
            Write escribir = new Write();
            escribir.execute(msg, GameContext.getHijos().size()-1);
        }
    }
}

