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
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cibergoliath.mytxis.location.LocationHelper;

import android.util.Log;
import android.graphics.Color;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import android.widget.LinearLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

public class ConductorActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    TextView txtEstado;
    SwitchMaterial switchDisponible;

    TextView txtCliente;
    TextView txtOrigen;
    TextView txtDestino;
    TextView txtReferencia;

    Button btnActualizar;
    Button btnAceptar;
    Button btnRechazar;

    Button btnIniciarViaje;
    Button btnFinalizarViaje;

    TextView txtSolicitud;
    private RecyclerView rvSolicitudes;
    private List<ViajeResponse> listaSolicitudes;
    private SolicitudAdapter solicitudAdapter;

    private static final String TAG = "MYTXIS";
    private GoogleMap mMap;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private Marker marcadorConductor;

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

                            iniciarActualizacionUbicacion();

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

    private boolean viajeAceptado = false;


    //inicia el segundo oncreate refactorizado//

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String email = getSharedPreferences("sesion", MODE_PRIVATE)
                .getString("email", "");

        if (email.isEmpty()) {

            Intent intent = new Intent(
                    ConductorActivity.this,
                    Login.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
            return;
        }

        Log.d(TAG, "onCreate()");

        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_conductor);
        //codigo para que se pueda deslizar bottomSheet

        LinearLayout bottomSheet =
                findViewById(R.id.bottomSheetConductor);

        bottomSheetBehavior =
                BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setPeekHeight(150);
        bottomSheetBehavior.setHideable(false);
                bottomSheetBehavior.setState(
                BottomSheetBehavior.STATE_COLLAPSED
        );

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapaConductor);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        inicializarComponentes();
        configurarPermisos();
        configurarEstadoInicial();
        configurarSwitch();

        configurarBottomNavigation();
        configurarEventos();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        LatLng mexico = new LatLng(19.9492195, -99.9978082);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        mexico,
                        10
                )
        );
    }

    private void inicializarComponentes() {

        locationHelper = new LocationHelper(this);

        txtEstado = findViewById(R.id.txtEstado);

        switchDisponible = findViewById(R.id.switchDisponible);

        txtCliente = findViewById(R.id.txtCliente);
        txtOrigen = findViewById(R.id.txtOrigen);
        txtDestino = findViewById(R.id.txtDestino);

        txtReferencia = findViewById(R.id.txtReferencia);

        txtSolicitud = findViewById(R.id.txtSolicitud);
        rvSolicitudes = findViewById(R.id.rvSolicitudes);
        rvSolicitudes.setLayoutManager(
                new LinearLayoutManager(this)
        );
        listaSolicitudes = new ArrayList<>();

        solicitudAdapter = new SolicitudAdapter(
                listaSolicitudes,
                viaje -> {
                    mostrarViaje(viaje);

                }
        );

        rvSolicitudes.setAdapter(solicitudAdapter);

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


        } else {

            txtEstado.setText("Estado: Desconectado");

            btnActualizar.setEnabled(false);
            btnAceptar.setEnabled(false);
            btnRechazar.setEnabled(false);
            btnIniciarViaje.setEnabled(false);
            btnFinalizarViaje.setEnabled(false);

            detenerActualizacionUbicacion();
        }
        actualizarBotonesSegunViaje("");

    }
    private void configurarSwitch() {

        switchDisponible.setOnCheckedChangeListener((buttonView, isChecked) -> {

            getSharedPreferences("conductor", MODE_PRIVATE)
                    .edit()
                    .putBoolean("disponible", isChecked)
                    .apply();

            if (isChecked) {

                txtEstado.setText("🟢 Disponible");
                txtEstado.setTextColor(Color.parseColor("#2E7D32"));

                btnActualizar.setEnabled(true);
                btnAceptar.setEnabled(true);
                btnRechazar.setEnabled(true);
                btnIniciarViaje.setEnabled(true);
                btnFinalizarViaje.setEnabled(true);

                iniciarActualizacionUbicacion();
                iniciarBusquedaViajes();

            } else {

                txtEstado.setText("🔴 Desconectado");
                txtEstado.setTextColor(Color.parseColor("#D32F2F"));

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
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");

        super.onStop();

        detenerActualizacionUbicacion();

        detenerBusquedaViajes();

    }
    @Override
    protected void onResume() {

        super.onResume();

        Log.d(TAG, "onResume()");

        boolean disponible = getSharedPreferences(
                "conductor",
                MODE_PRIVATE
        ).getBoolean("disponible", false);

        Log.d(TAG, "Disponible = " + disponible);

        if (disponible) {

            cargarViajeAceptado();          // Primero recuperar el viaje aceptado

            iniciarActualizacionUbicacion();

            iniciarBusquedaViajes();        // Después empezar a buscar pendientes

        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "===== ConductorActivity PAUSE =====");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "===== ConductorActivity DESTROY =====");
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

            Call<String> call = apiService.irHaciaCliente(viajeId);

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().trim().equals("success")) {

                        txtSolicitud.setText("Conductor en camino");

                        actualizarBotonesSegunViaje("hacia_cliente");

                        Toast.makeText(
                                ConductorActivity.this,
                                "Ahora vas hacia el cliente",
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

    private void verificarViajesPendientes() {

        Log.d(TAG, ">>> verificarViajesPendientes()");

        if (viajeAceptado) {

            Log.d(
                    TAG,
                    "Ya existe un viaje aceptado. No buscar pendientes."
            );

            return;
        }

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        String conductorEmail =
                getSharedPreferences("sesion", MODE_PRIVATE)
                        .getString("email", "");

        Call<List<ViajeResponse>> call =
                apiService.obtenerViajePendiente(
                        conductorEmail
                );

        call.enqueue(new Callback<List<ViajeResponse>>() {

            @Override
            public void onResponse(
                    Call<List<ViajeResponse>> call,
                    Response<List<ViajeResponse>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    List<ViajeResponse> viajes =
                            response.body();

                    Log.d(
                            TAG,
                            "[PENDIENTES] Total = "
                                    + viajes.size()
                    );

                    if (!viajes.isEmpty()) {

                        Log.d(
                                TAG,
                                "[PENDIENTES] Actualizando lista..."
                        );

                        listaSolicitudes.clear();

                        listaSolicitudes.addAll(viajes);

                        solicitudAdapter.notifyDataSetChanged();

                        // Mantener la solicitud seleccionada
                        ViajeResponse viajeSeleccionado = null;

                        for (ViajeResponse viaje : viajes) {

                            if (viaje.getId() == viajeId) {

                                viajeSeleccionado = viaje;
                                break;
                            }
                        }

// Si la solicitud seleccionada todavía existe,
// mantenerla en el detalle.
                        if (viajeSeleccionado != null) {

                            mostrarViaje(viajeSeleccionado);

                        } else {

                            // Si no hay una solicitud seleccionada,
                            // mostrar la primera.
                            if (viajeId == 0) {

                                mostrarViaje(viajes.get(0));

                            }
                        }

                    } else {

                        listaSolicitudes.clear();

                        solicitudAdapter.notifyDataSetChanged();

                        limpiarSolicitudPendiente();
                    }

                } else {

                    limpiarSolicitudPendiente();
                }
            }

            @Override
            public void onFailure(
                    Call<List<ViajeResponse>> call,
                    Throwable t) {

                Toast.makeText(
                        ConductorActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void mostrarViaje(ViajeResponse viaje) {

        Log.d(TAG, "===== mostrarViaje =====");
        Log.d(TAG, "Estado recibido = " + viaje.getEstado());

        viajeId = viaje.getId();

        txtCliente.setText("Cliente: " + viaje.getNombre());
        txtOrigen.setText("Origen: " + viaje.getPunto_partida());
        txtReferencia.setText("Referencia: " + viaje.getReferencia());
        txtDestino.setText("Destino: " + viaje.getDestino());

        txtSolicitud.setText("Estado: " + viaje.getEstado());

        actualizarBotonesSegunViaje(viaje.getEstado());
    }

    private void limpiarSolicitudPendiente() {

        viajeId = 0;

        txtCliente.setText("Cliente:");
        txtOrigen.setText("Origen:");
        txtReferencia.setText("Referencia:");
        txtDestino.setText("Destino:");
        txtSolicitud.setText("");

        actualizarBotonesSegunViaje("");

    }

    private void actualizarBotonesSegunViaje(String estado) {
        Log.d(TAG, "actualizarBotonesSegunViaje -> " + estado);

        // Ocultamos todos
        btnActualizar.setVisibility(View.GONE);
        btnAceptar.setVisibility(View.GONE);
        btnRechazar.setVisibility(View.GONE);
        btnIniciarViaje.setVisibility(View.GONE);
        btnFinalizarViaje.setVisibility(View.GONE);

        switch (estado.toLowerCase()) {

            case "pendiente":

                btnAceptar.setVisibility(View.VISIBLE);
                btnRechazar.setVisibility(View.VISIBLE);
                break;

            case "aceptado":

                btnActualizar.setVisibility(View.VISIBLE); // Después lo renombraremos a "Ir hacia el cliente"
                break;

            case "hacia_cliente":

                btnIniciarViaje.setVisibility(View.VISIBLE);
                break;

            case "en_camino":

                btnFinalizarViaje.setVisibility(View.VISIBLE);
                break;

            default:
                // No mostrar ningún botón
                break;
        }
    }

    private void limpiarPantallaViaje() {

        txtSolicitud.setText("No hay solicitudes pendientes");
        txtCliente.setText("Cliente: Sin solicitudes");
        txtOrigen.setText("Origen:");
        txtReferencia.setText("Referencia:");
        txtDestino.setText("Destino:");
        viajeId = 0;
        viajeAceptado = false;

    }

    private void iniciarBusquedaViajes() {

        Log.d(TAG, "iniciarBusquedaViajes()");

        handlerViajes = new Handler(Looper.getMainLooper());

        runnableViajes = new Runnable() {

            @Override
            public void run() {

                Log.d(TAG, "Runnable ejecutándose");

                verificarViajesPendientes();

                handlerViajes.postDelayed(this, 5000);

            }

        };

        handlerViajes.postDelayed(runnableViajes, 3000);

    }
    private void detenerBusquedaViajes() {

        Log.d(TAG, ">>> detenerBusquedaViajes()");

        if (handlerViajes != null) {

            handlerViajes.removeCallbacksAndMessages(null);

            Log.d(TAG, "Todos los callbacks eliminados");
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

                            viajeAceptado = true;

                            detenerBusquedaViajes();

                            /// El conductor ya está ocupado con este viaje
                            // Limpiar todas las solicitudes pendientes de su pantalla
                            listaSolicitudes.clear();

                            solicitudAdapter.notifyDataSetChanged();

                            solicitudAdapter.notifyDataSetChanged();

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

                        actualizarBotonesSegunViaje("en_camino");

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

                        limpiarPantallaViaje();

                        actualizarBotonesSegunViaje("");

                        if (switchDisponible.isChecked()) {

                            detenerBusquedaViajes();
                            iniciarBusquedaViajes();

                        }

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

            ApiService apiService =
                    RetrofitClient
                            .getClient()
                            .create(ApiService.class);

            String conductorEmail =
                    getSharedPreferences("sesion", MODE_PRIVATE)
                            .getString("email", "");

            Call<String> call =
                    apiService.rechazarViaje(
                            viajeId,
                            conductorEmail
                    );

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(
                        Call<String> call,
                        Response<String> response) {

                    if (response.isSuccessful()
                            && response.body() != null) {

                        String resultado =
                                response.body().trim();

                        if (resultado.equals("success")) {

                            // Eliminar la solicitud rechazada
                            for (int i = 0;
                                 i < listaSolicitudes.size();
                                 i++) {

                                if (listaSolicitudes
                                        .get(i)
                                        .getId() == viajeId) {

                                    listaSolicitudes.remove(i);
                                    break;
                                }
                            }

                            solicitudAdapter
                                    .notifyDataSetChanged();

                            limpiarPantallaViaje();

                            Toast.makeText(
                                    ConductorActivity.this,
                                    "Viaje rechazado",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else if (resultado.equals("ya_rechazado")) {

                            Toast.makeText(
                                    ConductorActivity.this,
                                    "Este viaje ya fue rechazado",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else if (resultado.equals("no_autorizado")) {

                            Toast.makeText(
                                    ConductorActivity.this,
                                    "Conductor no autorizado",
                                    Toast.LENGTH_LONG
                            ).show();

                        } else if (resultado.equals("viaje_no_disponible")) {

                            Toast.makeText(
                                    ConductorActivity.this,
                                    "El viaje ya no está disponible",
                                    Toast.LENGTH_LONG
                            ).show();

                        } else {

                            Toast.makeText(
                                    ConductorActivity.this,
                                    "Error al rechazar viaje",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } else {

                        Toast.makeText(
                                ConductorActivity.this,
                                "Error del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(
                        Call<String> call,
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

        locationHelper.iniciarActualizacionesUbicacion(
                new LocationHelper.OnLocationResult() {

                    @Override
                    public void onLocationReceived(Location location) {

                        double latitud = location.getLatitude();
                        double longitud = location.getLongitude();

                        float precision = location.getAccuracy();

                        Log.d(
                                TAG,
                                "GPS -> Lat: " + latitud
                                        + " Lon: " + longitud
                                        + " Precisión: "
                                        + precision
                                        + " metros"
                        );

                        enviarUbicacionAlServidor(
                                latitud,
                                longitud
                        );

                        if (mMap != null) {

                            LatLng ubicacionConductor =
                                    new LatLng(latitud, longitud);

                            if (marcadorConductor == null) {

                                marcadorConductor = mMap.addMarker(
                                        new MarkerOptions()
                                                .position(ubicacionConductor)
                                                .title("Mi ubicación")
                                );

                                mMap.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                ubicacionConductor,
                                                16
                                        )
                                );

                            } else {

                                marcadorConductor.setPosition(
                                        ubicacionConductor
                                );
                            }
                        }
                    }

                    @Override
                    public void onError(String mensaje) {

                        Toast.makeText(
                                ConductorActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();

                    }
                }
        );
    }

    private void detenerActualizacionUbicacion() {

        locationHelper.detenerActualizacionesUbicacion();

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

                    Log.d(TAG, "[ACEPTADO] Nombre = '" + viaje.getNombre() + "'");
                    Log.d(TAG, "[ACEPTADO] ID = " + viaje.getId());
                    Log.d(TAG, "[ACEPTADO] Estado = '" + viaje.getEstado() + "'");

                    String estado = viaje.getEstado();

                    if ("aceptado".equalsIgnoreCase(estado)) {

                        txtSolicitud.setText("Viaje aceptado");

                    } else if ("en_camino".equalsIgnoreCase(estado)) {

                        txtSolicitud.setText("Viaje en camino");

                    } else {

                        txtSolicitud.setText("Estado: " + estado);

                    }

                    mostrarViaje(viaje);
                    actualizarBotonesSegunViaje(viaje.getEstado());

                }
                else {

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