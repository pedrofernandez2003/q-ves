package com.example.login_crud;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.Listeners.onTraerDatosListener;
import com.example.Objetos.Categoria;

import java.util.ArrayList;

public class CrearJuegoActivity extends AppCompatActivity  {
    Button mOrder;
    TextView mItemSelected;
    ArrayList<String> listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_juego);

    mOrder = (Button) findViewById(R.id.btnOrder);
    ArrayList<String> nombresCategoria = new ArrayList<>();
    mItemSelected = (TextView) findViewById(R.id.tvItemSelected);
    DataManagerCategoria.traerCategorias(new onTraerDatosListener() {
        @Override
        public void traerDatos(ArrayList<Object> datos) {
            checkedItems = new boolean[datos.size()];
            for (Object dato: datos) {
                Categoria categoria= (Categoria) dato;
                nombresCategoria.add(categoria.getNombre());
            }
            listItems = nombresCategoria;
        }
    });


    mOrder.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CrearJuegoActivity.this);
            mBuilder.setTitle(R.string.dialog_title);
            mBuilder.setMultiChoiceItems(listItems.toArray(new String[0]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                    if(isChecked){
                        mUserItems.add(position);
                    }else{
                        mUserItems.remove((Integer.valueOf(position)));
                    }
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    String item = "";
                    for (int i = 0; i < mUserItems.size(); i++) {
                        item = item + listItems.get(mUserItems.get(i));
                        if (i != mUserItems.size() - 1) {
                            item = item + ", ";
                        }
                    }
                    mItemSelected.setText(item);
                }
            });

            mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    for (int i = 0; i < checkedItems.length; i++) {
                        checkedItems[i] = false;
                        mUserItems.clear();
                        mItemSelected.setText("");
                    }
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }
    });
}
}



