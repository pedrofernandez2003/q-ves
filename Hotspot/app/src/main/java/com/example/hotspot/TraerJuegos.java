package com.example.hotspot;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class TraerJuegos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traer_juegos);
        mostrarPlantillas(this.getApplicationContext());
    }

    private void mostrarPlantillas(Context context)  {
        DataManagerPlantillas.traerPlantillas(new onTraerDatosListener() {
            @Override
            public void traerDatos(ArrayList<Object> datos) {
                for (Object PlantillaObject:datos) {
                    Plantilla plantilla= (Plantilla) PlantillaObject;
                    LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
                    Button button = new Button(context);
                    button.setLayoutParams(lp);
                    button.setText(plantilla.getNombre());
                    button.setBackgroundColor(939393);
                    llBotonera.addView(button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("deberia comenzar la partida");
//                            Intent irATarjetas = new Intent(TraerJuegos.this, TarjetasActivity.class);
//                            irATarjetas.putExtra("Color",categoria.getColor().getCodigo());
//                            irATarjetas.putExtra("Nombre",categoria.getNombre());
//                            startActivity(irATarjetas);
                        }
                    });
                }
            }
        });
    }

}
