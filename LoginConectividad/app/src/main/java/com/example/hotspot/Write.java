package com.example.hotspot;

import android.os.AsyncTask;

public class Write extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            System.out.println(GameContext.getHijos().get((Integer) objects[1]).callbackMensaje);
            GameContext.getHijos().get((Integer) objects[1]).write((String) objects[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}