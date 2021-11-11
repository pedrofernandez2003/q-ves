package com.example.listeners;

import com.example.personajesEnMiniaturas.utils.PicHolder;
import com.example.personajesEnMiniaturas.utils.PictureFacer;

import java.util.ArrayList;


public interface itemClickListener {


    void onPicClicked(PicHolder holder, int position, ArrayList<PictureFacer> pics);
}
