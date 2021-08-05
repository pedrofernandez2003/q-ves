package com.example.hotspot;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameContext extends Thread {
    private static GameContext context;
    private static ThreadedEchoServer server;
    public List<ThreadedEchoServer> servers = new ArrayList<ThreadedEchoServer>();

    private GameContext(){}
    public static GameContext getGameContext() {
        //instantiate a new CustomerLab if we didn't instantiate one yet
        if (server == null) {
            mCustLab = new CustomerLab();
        }
        return mCustLab;
    }

}
