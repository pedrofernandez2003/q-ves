package com.example.objetos.manejoSockets;

import com.example.interfaces.mensajeCallback;

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
        socket = skt;
    }

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[1];
        int bytes;
        String bufferAcumulado="";
        while (socket != null) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    if ((byte)'>'== buffer[0]){
                        callbackMensaje.mensajeRecibido(MESSAGE_READ, bufferAcumulado);//no funciona
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
            byte[] bytesMsg = msg.getBytes();
            outputStream.write(bytesMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public static int getMessageRead() {
        return MESSAGE_READ;
    }

    public mensajeCallback getCallbackMensaje() {
        return callbackMensaje;
    }

    public void setCallbackMensaje(mensajeCallback callbackMensaje) {
        this.callbackMensaje = callbackMensaje;
    }

}