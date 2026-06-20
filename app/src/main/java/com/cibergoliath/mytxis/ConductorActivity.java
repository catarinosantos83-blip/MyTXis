package com.cibergoliath.mytxis;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConductorActivity extends AppCompatActivity {

    TextView txtEstado;
    SwitchMaterial switchDisponible;

    TextView txtCliente;
    TextView txtOrigen;
    TextView txtDestino;

    Button btnActualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conductor);

        txtEstado = findViewById(R.id.txtEstado);
        switchDisponible = findViewById(R.id.switchDisponible);

        txtCliente = findViewById(R.id.txtCliente);
        txtOrigen = findViewById(R.id.txtOrigen);
        txtDestino = findViewById(R.id.txtDestino);


        btnActualizar = findViewById(R.id.btnActualizar);



        switchDisponible.setOnCheckedChangeListener((buttonView, isChecked) -> {


            if (isChecked) {
                txtEstado.setText("Estado: Disponible");
            } else {
                txtEstado.setText("Estado: Desconectado");
            }

        });

        btnActualizar.setOnClickListener(v -> {



            txtCliente.setText("Cliente: Juan Pérez");

            txtOrigen.setText("Origen: Centro");

            txtDestino.setText("Destino: Mercado Municipal");

        });

    }
}