package com.example.objetos.manejoSockets;

import android.os.AsyncTask;

import com.example.objetos.GameContext;

public class Write extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            GameContext.getHijos().get((Integer) objects[1]).write((String) objects[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}