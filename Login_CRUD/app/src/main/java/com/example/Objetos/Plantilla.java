package com.example.Objetos;

import java.util.ArrayList;

public class Plantilla {
    private static Plantilla context;
    private static String nombrePlantilla;
    private static String cantidadEquipos;
    private static String usuario;
    private static ArrayList<String>  categoriasElegidas = new ArrayList<>();
    private static ArrayList<String> categorias = new ArrayList<>();
    private static ArrayList<String> urls = new ArrayList<>() ;


    private Plantilla() {
    }

    public static Plantilla getContext() {
        return context;
    }

    public static void setContext(Plantilla context) {
        Plantilla.context = context;
    }

    public static String  getNombrePlantilla() {
        return nombrePlantilla;
    }

    public static void setNombrePlantilla(String nombrePlantilla) {
        Plantilla.nombrePlantilla = nombrePlantilla;
    }

    public static String getCantidadEquipos() {
        return cantidadEquipos;
    }

    public static void setCantidadEquipos(String cantidadEquipos) {
        Plantilla.cantidadEquipos = cantidadEquipos;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static void setUsuario(String usuario) {
        Plantilla.usuario = usuario;
    }

    public static ArrayList<String>  getCategoriasElegidas() {
        return categoriasElegidas;
    }

    public static void setCategoriasElegidas(ArrayList<String>  categoriasElegidas) {
        Plantilla.categoriasElegidas = categoriasElegidas;
    }

    public static ArrayList<String> getCategorias() {
        return categorias;
    }

    public static void setCategorias(ArrayList<String> categorias) {
        Plantilla.categorias = categorias;
    }

    public static ArrayList<String> getUrls() {
        return urls;
    }

    public static void setUrls(ArrayList<String> urls) {
        Plantilla.urls = urls;
    }

    public static Plantilla obtenerPlantilla(){
        if(context == null){
            context = new Plantilla();
        }
        return context;
    }
}
