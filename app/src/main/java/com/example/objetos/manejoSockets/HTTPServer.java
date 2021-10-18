package com.example.objetos.manejoSockets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;

public class HTTPServer extends NanoHTTPD {

    public HTTPServer() throws IOException {
        super(5880);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static void main(String[] args) {
        try {
            new HTTPServer();
        } catch (IOException ioe) {

        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        FileInputStream fis = null;
        File f = null;
        try {
            f = new File("data/user/0/com.example.login_crud/app_personajes/", session.getParameters().get("imagen").get(0)+".png");
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            return newFixedLengthResponse(Response.Status.OK, "image/png", fis, fis.available());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}