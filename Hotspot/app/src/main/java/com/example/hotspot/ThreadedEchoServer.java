package com.example.hotspot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedEchoServer extends Thread{
    static final int PORT = 7028;

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
            GameContext.agregarHijo(nuevoHijo);
//            ServicioJuego.handler.obtainMessage().sendToTarget();
            nuevoHijo.start();
        }
    }
}

