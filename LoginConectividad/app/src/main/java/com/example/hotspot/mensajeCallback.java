package com.example.hotspot;

import android.os.Message;

import androidx.annotation.NonNull;

public interface mensajeCallback {
    void mensajeRecibido(int estado, int bytes, int argumento, String buffer);//creo que se podrian borrar los primeros 3 parametros
}