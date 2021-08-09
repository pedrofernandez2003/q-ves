//package com.example.hotspot;
//
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import com.google.gson.Gson;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//
//public class ThreadedEchoServer extends Thread {
//    static final int PORT = 7028;
//    private GameContext context;
//
//
//    public void run() {
//        ServerSocket serverSocket = null;
//        Socket socket = null;
//
//        try {
//            serverSocket = new ServerSocket(PORT);
//            System.out.println("creo el socket");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        while (true) {
//            try {
//                socket = serverSocket.accept();
//            } catch (IOException e) {
//                System.out.println("I/O error: " + e);
//            }
//            SendReceive nuevoHijo=new SendReceive(socket);
//            context.agregarHijo(nuevoHijo);
//            nuevoHijo.start();
//        }
//    }
//
//}
//
