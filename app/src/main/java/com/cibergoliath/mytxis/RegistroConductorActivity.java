package com.cibergoliath.mytxis;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.widget.Button;

public class RegistroConductorActivity extends AppCompatActivity {

    Button btnIngresar, btnVolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_conductor);

        btnIngresar = findViewById(R.id.btnIngresar);
        btnVolver = findViewById(R.id.btnVolver);

        btnIngresar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegistroConductorActivity.this,
                    ConductorActivity.class
            );

            startActivity(intent);

        });

        btnVolver.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegistroConductorActivity.this,
                    UsuarioActivity.class
            );

            startActivity(intent);
            finish();

        });


    }
}