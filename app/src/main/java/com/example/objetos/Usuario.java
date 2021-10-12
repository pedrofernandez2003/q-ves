package com.example.objetos;

public class Usuario {
    private String mail;
    private String rol;
    private static Usuario context;

    private Usuario(){}
    public static Usuario getUsuario() {
        if (context == null) {
            context = new Usuario();
        }
        return context;
    }

    public String getMail() {
        return mail;
    }

    public String getRol() {
        return rol;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
