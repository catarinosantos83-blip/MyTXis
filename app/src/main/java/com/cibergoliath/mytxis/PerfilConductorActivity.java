package com.cibergoliath.mytxis;

import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilConductorActivity extends AppCompatActivity {

    TextView txtPlacaPerfil;
    TextView txtMarcaPerfil;
    TextView txtModeloPerfil;
    TextView txtColorPerfil;

    Button btnCerrarSesionConductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_conductor);

        BottomNavigationView bottomNavigation;

        bottomNavigation = findViewById(R.id.bottomNavigationConductor);

        bottomNavigation.setSelectedItemId(R.id.nav_perfil_conductor);

        bottomNavigation.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_inicio_conductor) {

                startActivity(new Intent(
                        PerfilConductorActivity.this,
                        ConductorActivity.class));

                return true;

            } else if (item.getItemId() == R.id.nav_perfil_conductor) {

                return true;
            }

            return false;
        });

        txtPlacaPerfil = findViewById(R.id.txtPlacaPerfil);
        txtMarcaPerfil = findViewById(R.id.txtMarcaPerfil);
        txtModeloPerfil = findViewById(R.id.txtModeloPerfil);
        txtColorPerfil = findViewById(R.id.txtColorPerfil);

        btnCerrarSesionConductor =
                findViewById(R.id.btnCerrarSesionConductor);

        SharedPreferences preferencias =
                getSharedPreferences("conductor", MODE_PRIVATE);

        String placa =
                preferencias.getString("placa", "");

        String marca =
                preferencias.getString("marca", "");

        String modelo =
                preferencias.getString("modelo", "");

        String color =
                preferencias.getString("color", "");

        txtPlacaPerfil.setText("Placa: " + placa);
        txtMarcaPerfil.setText("Marca: " + marca);
        txtModeloPerfil.setText("Modelo: " + modelo);
        txtColorPerfil.setText("Color: " + color);

        btnCerrarSesionConductor.setOnClickListener(v -> {

            getSharedPreferences("sesion", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(
                    PerfilConductorActivity.this,
                    Login.class
            );

            startActivity(intent);
            finish();

        });


    }
}