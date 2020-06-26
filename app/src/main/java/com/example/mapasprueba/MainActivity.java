package com.example.mapasprueba;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnAdministrador;
    private ProgressDialog progressDialog;
    private  Button btnConductor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //referenciamos
        btnAdministrador = (Button) findViewById(R.id.btn_administrador);
        btnConductor = (Button) findViewById(R.id.btn_Conductor);

        progressDialog = new ProgressDialog(this);


        //Asociamos un oyente al evento onClick
        btnAdministrador.setOnClickListener(this);
        btnConductor.setOnClickListener(this);


    }

    private void admin(){
        Intent ubicacion = new Intent(getApplication(),AdministradorPassword.class);
        startActivity(ubicacion);
    }


    private void Driver(){
        Intent ubicacion = new Intent(getApplication(),RegistroConductor.class);
        startActivity(ubicacion);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_administrador:
                //invocamos al metodo
                admin();
                break;
            case R.id.btn_Conductor:
                Driver();

            break;
        }


    }
}


