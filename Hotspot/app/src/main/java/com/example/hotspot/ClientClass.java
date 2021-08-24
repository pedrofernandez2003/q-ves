package com.example.hotspot;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientClass extends Thread{
    Socket socket;
    String hostAdd;
    public conectarCallback callbackMensaje;

    public ClientClass(String hostAddress) {
        hostAdd = hostAddress;
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            System.out.println("entre al run client");
            socket.connect(new InetSocketAddress(hostAdd, 7028), 5000);
            SendReceive sendReceive = new SendReceive(socket);
            sendReceive.start();
            sendReceive.callbackMensaje= new mensajeCallback() {
                @Override
                public void mensajeRecibido(int estado, int bytes, int argumento, byte[] buffer) {
                    callbackMensaje.conectar(estado, bytes, argumento,buffer);
                }
            };
            GameContext.agregarHijo(sendReceive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
