package com.example.objetos.manejoSockets;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.interfaces.mensajeCallback;

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
        socket = skt;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        byte[] buffer = new byte[1];
        ArrayList<Byte>arregloBytes=new ArrayList<>();
        int bytes;
        int contador=0;
        String bufferAcumulado="";
        while (socket != null) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    if ((byte)'>'== buffer[0]){
                        byte[] bufferBytes=new byte[arregloBytes.size()];
                        for (int i=0;i<arregloBytes.size();i++){
                            bufferBytes[i]=arregloBytes.get(i);
                        }
                        contador=0;
                        bufferAcumulado= new String(bufferBytes,StandardCharsets.UTF_8);
                        callbackMensaje.mensajeRecibido(MESSAGE_READ, bufferAcumulado);
                        arregloBytes=new ArrayList<>();
                    }
                    else{
                        arregloBytes.add(buffer[0]);
                        contador++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void write(String msg) {
        try {
            msg=msg+">";
            byte[] bytesMsg = msg.getBytes(StandardCharsets.UTF_8);
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