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

    public SendReceive(Socket skt) {
        System.out.println("entre al constructor");
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuff = (byte[]) msg.obj;
                        String tempMsg = new String(readBuff, 0, msg.arg1);
                        System.out.println("mensaje recibido "+tempMsg);
                        try {
                            Gson json = new Gson();
//                        Mensaje mensaje = json.fromJson(tempMsg, Mensaje.class);
//                        Juego juego = json.fromJson(mensaje.getDatos().get(0), Juego.class);
//                        Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
//                        empezarJuego();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        socket = skt;
        try {
            System.out.println("se construyo el sendReceive");
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("entre al catch");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (socket != null) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();//este handler deberia estar en el servicio
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
}