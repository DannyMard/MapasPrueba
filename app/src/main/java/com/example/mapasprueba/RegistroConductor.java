package com.example.mapasprueba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegistroConductor extends AppCompatActivity implements  View.OnClickListener{


    private EditText txtUser;
    private EditText txtPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private  Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_conductor);


        mAuth = FirebaseAuth.getInstance();

        //referenciamos
        txtUser = (EditText) findViewById(R.id.tv_email);
        txtPassword = (EditText) findViewById(R.id.tv_passwordConductor);
                btnIniciarSesion = (Button) findViewById(R.id.btn_login);

        progressDialog = new ProgressDialog(this);


        //Asociamos un oyente al evento onClick

        btnIniciarSesion.setOnClickListener(this);


    }



    private  void iniciarSesion(){

        final String email = txtUser.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        //verificamos que las cajas de texto no esten vacias
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Se debe ingresar email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Se debe ingresar password", Toast.LENGTH_SHORT).show();
            return;
        }

        //barra de progreso
        progressDialog.setMessage("Realizando consulta en linea...");
        progressDialog.show();
        //iniciar secion
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //
                        if(task.isSuccessful()){
                            //mostramos solo los caranteres antes del @
                            int posicion = email.indexOf("@");
                            String user= email.substring(0,posicion);

                            Toast.makeText(RegistroConductor.this, "bienvenido "+ txtUser.getText(), Toast.LENGTH_LONG).show();
                            //pasamos de actividad
                            Intent ubicacion = new Intent(getApplication(),Ubicaciones.class);
                            ubicacion.putExtra(Ubicaciones.user, user);
                            startActivity(ubicacion);


                        } else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){//verificamos si hay alguna colision
                                Toast.makeText(RegistroConductor.this, "El Usuario ya existe", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(RegistroConductor.this, "No se ha registrado el email", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_login:
                iniciarSesion();

                break;
        }


    }

}
