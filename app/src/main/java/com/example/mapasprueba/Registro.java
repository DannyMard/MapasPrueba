package com.example.mapasprueba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity implements View.OnClickListener{

    private EditText txtNombreC;
    private EditText txtnroBus;
    private EditText txtCooperativa;
    private EditText txtRutaC;
    private EditText txtHorario;
    private EditText txtCelular;
    public EditText txtEmail;
    public EditText txtContraseña;
    private Button btnRegistrar;

    //variables de registro

    private String nombre =  "";
    private String bus =  "";
    private String cooperativa =  "";
    private String ruta =  "";
    private String horario =  "";
    private String celular =  "";
    private String contraseña =  "";
    private String email =  "";

    // Variables de Firebase

    public  FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtNombreC = (EditText)findViewById(R.id.tv_nombreConductor);
        txtnroBus = (EditText)findViewById(R.id.tv_numBUs);
        txtCooperativa = (EditText)findViewById(R.id.tv_cooperativa);
        txtRutaC = (EditText)findViewById(R.id.tv_Ruta);
        txtHorario = (EditText)findViewById(R.id.tv_horario);
        txtCelular = (EditText)findViewById(R.id.tv_celular);
        txtEmail = ( EditText)findViewById(R.id.tv_email);
        txtContraseña = (EditText)findViewById(R.id.tv_passwordConductor);
        btnRegistrar = (Button)findViewById(R.id.btn_registrar);

        btnRegistrar.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    private void registroDatos(){

        nombre = txtNombreC.getText().toString().trim();
        bus = txtnroBus.getText().toString().trim();
        cooperativa = txtCooperativa.getText().toString().trim();
        ruta = txtRutaC.getText().toString().trim();
        horario = txtHorario.getText().toString().trim();
        celular = txtCelular.getText().toString().trim();
        email = txtEmail.getText().toString().trim();
        contraseña = txtContraseña.getText().toString().trim();

        // preguntamos si los campos no estan vacios

        if(!nombre.isEmpty() && !bus.isEmpty() && !cooperativa.isEmpty() && !ruta.isEmpty()
        && !horario.isEmpty() && !celular.isEmpty() && !email.isEmpty() && !contraseña.isEmpty()) {

            if(contraseña.length() >= 6 ){

                if(celular.length() == 10){

                    registrarUsuario();
                    progressDialog.setMessage("Registrando Conductor...");
                    progressDialog.show();

                }else {
                    Toast.makeText(getApplicationContext(), "El nro debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(getApplicationContext(), "La contraseña debe contener almenos 6 caracteres", Toast.LENGTH_SHORT).show();
            }



        }

        else {
            Toast.makeText(getApplicationContext(), "Debe llenar todos los Datos", Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();

    }

    private void registrarUsuario() {
        mAuth.createUserWithEmailAndPassword(email,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    //traemos los valores que queremos almacenar en la base de datos

                    HashMap<String, Object> datos = new HashMap<>();
                    datos.put("Nombre", nombre);
                    datos.put("Nro Bus", bus);
                    datos.put("Cooperativa", cooperativa);
                    datos.put("Ruta", ruta);
                    datos.put("Horario", horario);
                    datos.put("Celular", celular);
                    datos.put("Email", email);
                    datos.put("Contraseña", contraseña);

                    String userId = mAuth.getCurrentUser().getUid();

                    mDatabase.child("Conductores").child(userId).setValue(datos).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){

                                Toast.makeText(getApplicationContext(), "El conductor fue registrado", Toast.LENGTH_SHORT).show();

                                Intent principal = new Intent(getApplication(), MainActivity.class);
                                startActivity(principal);
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), "Los datos no fueron registrados", Toast.LENGTH_SHORT).show();
                            }
                            
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "NO se pudo registrar el usuario Verifique los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        registroDatos();
    }
}
