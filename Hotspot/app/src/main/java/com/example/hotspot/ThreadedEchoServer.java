package com.example.hotspot;


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
            System.out.println("creo el socket");
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            SendReceive nuevoHijo=new SendReceive(socket);
            nuevoHijo.callbackMensaje= new mensajeCallback() {
                @Override
                public void mensajeRecibido(int estado, int bytes, int argumento, byte[] buffer) {
                    callbackMensaje.conectar(estado, bytes, argumento,buffer);
                }
            };
            GameContext.agregarHijo(nuevoHijo);
            nuevoHijo.start();
            ArrayList<String> datos=new ArrayList<>();
            Mensaje mensaje=new Mensaje("conectar",datos);
            String msg=mensaje.serializar();
            System.out.println();
            byte[] bytesMsg = msg.getBytes();
            Write escribir = new Write();
            escribir.execute(bytesMsg, GameContext.getHijos().size()-1);
        }
    }
}

