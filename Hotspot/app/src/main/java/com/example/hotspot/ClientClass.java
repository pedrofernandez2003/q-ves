package com.example.hotspot;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientClass extends Thread{
    Socket socket;
    String hostAdd;

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
            GameContext.agregarHijo(sendReceive);
//            ServicioJuego.handler.obtainMessage().sendToTarget(); cuando se conecta al server, llamaria al handler del servicio
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
