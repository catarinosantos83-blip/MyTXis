package com.cibergoliath.mytxis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class UsuarioActivity extends AppCompatActivity {

    Button btnCerrarSesion;
    Button btnConductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuario);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnConductor = findViewById(R.id.btnConductor);

        BottomNavigationView bottomNavigation;

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.nav_usuario);

        bottomNavigation.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {

                startActivity(new Intent(
                        UsuarioActivity.this,
                        MainActivity.class));

                return true;

            } else if (item.getItemId() == R.id.nav_actividades) {

                startActivity(new Intent(
                        UsuarioActivity.this,
                        ActividadesActivity.class));

                return true;

            } else if (item.getItemId() == R.id.nav_usuario) {

                return true;
            }

            return false;
        });

        btnConductor.setOnClickListener(v -> {

            Intent intent = new Intent(
                    UsuarioActivity.this,
                    RegistroConductorActivity.class
            );

            startActivity(intent);

        });

        btnCerrarSesion.setOnClickListener(v -> {

            getSharedPreferences("sesion", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(
                    UsuarioActivity.this,
                    Login.class
            );

            startActivity(intent);
            finish();

        });

    }
}