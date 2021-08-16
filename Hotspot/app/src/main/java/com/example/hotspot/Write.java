package com.example.hotspot;

import android.os.AsyncTask;

public class Write extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            GameContext.getHijos().get((Integer) objects[1]).write((byte[]) objects[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}