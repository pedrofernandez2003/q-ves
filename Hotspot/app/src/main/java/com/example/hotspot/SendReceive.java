package com.example.hotspot;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendReceive extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    static final int MESSAGE_READ=1;
    private Handler handler;
    public mensajeCallback callbackMensaje;


    public SendReceive(Socket skt) {
        System.out.println("entre al constructor");
        socket = skt;
    }

    @Override
    public void run() {
        try {
            System.out.println("se construyo el sendReceive");
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("entre al catch");
            e.printStackTrace();
        }
        byte[] buffer = new byte[1024];
        int bytes;
        while (socket != null) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    callbackMensaje.mensajeRecibido(MESSAGE_READ, bytes, -1, buffer);
//                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();//este handler deberia estar en el servicio
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}