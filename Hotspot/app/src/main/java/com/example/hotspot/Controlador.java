package com.example.hotspot;

import java.util.HashMap;

public class Controlador {
    private Juego juego;
    private String id;

    public Controlador(){
        juego=new Juego();
        id="0";
    }

    public void elegirAccion(HashMap <String, Object> mensaje){ //
        switch(mensaje.get("ACCION").toString()){
            case "TURNO":
                break;
            case "COMENZAR":
                System.out.println("entre comenzar");
                break;
            case "JUGADA":
                break;
            case "ACTUALIZACION_TABLERO":
                break;
            case "REINICIAR_PARTIDA":
                break;
            case "TERMINAR_JUEGO":
                break;
            case "LEVANTAR_TARJETA":
                break;
            case "PING":
                System.out.println("entre ping");
                break;
            case "ACK":
                break;
            case "LISTO_PARA_EMPEZAR":
                break;
        }
    }

    public void hacerTurno(){
        System.out.println("hace el turno el jugador");
    }
}
