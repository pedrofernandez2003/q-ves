package com.example.hotspot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadedEchoServer extends Thread {
    static final int PORT = 7028;
    private ArrayList<EchoThread> hijos=new ArrayList<>();

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
            System.out.println("a");
            try {
                socket = serverSocket.accept();
                System.out.println("b");
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            EchoThread nuevoHijo=new EchoThread(socket);
            hijos.add(nuevoHijo);
            nuevoHijo.start();
        }
    }
}
