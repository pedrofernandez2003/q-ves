package com.example.Objetos;

import java.util.ArrayList;

public class Plantilla {
    private static Plantilla context;
    private static String nombrePlantilla;
    private static String cantidadEquipos;
    private static ArrayList<String>  categorias;
    private static ArrayList<String> urls;


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

    public static ArrayList<String>  getCategorias() {
        return categorias;
    }

    public static void setCategorias(ArrayList<String>  categorias) {
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
