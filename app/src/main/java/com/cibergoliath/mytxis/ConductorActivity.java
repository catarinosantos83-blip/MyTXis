package com.cibergoliath.mytxis;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.material.switchmaterial.SwitchMaterial;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    Button btnAceptar;
    Button btnRechazar;

    TextView txtSolicitud;

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
        txtSolicitud = findViewById(R.id.txtSolicitud);


        btnActualizar = findViewById(R.id.btnActualizar);
        btnAceptar = findViewById(R.id.btnAceptar);
        btnRechazar = findViewById(R.id.btnRechazar);

        BottomNavigationView bottomNavigation;

        bottomNavigation = findViewById(R.id.bottomNavigationConductor);

        bottomNavigation.setSelectedItemId(R.id.nav_inicio_conductor);

        bottomNavigation.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_inicio_conductor) {

                return true;

            } else if (item.getItemId() == R.id.nav_perfil_conductor) {

                startActivity(new Intent(
                        ConductorActivity.this,
                        PerfilConductorActivity.class));

                return true;
            }

            return false;
        });




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

        btnAceptar.setOnClickListener(v -> {

            txtCliente.setText("Cliente: En camino...");

            txtOrigen.setText("Origen:");

            txtDestino.setText("Destino:");

            txtSolicitud.setText("Viaje aceptado");

        });

        btnRechazar.setOnClickListener(v -> {

            txtSolicitud.setText("Solicitud rechazada");

            txtCliente.setText("Cliente: Sin solicitudes");

            txtOrigen.setText("Origen:");

            txtDestino.setText("Destino:");

        });

    }
}