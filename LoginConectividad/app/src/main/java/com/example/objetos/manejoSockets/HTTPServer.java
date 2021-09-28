package com.example.objetos.manejoSockets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class HTTPServer extends NanoHTTPD {

    public HTTPServer() throws IOException {
        super(5880);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    public static void main(String[] args) {
        try {
            new HTTPServer();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        FileInputStream fis = null;
        File f = null;
        try {
            f = new File("data/user/0/com.example.login_crud/app_personajes/", session.getParameters().get("imagen").get(0)+".png");
//            f = new File("/data/user/0/com.example.login_crud/app_personajes/", "insa_imagen_1.png");

            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            return newFixedLengthResponse(Response.Status.OK, "image/png", fis, fis.available());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//                String msg = "<html><body><h1>Hello server</h1>\n";
//        Map<String, String> parms = session.getParms();
//        if (parms.get("username") == null) {
//            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
//        } else {
//            msg += "<p>Hello, " + parms.get("username") + "!</p>";
//        }
//        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}