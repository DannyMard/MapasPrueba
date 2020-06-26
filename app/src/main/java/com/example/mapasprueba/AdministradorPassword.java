package com.example.mapasprueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdministradorPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText txtPassword;
    private EditText txtadmin;
    private Button btnIngresar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_password);


        txtPassword = (EditText)findViewById(R.id.tv_passwordAdmin);
        txtadmin = (EditText)findViewById(R.id.tv_admin);
        btnIngresar = (Button)findViewById(R.id.btn_InicioAdmin);

        btnIngresar.setOnClickListener(this);
    }

    private  void ingreso() {

        String password = txtPassword.getText().toString().trim();
        String admin = txtadmin.getText().toString().trim();

        //verificamos que las cajas de texto no esten vacias

        if (admin.equals("admin") && password.equals("admin")){

            Intent  siguiente = new Intent(getApplicationContext(), Registro.class);
            startActivity(siguiente);

        }else
        {
            Toast.makeText(getApplicationContext(), "Usuario o contraseña erronea", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
    ingreso();
    }
}
