package com.cibergoliath.mytxis;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        boolean disponible =
                getSharedPreferences("conductor", MODE_PRIVATE)
                        .getBoolean("disponible", false);

        switchDisponible.setChecked(disponible);

        txtCliente = findViewById(R.id.txtCliente);
        txtOrigen = findViewById(R.id.txtOrigen);
        txtDestino = findViewById(R.id.txtDestino);
        txtSolicitud = findViewById(R.id.txtSolicitud);


        btnActualizar = findViewById(R.id.btnActualizar);
        btnAceptar = findViewById(R.id.btnAceptar);
        btnRechazar = findViewById(R.id.btnRechazar);

        btnActualizar.setEnabled(false);
        btnAceptar.setEnabled(false);
        btnRechazar.setEnabled(false);

        if (disponible) {

            txtEstado.setText("Estado: Disponible");

            btnActualizar.setEnabled(true);
            btnAceptar.setEnabled(true);
            btnRechazar.setEnabled(true);

        } else {

            txtEstado.setText("Estado: Desconectado");
        }

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

                getSharedPreferences("conductor", MODE_PRIVATE)
                        .edit()
                        .putBoolean("disponible", true)
                        .apply();
                txtEstado.setText("Estado: Disponible");

                btnActualizar.setEnabled(true);
                btnAceptar.setEnabled(true);
                btnRechazar.setEnabled(true);

            } else {
                getSharedPreferences("conductor", MODE_PRIVATE)
                        .edit()
                        .putBoolean("disponible", false)
                        .apply();

                txtEstado.setText("Estado: Desconectado");

                btnActualizar.setEnabled(false);
                btnAceptar.setEnabled(false);
                btnRechazar.setEnabled(false);

                txtSolicitud.setText("No hay solicitudes pendientes");
                txtCliente.setText("Cliente: Sin solicitudes");
                txtOrigen.setText("Origen:");
                txtDestino.setText("Destino:");
            }

        });

        btnActualizar.setOnClickListener(v -> {

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Call<ViajeResponse> call =
                    apiService.obtenerViajePendiente();

            call.enqueue(new Callback<ViajeResponse>() {

                @Override
                public void onResponse(Call<ViajeResponse> call,
                                       Response<ViajeResponse> response) {

                    if (response.isSuccessful()
                            && response.body() != null) {

                        ViajeResponse viaje = response.body();

                        txtCliente.setText(
                                "Cliente: " +
                                        viaje.getUsuario_email());

                        txtOrigen.setText(
                                "Origen: " +
                                        viaje.getPunto_partida());

                        txtDestino.setText(
                                "Destino: " +
                                        viaje.getDestino());

                    } else {

                        Toast.makeText(
                                ConductorActivity.this,
                                "No hay viajes pendientes",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<ViajeResponse> call,
                                      Throwable t) {

                    Toast.makeText(
                            ConductorActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

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