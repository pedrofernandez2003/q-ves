package com.example.hotspot;

import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendReceive extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    static final int MESSAGE_READ=1;
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
        byte[] buffer = new byte[16384];
        System.out.println(buffer.length);
        int bytes;
        while (socket != null) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    callbackMensaje.mensajeRecibido(MESSAGE_READ, bytes, -1, buffer);
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