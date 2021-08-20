package com.example.hotspot;

import android.os.Message;

import androidx.annotation.NonNull;

public interface mensajeCallback {
    void mensajeRecibido(int estado, int bytes, int argumento, byte[] buffer);
}
