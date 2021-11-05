package com.example.actividades;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.objetos.Equipo;
import com.example.objetos.GameContext;
import com.example.R;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.objetos.ServicioJuego;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private DhcpInfo dhcpInfo;
    private Button botonUnirse, botonIniciarSesion;
    private TextInputEditText nombreEquipo;
    private FloatingActionButton cerrarSesion;
    FirebaseAuth firebase;
    private static String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("comenzar");
        intentFilter.addAction("turno");
        registerReceiver(broadcastReceiver,intentFilter);
        startService(new Intent(this, ServicioJuego.class));
        setContentView(R.layout.activity_main);

        if(reanudarPartida()){
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

            View dialog_layout = inflater.inflate(R.layout.confirmacion_accion, null);
            TextView seleccionado = dialog_layout.findViewById(R.id.confirmarSelección);

            AlertDialog.Builder db = new AlertDialog.Builder(MainActivity.this);
            db.setView(dialog_layout);
            db.setTitle("reanudar partida");
            db.setPositiveButton("Sí", null);
            db.setNegativeButton("No", null);
            db.setNeutralButton("Borrar", null);
            final AlertDialog a = db.create();

            a.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button si = a.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button no = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                    Button borrar = a.getButton(AlertDialog.BUTTON_NEUTRAL);
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //q borre al archivo
                            a.dismiss();
                        }
                    });
                    si.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View view){//se abre el hotspot, se crea el juego y el server
                            //chequear hotspot
                            Intent intent = new Intent(MainActivity.this, JugarActivity.class);
                            intent.putExtra("reanudar",true);
                            startActivity(intent);
                            finish();
                            a.dismiss();
                        }
                    });
                    borrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            borrarAutoguardado();
                            a.dismiss();
                        }
                    });
                }
            });
            a.show();
        }
        cerrarSesion = findViewById(R.id.fab);
        botonUnirse = (Button) findViewById(R.id.botonUnirse);
        nombreEquipo =  (TextInputEditText) findViewById(R.id.nombreEquipo);
        botonIniciarSesion=(Button)findViewById(R.id.iniciarSesion);
        ActivityCompat.requestPermissions(this, permissionstorage, 1);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Context appContext=this;
        firebase = FirebaseAuth.getInstance();
        if (firebase.getCurrentUser()==null){//si no inicio sesion no muestra el boton
            cerrarSesion.setVisibility(View.INVISIBLE);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleOnBackPressed() {//Saca la aplicación
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };

        this.getOnBackPressedDispatcher().addCallback(this, callback);

        botonUnirse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dhcpInfo=wifiManager.getDhcpInfo();
                String codigo = formatIP(dhcpInfo.gateway);
                if (!codigo.equals("0.0.0.0")){ //si no es el server
                    Intent intent= new Intent();
                    intent.setAction("unirse");
                    intent.putExtra("codigo",codigo);
                    appContext.sendBroadcast(intent);
                    GameContext.getNombresEquipos().add(nombreEquipo.getText().toString());
                    setContentView(R.layout.cargando);
                    volverAtras();
                }
                else {
                    Toast.makeText(getApplicationContext(), "No está conectado a la red", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View dialog_layout = inflater.inflate(R.layout.confirmacion_accion, null);
                TextView seleccionado = dialog_layout.findViewById(R.id.confirmarSelección);
                seleccionado.setText("¿Quiere cerrar la sesión?");
                AlertDialog.Builder db = new AlertDialog.Builder(MainActivity.this);
                db.setView(dialog_layout);
                db.setTitle("Cerrar sesión");
                db.setPositiveButton("Sí", null);
                db.setNegativeButton("Cancelar", null);
                final AlertDialog a = db.create();

                a.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button si = a.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button no = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                a.dismiss();
                            }
                        });
                        si.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick (View view){
                                firebase.signOut();
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
                a.show();
            }
        });

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent elegirPlantilla = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(elegirPlantilla);
                finish();
            }
        });
    }

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "comenzar":
                    GameContext.setServer(null); //esto despues lo usamos para diferenciar entre server y cliente
                    empezarJuego();
                    break;
            }
        }
    };

    public String formatIP(int IpAddress) {
        return Formatter.formatIpAddress(IpAddress);
    }

    public void volverAtras() {//para que cuando vayamos para atras se quede en la app
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void handleOnBackPressed() {
                    Intent intent=new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };

            this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void empezarJuego(){
        unregisterReceiver(broadcastReceiver);
        Intent partida = new Intent(this, JugarActivity.class);
        partida.putExtra("reanudar", false);
        startActivity(partida);
        finish();
    }
    private boolean borrarAutoguardado(){
        String fullPath = "data/user/0/com.example.login_crud/app_personajes/plantillas";
        try {
            File file = new File(fullPath, "Autoguardado.qves");
            if(file.exists()) {
                return file.delete();
            }
        }
        catch (Exception e) {
            Log.e("App", "Exception while deleting file " + e.getMessage());
        }
        return false;
    }

    private boolean reanudarPartida(){
        File file = new File("data/user/0/com.example.login_crud/app_personajes/plantillas/Autoguardado.qves");
        return file.exists();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}