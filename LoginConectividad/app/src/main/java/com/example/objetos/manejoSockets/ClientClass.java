package com.example.objetos.manejoSockets;

import com.example.interfaces.conectarCallback;
import com.example.interfaces.mensajeCallback;
import com.example.objetos.GameContext;

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
                public void mensajeRecibido(int estado, String buffer) {
                    callbackMensaje.conectar(estado,buffer);
                }
            };
            GameContext.agregarHijo(sendReceive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
