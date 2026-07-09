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

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.cibergoliath.mytxis.location.LocationHelper;

public class ConductorActivity extends AppCompatActivity {

    TextView txtEstado;
    SwitchMaterial switchDisponible;

    TextView txtCliente;
    TextView txtOrigen;
    TextView txtDestino;

    Button btnActualizar;
    Button btnAceptar;
    Button btnRechazar;

    Button btnIniciarViaje;
    Button btnFinalizarViaje;

    TextView txtSolicitud;

    private LocationHelper locationHelper;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnableUbicacion;
    private Handler handlerViajes;
    private Runnable runnableViajes;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                        if (isGranted) {

                            Toast.makeText(
                                    this,
                                    "Permiso de ubicación concedido",
                                    Toast.LENGTH_SHORT
                            ).show();

                            obtenerUbicacionConductor();



                        } else {

                            Toast.makeText(
                                    this,
                                    "Se necesita el permiso de ubicación",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }
            );

    private int viajeId = 0;



    //inicia el segundo oncreate refactorizado//

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conductor);

        inicializarComponentes();
        configurarPermisos();
        configurarEstadoInicial();
        configurarSwitch();

        configurarBottomNavigation();
        configurarEventos();
    }
    private void inicializarComponentes() {

        locationHelper = new LocationHelper(this);

        txtEstado = findViewById(R.id.txtEstado);

        switchDisponible = findViewById(R.id.switchDisponible);

        txtCliente = findViewById(R.id.txtCliente);
        txtOrigen = findViewById(R.id.txtOrigen);
        txtDestino = findViewById(R.id.txtDestino);
        txtSolicitud = findViewById(R.id.txtSolicitud);

        btnActualizar = findViewById(R.id.btnActualizar);
        btnAceptar = findViewById(R.id.btnAceptar);
        btnRechazar = findViewById(R.id.btnRechazar);

        btnIniciarViaje = findViewById(R.id.btnIniciarViaje);
        btnFinalizarViaje = findViewById(R.id.btnFinalizarViaje);

    }
    private void configurarPermisos() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {



        } else {

            locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );

        }

    }
    private void configurarEstadoInicial() {

        boolean disponible = getSharedPreferences(
                "conductor",
                MODE_PRIVATE
        ).getBoolean("disponible", false);

        switchDisponible.setChecked(disponible);

        if (disponible) {

            txtEstado.setText("Estado: Disponible");

            btnActualizar.setEnabled(true);
            btnAceptar.setEnabled(true);
            btnRechazar.setEnabled(true);
            btnIniciarViaje.setEnabled(true);
            btnFinalizarViaje.setEnabled(true);

            iniciarActualizacionUbicacion();
            iniciarBusquedaViajes();

        } else {

            txtEstado.setText("Estado: Desconectado");

            btnActualizar.setEnabled(false);
            btnAceptar.setEnabled(false);
            btnRechazar.setEnabled(false);
            btnIniciarViaje.setEnabled(false);
            btnFinalizarViaje.setEnabled(false);

            detenerActualizacionUbicacion();
        }

    }
    private void configurarSwitch() {

        switchDisponible.setOnCheckedChangeListener((buttonView, isChecked) -> {

            getSharedPreferences("conductor", MODE_PRIVATE)
                    .edit()
                    .putBoolean("disponible", isChecked)
                    .apply();

            if (isChecked) {

                txtEstado.setText("Estado: Disponible");

                btnActualizar.setEnabled(true);
                btnAceptar.setEnabled(true);
                btnRechazar.setEnabled(true);
                btnIniciarViaje.setEnabled(true);
                btnFinalizarViaje.setEnabled(true);

                iniciarActualizacionUbicacion();
                iniciarBusquedaViajes();

            } else {

                txtEstado.setText("Estado: Desconectado");

                btnActualizar.setEnabled(false);
                btnAceptar.setEnabled(false);
                btnRechazar.setEnabled(false);
                btnIniciarViaje.setEnabled(false);
                btnFinalizarViaje.setEnabled(false);

                detenerActualizacionUbicacion();
                detenerBusquedaViajes();

            }

        });

    }


    private void configurarBottomNavigation() {

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

    }
    private void configurarEventos() {

        configurarBotonActualizar();

        configurarBotonAceptar();

        configurarBotonIniciarViaje();

        configurarBotonFinalizarViaje();

        configurarBotonRechazar();


    }
    private void configurarBotonActualizar() {

        btnActualizar.setOnClickListener(v -> {

            verificarViajesPendientes();

        });

    }

    private void verificarViajesPendientes() {

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

                    viajeId = viaje.getId();

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

                    //no hay viajespendientes

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

    }

    private void iniciarBusquedaViajes() {

        handlerViajes = new Handler(Looper.getMainLooper());

        runnableViajes = new Runnable() {

            @Override
            public void run() {

                verificarViajesPendientes();

                handlerViajes.postDelayed(this, 5000);

            }

        };

        handlerViajes.post(runnableViajes);

    }
    private void detenerBusquedaViajes() {

        if (handlerViajes != null && runnableViajes != null) {

            handlerViajes.removeCallbacks(runnableViajes);

        }

    }



    private void configurarBotonAceptar() {

        btnAceptar.setOnClickListener(v -> {

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);


            String conductorEmail =
                    getSharedPreferences("sesion", MODE_PRIVATE)
                            .getString("email", "");

            Call<String> call =
                    apiService.aceptarViaje(
                            viajeId,
                            conductorEmail
                    );

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {

                    if (response.isSuccessful()
                            && response.body() != null) {

                        String resultado = response.body().trim();

                        if (resultado.equals("success")) {

                            detenerBusquedaViajes();

                            txtSolicitud.setText("Viaje aceptado");

                            cargarViajeAceptado();

                            Toast.makeText(
                                    ConductorActivity.this,
                                    "Viaje aceptado correctamente",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else if (resultado.equals("ocupado")) {

                            Toast.makeText(
                                    ConductorActivity.this,
                                    "Este viaje ya fue aceptado por otro conductor",
                                    Toast.LENGTH_LONG
                            ).show();

                            txtSolicitud.setText(
                                    "Viaje tomado por otro conductor");
                        }

                    } else {

                        Toast.makeText(
                                ConductorActivity.this,
                                "Error al aceptar viaje",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call,
                                      Throwable t) {

                    Toast.makeText(
                            ConductorActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

        });

    }


    private void configurarBotonIniciarViaje() {
        btnIniciarViaje.setOnClickListener(v -> {

            if (viajeId == 0) {

                Toast.makeText(
                        ConductorActivity.this,
                        "Primero acepte un viaje",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Call<String> call = apiService.iniciarViaje(viajeId);

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().trim().equals("success")) {

                        txtSolicitud.setText("Viaje en camino");

                        Toast.makeText(
                                ConductorActivity.this,
                                "Viaje iniciado",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call,
                                      Throwable t) {

                    Toast.makeText(
                            ConductorActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

        });

    }



    private void configurarBotonFinalizarViaje() {
        btnFinalizarViaje.setOnClickListener(v -> {

            if (viajeId == 0) {

                Toast.makeText(
                        ConductorActivity.this,
                        "No hay viaje activo",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Call<String> call = apiService.finalizarViaje(viajeId);

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().trim().equals("success")) {

                        txtSolicitud.setText(
                                "No hay solicitudes pendientes");

                        txtCliente.setText(
                                "Cliente: Sin solicitudes");

                        txtOrigen.setText("Origen:");

                        txtDestino.setText("Destino:");

                        viajeId = 0;

                        Toast.makeText(
                                ConductorActivity.this,
                                "Viaje finalizado",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call,
                                      Throwable t) {

                    Toast.makeText(
                            ConductorActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

        });

    }





    private void configurarBotonRechazar() {

        btnRechazar.setOnClickListener(v -> {

            txtSolicitud.setText("Solicitud rechazada");

            txtCliente.setText("Cliente: Sin solicitudes");

            txtOrigen.setText("Origen:");

            txtDestino.setText("Destino:");

        });

    }







    private void obtenerUbicacionConductor() {

        locationHelper.obtenerUbicacionActual(
                new LocationHelper.OnLocationResult() {

                    @Override
                    public void onLocationReceived(Location location) {

                        enviarUbicacionAlServidor(
                                location.getLatitude(),
                                location.getLongitude()
                        );

                    }

                    @Override
                    public void onError(String mensaje) {

                        Toast.makeText(
                                ConductorActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();

                    }
                });

    }
    private void enviarUbicacionAlServidor(double latitud, double longitud) {

        String email = getSharedPreferences("sesion", MODE_PRIVATE)
                .getString("email", "");

        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        Call<String> call =
                apiService.actualizarUbicacionConductor(
                        email,
                        latitud,
                        longitud
                );

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call,
                                   Response<String> response) {

                if (response.isSuccessful()) {

                    // Ubicación enviada correctamente

                }

            }

            @Override
            public void onFailure(Call<String> call,
                                  Throwable t) {

                Toast.makeText(
                        ConductorActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }

    private void iniciarActualizacionUbicacion() {
        detenerActualizacionUbicacion();

        runnableUbicacion = new Runnable() {

            @Override
            public void run() {



                obtenerUbicacionConductor();

                handler.postDelayed(this, 5000);

            }

        };

        handler.post(runnableUbicacion);

    }

    private void detenerActualizacionUbicacion() {

        if (runnableUbicacion != null) {

            handler.removeCallbacks(runnableUbicacion);

        }

    }

    private void cargarViajeAceptado() {

        String conductorEmail = getSharedPreferences("sesion", MODE_PRIVATE)
                .getString("email", "");

        ApiService apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        Call<ViajeResponse> call =
                apiService.obtenerViajeAceptado(conductorEmail);

        call.enqueue(new Callback<ViajeResponse>() {

            @Override
            public void onResponse(Call<ViajeResponse> call,
                                   Response<ViajeResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    ViajeResponse viaje = response.body();

                    viajeId = viaje.getId();

                    txtSolicitud.setText("Viaje aceptado");

                    txtCliente.setText(
                            "Cliente: " +
                                    viaje.getUsuario_email());

                    txtOrigen.setText(
                            "Origen: " +
                                    viaje.getPunto_partida());

                    txtDestino.setText(
                            "Destino: " +
                                    viaje.getDestino());

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

    }
}