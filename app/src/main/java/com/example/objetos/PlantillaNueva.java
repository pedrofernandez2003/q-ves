package com.example.objetos;

import java.util.ArrayList;

public class PlantillaNueva {
    private static PlantillaNueva context;
    private static String nombrePlantilla;
    private static String cantidadEquipos;
    private static String usuario;
    private static ArrayList<String>  categoriasElegidas = new ArrayList<>();
    private static ArrayList<String> categorias = new ArrayList<>();
    private static ArrayList<String> urls = new ArrayList<>() ;


    private PlantillaNueva() {
    }

    public static PlantillaNueva getContext() {
        return context;
    }

    public static void setContext(PlantillaNueva context) {
        PlantillaNueva.context = context;
    }

    public static String  getNombrePlantilla() {
        return nombrePlantilla;
    }

    public static void setNombrePlantilla(String nombrePlantilla) {
        PlantillaNueva.nombrePlantilla = nombrePlantilla;
    }

    public static String getCantidadEquipos() {
        return cantidadEquipos;
    }

    public static void setCantidadEquipos(String cantidadEquipos) {
        PlantillaNueva.cantidadEquipos = cantidadEquipos;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static void setUsuario(String usuario) {
        PlantillaNueva.usuario = usuario;
    }

    public static ArrayList<String>  getCategoriasElegidas() {
        return categoriasElegidas;
    }

    public static void setCategoriasElegidas(ArrayList<String>  categoriasElegidas) {
        PlantillaNueva.categoriasElegidas = categoriasElegidas;
    }

    public static ArrayList<String> getCategorias() {
        return categorias;
    }

    public static void setCategorias(ArrayList<String> categorias) {
        PlantillaNueva.categorias = categorias;
    }

    public static ArrayList<String> getUrls() {
        return urls;
    }

    public static void setUrls(ArrayList<String> urls) {
        PlantillaNueva.urls = urls;
    }

    public static PlantillaNueva obtenerPlantilla(){
        if(context == null){
            context = new PlantillaNueva();
        }
        return context;
    }
}
