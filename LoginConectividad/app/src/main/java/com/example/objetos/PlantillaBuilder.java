package com.example.objetos;

public class PlantillaBuilder {
    private Plantilla plantilla;

    public PlantillaBuilder(){
        this.plantilla = new Plantilla();
    }

    public Plantilla build(int cantEquipos ){ //??
        plantilla.setCantEquipos(cantEquipos);
        return plantilla;
    }
    
}
