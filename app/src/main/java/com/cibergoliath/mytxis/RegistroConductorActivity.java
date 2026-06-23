package com.cibergoliath.mytxis;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import android.widget.Button;

public class RegistroConductorActivity extends AppCompatActivity {

    Button btnIngresar, btnVolver;

    Button btnDatos;

    EditText txtPlaca;
    EditText txtMarca;
    EditText txtModelo;
    EditText txtColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_conductor);

        btnIngresar = findViewById(R.id.btnIngresar);
        btnVolver = findViewById(R.id.btnVolver);

        btnDatos = findViewById(R.id.btnDatos);

        txtPlaca = findViewById(R.id.txtPlaca);
        txtMarca = findViewById(R.id.txtMarca);
        txtModelo = findViewById(R.id.txtModelo);
        txtColor = findViewById(R.id.txtColor);

        SharedPreferences preferencias =
                getSharedPreferences("conductor", MODE_PRIVATE);

        txtPlaca.setText(
                preferencias.getString("placa", ""));

        txtMarca.setText(
                preferencias.getString("marca", ""));

        txtModelo.setText(
                preferencias.getString("modelo", ""));

        txtColor.setText(
                preferencias.getString("color", ""));

        btnDatos.setOnClickListener(v -> {



            SharedPreferences.Editor editor =
                    preferencias.edit();

            editor.putString("placa",
                    txtPlaca.getText().toString());

            editor.putString("marca",
                    txtMarca.getText().toString());

            editor.putString("modelo",
                    txtModelo.getText().toString());

            editor.putString("color",
                    txtColor.getText().toString());

            editor.apply();


            String email = getSharedPreferences(
                    "sesion",
                    MODE_PRIVATE
            ).getString("email", "");

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Call<String> call = apiService.guardarDatosConductor(
                    email,
                    txtPlaca.getText().toString(),
                    txtMarca.getText().toString(),
                    txtModelo.getText().toString(),
                    txtColor.getText().toString()
            );

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().trim().equals("success")) {

                        Toast.makeText(
                                RegistroConductorActivity.this,
                                "Datos guardados en MySQL",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call,
                                      Throwable t) {

                    Toast.makeText(
                            RegistroConductorActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

            Toast.makeText(
                    this,
                    "Datos guardados correctamente",
                    Toast.LENGTH_SHORT
            ).show();

        });

        btnIngresar.setOnClickListener(v -> {

            String email = getSharedPreferences(
                    "sesion",
                    MODE_PRIVATE
            ).getString("email", "");

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Call<String> call = apiService.actualizarTipoUsuario(email);

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().trim().equals("ok")) {

                        Toast.makeText(
                                RegistroConductorActivity.this,
                                "Ahora eres conductor",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                RegistroConductorActivity.this,
                                ConductorActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                RegistroConductorActivity.this,
                                "Error al actualizar usuario",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call,
                                      Throwable t) {

                    Toast.makeText(
                            RegistroConductorActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

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