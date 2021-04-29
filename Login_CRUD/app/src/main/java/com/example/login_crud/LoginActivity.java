package com.example.login_crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class LoginActivity extends AppCompatActivity {
    EditText mailIngresado, contraseniaIngresada;
    Button botonIngresar, botonRegistrarse;
    AwesomeValidation awesomeValidation;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();


        mailIngresado = (EditText) findViewById(R.id.mail);
        contraseniaIngresada = (EditText) findViewById(R.id.contrasenia);
        botonIngresar = (Button) findViewById(R.id.confirmarInicio);
        botonRegistrarse = (Button) findViewById(R.id.registrarseConMail);

        botonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrarse = new Intent(LoginActivity.this, RegistrarseActivity.class);
                startActivity(registrarse);
            }
        });
    /*
                btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail=et_mail.getText().toString();
                String pass=et_pass.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(mail,pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(MainActivity.this,HomeActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(MainActivity.this,"Todo mal",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        
        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = mailIngresado.getText().toString();
                String pass = contraseniaIngresada.getText().toString();

                if (awesomeValidation.validate()) {
                    firebaseAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(RegistrarActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                finish();


                            } else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                dameToastdeerror(errorCode);
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegistrarActivity.this, "Completa todos los datos..!!", Toast.LENGTH_SHORT).show();
                }


            }
        });*/


    }
}




