package com.example.login_crud;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class RegistrarseActivity extends AppCompatActivity {

    EditText mailIngresado,contraseniaIngresada;
    Button botonRegistrarse;
    AwesomeValidation awesomeValidation;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // aca defino las dos variables para hacer el registro firebaseAuth que es la comunicacion con firebase donde lo registro
        // y awasomeValidation para que no se repitan mal y otras cosas
        firebaseAuth= FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.mail, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);
        awesomeValidation.addValidation(this,R.id.contrasenia,".{6,}",R.string.invalid_password);

        mailIngresado = (EditText) findViewById(R.id.mailRegistro);
        contraseniaIngresada = (EditText) findViewById(R.id.contraseniaRegistro);
        botonRegistrarse = (Button) findViewById(R.id.registrarse);

        botonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = mailIngresado.getText().toString();
                String pass = contraseniaIngresada.getText().toString();

                if(awesomeValidation.validate()){
                    firebaseAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(RegistrarseActivity.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegistrarseActivity.this,"registrado!",Toast.LENGTH_SHORT).show();
                                finish();


                            }else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                dameToastdeerror(errorCode);
                            }
                        }
                    });
                }else {
                    Toast.makeText(RegistrarseActivity.this, "Completa todos los datos..!!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void dameToastdeerror(String error) {

        switch (error) {


            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(RegistrarseActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(RegistrarseActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                mailIngresado.setError("La dirección de correo electrónico está mal formateada.");
                mailIngresado.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(RegistrarseActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                mailIngresado.setError("la contraseña es incorrecta ");
                mailIngresado.requestFocus();
                mailIngresado.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(RegistrarseActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(RegistrarseActivity.this,"Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(RegistrarseActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(RegistrarseActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                mailIngresado.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                mailIngresado.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(RegistrarseActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(RegistrarseActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(RegistrarseActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(RegistrarseActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;


            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(RegistrarseActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(RegistrarseActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                contraseniaIngresada.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                contraseniaIngresada.requestFocus();
                break;

        }

    }
}
