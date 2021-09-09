package com.example.hotspot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
        byte[] buffer = new byte[1];
        System.out.println(buffer.length);
        int bytes;
        String bufferAcumulado="";
        while (socket != null) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    if ((byte)'>'== buffer[0]){
                        System.out.println("me llego el ultimo caracter, buffer: "+bufferAcumulado);
                        callbackMensaje.mensajeRecibido(MESSAGE_READ, bytes, -1, bufferAcumulado);//no funciona
                        bufferAcumulado="";
                    }
                    else   {
                        bufferAcumulado+=(char)buffer[0];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(String msg) {
        try {
            msg=msg+">";
            System.out.println("envio "+msg);
            byte[] bytesMsg = msg.getBytes();
            outputStream.write(bytesMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}