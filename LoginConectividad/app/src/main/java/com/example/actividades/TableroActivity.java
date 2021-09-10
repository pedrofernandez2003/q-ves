package com.example.actividades;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.objetos.Categoria;
import com.example.objetos.Tarjeta;
import com.example.R;

import java.util.ArrayList;

public class TableroActivity extends AppCompatActivity {
    private LinearLayout verCartas;
    private Tarjeta tarjetaElegida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablero_creable);
        verCartas = findViewById(R.id.verCartas);

        ArrayList<Categoria> categorias= new ArrayList<>();
        ArrayList<Tarjeta> tarjetasArray= new ArrayList<>();
        ArrayList<Tarjeta> ejemploParaRellenar= new ArrayList<>();

        Tarjeta tarjeta1=new Tarjeta("prueba1","probandoooooo","categoria1");
        Tarjeta tarjeta2=new Tarjeta("prueba2","probandoooooo","categoria1");
        Tarjeta tarjeta3=new Tarjeta("prueba3","probandoooooo","categoria2");
        Tarjeta tarjeta4=new Tarjeta("prueba4","probandoooooo","categoria2");
        Tarjeta tarjeta5=new Tarjeta("prueba5","probandoooooo","categoria3");
        Tarjeta tarjeta6=new Tarjeta("prueba6","probandoooooo","categoria3");


        Categoria categoria1= new Categoria("categoria1","AMARILLO",3,ejemploParaRellenar);
        Categoria categoria2= new Categoria("categoria2","AZUL",3,ejemploParaRellenar);
        Categoria categoria3= new Categoria("categoria3","VERDE",3,ejemploParaRellenar);

        categorias.add(categoria1);
        categorias.add(categoria2);
        categorias.add(categoria3);

        tarjetasArray.add(tarjeta1);
        tarjetasArray.add(tarjeta2);
        tarjetasArray.add(tarjeta3);
        tarjetasArray.add(tarjeta4);
        tarjetasArray.add(tarjeta5);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);
        tarjetasArray.add(tarjeta6);


        verCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("tocaste mandar");

                LayoutInflater inflater = LayoutInflater.from(TableroActivity.this);
                View dialog_layout = inflater.inflate(R.layout.ver_cartas, null);
                AlertDialog.Builder db = new AlertDialog.Builder(TableroActivity.this);
                db.setView(dialog_layout);
                LinearLayout contenedorCartas=(LinearLayout) dialog_layout.findViewById(R.id.contenedorCartas);

                for (int i = 0; i < tarjetasArray.size(); i++) {
                    Tarjeta tarjetaARevisar=tarjetasArray.get(i);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT );
                    Button button = new Button(getApplicationContext());
                    button.setLayoutParams(lp);
                    button.setText(tarjetaARevisar.getContenido());

                    for (int j=0; j < categorias.size(); j++){
                        Categoria categoriaARevisar=categorias.get(j);
                        if (categoriaARevisar.getNombre().equals(tarjetaARevisar.getCategoria())){
                            button.setBackgroundColor(categoriaARevisar.getColor().getCodigo());
                        }
                    }
                    contenedorCartas.addView(button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tarjetaElegida=tarjetaARevisar;
                            System.out.println(tarjetaARevisar.getContenido());

                            //cuando tenga las tarjetas correctas hacer esto de "Seleccionarlas"

                        }
                    });
                }

                db.setPositiveButton("Enviar al tablero", null);
                db.setNegativeButton("Atras", null);
                final AlertDialog a = db.create();
                a.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = a.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                insertarTarjetaEnTablero();
                                a.dismiss();

                            }
                        });
                    }
                });
                a.show();





            }
        });
    }

    public void insertarTarjetaEnTablero(){
        // IDEA: deberia buscar entre todos los text para ver cual corresponde
        // con la categoria y cambiar algo como para decir "Estoy aca :D"

        ArrayList<TextView> nombreCategoriasEnTablero=conseguirTextViews();

        for (TextView nombreCategoria:nombreCategoriasEnTablero) {
            System.out.println("Hola");
            if (tarjetaElegida.getCategoria().equals(nombreCategoria.getText())){
                String prueba="Meti carta de esto";
                nombreCategoria.setText(prueba);
            }
        }

    }

    //total despues esto va en Jugar asi que esto no importa
    public ArrayList<TextView> conseguirTextViews() {
        ArrayList<TextView> espaciosTexto = new ArrayList<>();
        espaciosTexto.add(findViewById(R.id.icon_name_1));
        espaciosTexto.add(findViewById(R.id.icon_name_2));
        espaciosTexto.add(findViewById(R.id.icon_name_3));
        espaciosTexto.add(findViewById(R.id.icon_name_4));
        espaciosTexto.add(findViewById(R.id.icon_name_5));
        espaciosTexto.add(findViewById(R.id.icon_name_6));
        espaciosTexto.add(findViewById(R.id.icon_name_7));
        espaciosTexto.add(findViewById(R.id.icon_name_8));
        espaciosTexto.add(findViewById(R.id.icon_name_9));
        espaciosTexto.add(findViewById(R.id.icon_name_10));
        return espaciosTexto;
    }
}
