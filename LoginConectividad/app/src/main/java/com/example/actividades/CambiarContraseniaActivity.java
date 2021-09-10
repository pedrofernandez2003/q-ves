package com.example.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.R;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CambiarContraseniaActivity extends AppCompatActivity {
    private Button botonConfirmar,botonCancelar;
    private EditText mail;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasenia);

        firebaseAuth=FirebaseAuth.getInstance();

        mail= (EditText) findViewById(R.id.mail);
        botonConfirmar= (Button) findViewById(R.id.confirmar);
        botonCancelar= (Button) findViewById(R.id.cancelar);

        botonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail( mail.getText().toString() );
                Toast.makeText(CambiarContraseniaActivity.this,"Ya le mandamos un mail",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CambiarContraseniaActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CambiarContraseniaActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }
}
