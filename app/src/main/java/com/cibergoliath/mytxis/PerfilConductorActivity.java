package com.cibergoliath.mytxis;

import android.os.Bundle;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.cibergoliath.mytxis.models.PerfilConductorResponse;

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

                finish();

                return true;
            }else if (item.getItemId() == R.id.nav_perfil_conductor) {

                return true;
            }

            return false;
        });

        txtPlacaPerfil = findViewById(R.id.txtPlacaPerfil);
        txtMarcaPerfil = findViewById(R.id.txtMarcaPerfil);
        txtModeloPerfil = findViewById(R.id.txtModeloPerfil);
        txtColorPerfil = findViewById(R.id.txtColorPerfil);
        btnCerrarSesionConductor = findViewById(R.id.btnCerrarSesionConductor);

        cargarPerfilConductor();
        /*

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

         */



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
    private void cargarPerfilConductor() {

        String email = getSharedPreferences("sesion", MODE_PRIVATE)
                .getString("email", "");

        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        Call<PerfilConductorResponse> call =
                apiService.obtenerPerfilConductor(email);

        call.enqueue(new Callback<PerfilConductorResponse>() {

            @Override
            public void onResponse(Call<PerfilConductorResponse> call,
                                   Response<PerfilConductorResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    PerfilConductorResponse perfil = response.body();

                    txtPlacaPerfil.setText(
                            "Placa: " + perfil.getPlaca());

                    txtMarcaPerfil.setText(
                            "Marca: " + perfil.getMarca());

                    txtModeloPerfil.setText(
                            "Modelo: " + perfil.getModelo());

                    txtColorPerfil.setText(
                            "Color: " + perfil.getColor());

                } else {

                    Toast.makeText(
                            PerfilConductorActivity.this,
                            "No se pudo cargar el perfil",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

            @Override
            public void onFailure(Call<PerfilConductorResponse> call,
                                  Throwable t) {

                Toast.makeText(
                        PerfilConductorActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }


}