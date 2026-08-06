package com.cibergoliath.mytxis;

import android.os.Bundle;
import android.widget.EditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.TextView;

import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;

import com.cibergoliath.mytxis.utils.GeocoderHelper;

import com.cibergoliath.mytxis.models.DirectionsResponse;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;
import android.graphics.Color;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.LatLngBounds;
import com.cibergoliath.mytxis.models.Route;
import com.cibergoliath.mytxis.models.Leg;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    EditText txtOrigen, txtDestino;
    EditText edtReferencia;
    MaterialButton btnSolicitarViaje;
    TextView txtConductor;
    TextView txtVehiculo;
    TextView txtPlaca;
    TextView txtColor;
    TextView txtDistancia;
    TextView txtTiempo;
    TextView txtEstadoViaje;

    GoogleMap mMap;
    BottomNavigationView bottomNavigation;
    private FusedLocationProviderClient fusedLocationClient;

    private Polyline rutaActual;

    private String tipoSeleccion = "";

    private double origenLat;
    private double origenLng;

    private double destinoLat;
    private double destinoLng;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private Runnable actualizarEstadoRunnable;

    private final ActivityResultLauncher<Intent> mapaLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {

                            double latitud =
                                    result.getData().getDoubleExtra("latitud", 0);

                            double longitud =
                                    result.getData().getDoubleExtra("longitud", 0);

                            String direccion =
                                    GeocoderHelper.obtenerDireccion(
                                            MainActivity.this,
                                            latitud,
                                            longitud
                                    );

                            if (tipoSeleccion.equals("ORIGEN")) {

                                origenLat = latitud;
                                origenLng = longitud;

                                txtOrigen.setText(direccion);

                            } else if (tipoSeleccion.equals("DESTINO")) {

                                destinoLat = latitud;
                                destinoLng = longitud;

                                txtDestino.setText(direccion);

                            }
                            if (!txtOrigen.getText().toString().isEmpty()
                                    && !txtDestino.getText().toString().isEmpty()) {

                                solicitarRuta();

                            }

                        }

                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MAIN", "onCreate ejecutado");

        String emailSesion =
                getSharedPreferences("sesion", MODE_PRIVATE)
                        .getString("email", "");

        if (emailSesion.isEmpty()) {

            Intent intent = new Intent(
                    MainActivity.this,
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


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        txtOrigen = findViewById(R.id.txtOrigen);
        txtDestino = findViewById(R.id.txtDestino);

        txtOrigen.setFocusable(false);
        txtOrigen.setClickable(true);

        txtDestino.setFocusable(false);
        txtDestino.setClickable(true);


        txtOrigen.setOnClickListener(v -> {

            String[] opciones = {
                    "📍 Usar mi ubicación actual",
                    "📝 Escribir dirección",
                    "🗺️ Seleccionar en el mapa"
            };

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Seleccionar origen")
                    .setItems(opciones, (dialog, which) -> {

                        switch (which) {

                            case 0:

                                obtenerUbicacionActual();

                                break;

                            case 1:
                                // Escribir dirección
                                break;

                            case 2:

                                tipoSeleccion = "ORIGEN";

                                Intent intent = new Intent(
                                        MainActivity.this,
                                        MapaActivity.class
                                );

                                intent.putExtra("tipo", "ORIGEN");

                                mapaLauncher.launch(intent);

                                break;
                        }

                    })
                    .show();

        });


        txtDestino.setOnClickListener(v -> {

            tipoSeleccion = "DESTINO";

            Intent intent = new Intent(
                    MainActivity.this,
                    MapaActivity.class
            );

            intent.putExtra("tipo", "DESTINO");

            mapaLauncher.launch(intent);

        });

        edtReferencia = findViewById(R.id.edtReferencia);
        btnSolicitarViaje = findViewById(R.id.btnSolicitarViaje);

        txtConductor = findViewById(R.id.txtConductor);
        txtVehiculo = findViewById(R.id.txtVehiculo);
        txtPlaca = findViewById(R.id.txtPlaca);
        txtColor = findViewById(R.id.txtColor);
        txtEstadoViaje = findViewById(R.id.txtEstadoViaje);

        txtDistancia = findViewById(R.id.txtDistancia);
        txtTiempo = findViewById(R.id.txtTiempo);

        bottomNavigation = findViewById(R.id.bottomNavigation);




        btnSolicitarViaje.setOnClickListener(v -> {

            String referencia = edtReferencia.getText().toString().trim();

            String origen = txtOrigen.getText().toString().trim();
            String destino = txtDestino.getText().toString().trim();

            if (origen.isEmpty()) {

                Toast.makeText(
                        MainActivity.this,
                        "Seleccione un origen",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (destino.isEmpty()) {

                Toast.makeText(
                        MainActivity.this,
                        "Seleccione un destino",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            String email = getSharedPreferences(
                    "sesion",
                    MODE_PRIVATE
            ).getString("email", "");

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Call<String> call = apiService.solicitarViaje(
                    email,
                    origen,
                    destino,
                    referencia
            );

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().trim().equals("success")) {

                        bloquearPantallaViaje();

                        iniciarConsultaEstadoViaje();

                        Toast.makeText(
                                MainActivity.this,
                                "Viaje solicitado correctamente.\nBuscando conductor...",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        Toast.makeText(
                                MainActivity.this,
                                "Error al solicitar el viaje",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call,
                                      Throwable t) {

                    Toast.makeText(
                            MainActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

        });

        bottomNavigation.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {

                return true;

            } else if (item.getItemId() == R.id.nav_actividades) {

                Intent intent = new Intent(
                        MainActivity.this,
                        ActividadesActivity.class
                );

                startActivity(intent);

                return true;

            } else if (item.getItemId() == R.id.nav_usuario) {

                Intent intent = new Intent(
                        MainActivity.this,
                        UsuarioActivity.class
                );

                startActivity(intent);

                return true;
            }

            return false;
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        actualizarEstadoRunnable = new Runnable() {
            @Override
            public void run() {

                consultarEstadoViaje();

                handler.postDelayed(this, 5000);

            }
        };
        // handler.post(actualizarEstadoRunnable);

    }

    private void consultarConductorAsignado() {


        String emailUsuario =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE)
                        .getString("email", "");

        ApiService apiServiceConductor = RetrofitClient
                .getClient()
                .create(ApiService.class);

        Call<ConductorInfoResponse> callConductor =
                apiServiceConductor.obtenerConductorCliente(emailUsuario);


        callConductor.enqueue(new Callback<ConductorInfoResponse>() {

            @Override
            public void onResponse(
                    Call<ConductorInfoResponse> call,
                    Response<ConductorInfoResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    ConductorInfoResponse conductor =
                            response.body();

                    if (conductor.getNombre() == null
                            || conductor.getNombre().isEmpty()) {

                        txtConductor.setText("Conductor:");
                        txtVehiculo.setText("Vehículo:");
                        txtPlaca.setText("Placa:");
                        txtColor.setText("Color:");

                    } else {

                        txtConductor.setText(
                                "Conductor: " +
                                        conductor.getNombre());

                        txtVehiculo.setText(
                                "Vehículo: " +
                                        conductor.getMarca() +
                                        " " +
                                        conductor.getModelo());

                        txtPlaca.setText(
                                "Placa: " +
                                        conductor.getPlaca());

                        txtColor.setText(
                                "Color: " +
                                        conductor.getColor());
                    }
                }
            }

            @Override
            public void onFailure(
                    Call<ConductorInfoResponse> call,
                    Throwable t) {

            }
        });
    }

    private void obtenerUbicacionActual() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {

                    if (location != null) {

                        origenLat = location.getLatitude();
                        origenLng = location.getLongitude();

                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                        try {

                            List<Address> direcciones =
                                    geocoder.getFromLocation(
                                            origenLat,
                                            origenLng,
                                            1
                                    );

                            if (direcciones != null && !direcciones.isEmpty()) {

                                Address direccion = direcciones.get(0);

                                txtOrigen.setText(
                                        direccion.getAddressLine(0)
                                );

                            }

                        } catch (IOException e) {

                            e.printStackTrace();

                        }

                    } else {

                        Toast.makeText(
                                this,
                                "No fue posible obtener la ubicación",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }

    private void consultarEstadoViaje() {

        String email = getSharedPreferences(
                "sesion",
                MODE_PRIVATE
        ).getString("email", "");

        ApiService apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        Call<UltimoViajeResponse> call =
                apiService.obtenerUltimoViaje(email);

        call.enqueue(new Callback<UltimoViajeResponse>() {

            @Override
            public void onResponse(
                    Call<UltimoViajeResponse> call,
                    Response<UltimoViajeResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    UltimoViajeResponse viaje = response.body();
                    txtOrigen.setText(viaje.getPunto_partida());

                    txtDestino.setText(viaje.getDestino());

                    edtReferencia.setText(viaje.getReferencia());


                    String estado = viaje.getEstado();

                    switch (estado) {

                        case "pendiente":
                            txtEstadoViaje.setText("🟡 Buscando conductor...");
                            break;

                        case "aceptado":
                            txtEstadoViaje.setText("🟢 Conductor asignado");
                            consultarConductorAsignado();
                            break;

                        case "hacia_cliente":

                            txtEstadoViaje.setText("🚖 Conductor va hacia ti");

                            consultarConductorAsignado();

                            break;



                        case "en_camino":
                            txtEstadoViaje.setText("🚖 Viaje en curso");
                            consultarConductorAsignado();
                            break;

                        case "finalizado":

                            txtEstadoViaje.setText("✅ Viaje finalizado");

                            detenerConsultaEstadoViaje();
                            limpiarPantallaViaje();
                            desbloquearPantallaViaje();

                            break;

                        default:
                            txtEstadoViaje.setText("Sin viaje");
                            break;
                    }

                }

            }

            @Override
            public void onFailure(
                    Call<UltimoViajeResponse> call,
                    Throwable t) {

            }

        });

    }

    private void solicitarRuta() {

        String origen = origenLat + "," + origenLng;
        String destino = destinoLat + "," + destinoLng;

        ApiService apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        Call<DirectionsResponse> call =
                apiService.obtenerRuta(origen, destino);

        call.enqueue(new Callback<DirectionsResponse>() {

            @Override
            public void onResponse(
                    Call<DirectionsResponse> call,
                    Response<DirectionsResponse> response) {

                Toast.makeText(
                        MainActivity.this,
                        "Código: " + response.code(),
                        Toast.LENGTH_LONG
                ).show();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getRoutes() != null
                        && !response.body().getRoutes().isEmpty()) {

                    Route route = response.body()
                            .getRoutes()
                            .get(0);

                    Leg leg = route
                            .getLegs()
                            .get(0);

                    String distancia = leg
                            .getDistance()
                            .getText();

                    String tiempo = leg
                            .getDuration()
                            .getText();

                    txtDistancia.setText("📏 Distancia: " + distancia);

                    txtTiempo.setText("⏱ Tiempo: " + tiempo);

                    String polyline =
                            response.body()
                                    .getRoutes()
                                    .get(0)
                                    .getOverviewPolyline()
                                    .getPoints();

                    List<LatLng> puntos =
                            PolyUtil.decode(polyline);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (LatLng punto : puntos) {
                        builder.include(punto);
                    }

                    LatLngBounds bounds = builder.build();

                    if (rutaActual != null) {
                        rutaActual.remove();
                    }

                    rutaActual = mMap.addPolyline(
                            new PolylineOptions()
                                    .addAll(puntos)
                                    .width(12)
                                    .color(Color.BLUE)
                    );
                    mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    150
                            )
                    );



                } else {

                    Toast.makeText(
                            MainActivity.this,
                            "Error del servidor",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

            @Override
            public void onFailure(
                    Call<DirectionsResponse> call,
                    Throwable t) {

                Toast.makeText(
                        MainActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;



        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        LatLng mexico = new LatLng(19.9492195,-99.9978082);
        mMap.addMarker(new MarkerOptions().position(mexico).title("México"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico));

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtOrigen.setText(""+latLng.latitude);
        txtDestino.setText(""+latLng.longitude);


    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtOrigen.setText(""+latLng.latitude);
        txtDestino.setText(""+latLng.longitude);
    }

    private void bloquearPantallaViaje() {

        txtOrigen.setEnabled(false);

        txtDestino.setEnabled(false);

        edtReferencia.setEnabled(false);

        btnSolicitarViaje.setEnabled(false);

    }

    private void desbloquearPantallaViaje() {

        txtOrigen.setEnabled(true);

        txtDestino.setEnabled(true);

        edtReferencia.setEnabled(true);

        btnSolicitarViaje.setEnabled(true);

    }

    // Con este metodo decidiremos cuando iniciar consulta

    private void iniciarConsultaEstadoViaje() {

        if (actualizarEstadoRunnable != null) {

            handler.post(actualizarEstadoRunnable);

        }

    }

    private void detenerConsultaEstadoViaje() {

        if (actualizarEstadoRunnable != null) {
            handler.removeCallbacks(actualizarEstadoRunnable);
        }

    }

    //*este metodo limpia toda la pantalla de cliente cuando el conductor finaliza*//

    private void limpiarPantallaViaje() {

        txtOrigen.setText("Seleccionar origen");

        txtDestino.setText("Seleccionar destino");

        edtReferencia.setText("");

        txtDistancia.setText("");

        txtTiempo.setText("");

        txtConductor.setText("Conductor:");

        txtVehiculo.setText("Vehículo:");

        txtPlaca.setText("Placa:");

        txtColor.setText("Color:");

        txtEstadoViaje.setText("");

        origenLat = 0;
        origenLng = 0;

        destinoLat = 0;
        destinoLng = 0;

        if (rutaActual != null) {

            rutaActual.remove();

            rutaActual = null;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(actualizarEstadoRunnable);
    }

}