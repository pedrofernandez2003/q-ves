package com.example.login_crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Objetos.Tarjeta;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText mailIngresado, contraseniaIngresada;
    Button botonIngresar, botonRegistrarse,boton;
    AwesomeValidation awesomeValidation;
    private static final String TAG= "Login";
    private static final int RC_SIGN_IN=1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton googleLogin = findViewById(R.id.gmail);
        googleLogin.setOnClickListener(handleGoogleLogin);

        LoginButton loginButton = findViewById(R.id.login_button);
        mailIngresado = (EditText) findViewById(R.id.mail);
        contraseniaIngresada = (EditText) findViewById(R.id.contrasenia);
        botonIngresar = (Button) findViewById(R.id.confirmarInicio);
        botonRegistrarse = (Button) findViewById(R.id.registrarseConMail);
        boton = (Button) findViewById(R.id.olvideContrasenia);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cambiarContrasenia = new Intent(LoginActivity.this, CambiarContraseniaActivity.class);
                startActivity(cambiarContrasenia);
            }
        });
        botonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrarse = new Intent(LoginActivity.this, RegistrarseActivity.class);
                startActivity(registrarse);
            }
        });

        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mailIngresado.getText().toString();
                String pass = contraseniaIngresada.getText().toString();

                if (!mail.isEmpty() & !pass.isEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        manejarUsuario(firebaseAuth.getCurrentUser());

                                        // Intent i = new Intent(LoginActivity.this, AdministradorActivity.class);
                                        // startActivity(i);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Todo mal", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                Toast.makeText(getApplicationContext(), "El email o la contraseña estan vacios", Toast.LENGTH_SHORT).show();
            }
        });


        
        //Registering callback!
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Sign in completed
                Log.i(TAG, "onSuccess: logged in successfully");

                //handling the token for Firebase Auth
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user!=null){
            manejarUsuario(firebaseAuth.getCurrentUser());
        }
        super.onStart();
    }



    private View.OnClickListener handleGoogleLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            signInGoogle();
        }
    };

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            manejarUsuario(firebaseAuth.getCurrentUser());


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


        
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void manejarUsuario(FirebaseUser user){
        DataManager.getDb().collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    boolean estaEnLaBase=false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        System.out.println(document.getData().get("UID"));
                        if (document.getData().get("UID").equals(user.getUid())){
                            estaEnLaBase=true;
                            if (document.getData().get("ocupacion").equals("Administrador")){
                                Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }
                            else{
                                Intent intent = new Intent(getApplicationContext(), ModeradorActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }
                        }

                    }
                    if(!estaEnLaBase){
                        elegirTrabajo();
                    }
                }
            }
        });
    }
    private void insertarUsuario(String ocupacion){
        Map<String,String> usuarioAIngresar= new HashMap<>();
        usuarioAIngresar.put("UID",firebaseAuth.getCurrentUser().getUid());
        usuarioAIngresar.put("ocupacion",ocupacion);
        DataManager.getDb().collection("usuarios").add(usuarioAIngresar)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (ocupacion.equals("Administrador")){
                            Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), ModeradorActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NotNull Exception e) {
                        System.out.println(e);
                    }
                });
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            manejarUsuario(firebaseAuth.getCurrentUser());

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Sorry authentication failed ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void elegirTrabajo() {

        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View dialog_layout = inflater.inflate(R.layout.elegir_trabajo, null);
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setView(dialog_layout);
        Button moderador=dialog_layout.findViewById(R.id.moderador);
        Button administrador=dialog_layout.findViewById(R.id.administrador);
        db.setTitle("Elegir trabajo");
        final AlertDialog a = db.create();
        a.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                moderador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        insertarUsuario("Moderador");
                        a.dismiss();
                    }
                });

                administrador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        insertarUsuario("Administrador");
                        a.dismiss();
                    }
                });
            }
        });
        a.show();

    }
}




