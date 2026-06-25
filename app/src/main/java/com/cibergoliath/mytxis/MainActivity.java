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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    EditText txtOrigen, txtDestino;
    EditText edtReferencia;
    MaterialButton btnSolicitarViaje;
    TextView txtConductor;
    TextView txtVehiculo;
    TextView txtPlaca;
    TextView txtColor;

    GoogleMap mMap;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        txtOrigen = findViewById(R.id.txtOrigen);
        txtDestino = findViewById(R.id.txtDestino);

        edtReferencia = findViewById(R.id.edtReferencia);
        btnSolicitarViaje = findViewById(R.id.btnSolicitarViaje);

        txtConductor = findViewById(R.id.txtConductor);
        txtVehiculo = findViewById(R.id.txtVehiculo);
        txtPlaca = findViewById(R.id.txtPlaca);
        txtColor = findViewById(R.id.txtColor);

        bottomNavigation = findViewById(R.id.bottomNavigation);


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

        btnSolicitarViaje.setOnClickListener(v -> {

            String referencia = edtReferencia.getText().toString().trim();

            if (referencia.isEmpty()) {

                edtReferencia.setError("Capture una referencia");
                edtReferencia.requestFocus();

                return;
            }

            String origen = txtOrigen.getText().toString().trim();
            String destino = txtDestino.getText().toString().trim();

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

                        Intent intent = new Intent(
                                MainActivity.this,
                                ActividadesActivity.class
                        );

                        intent.putExtra("origen", origen);
                        intent.putExtra("destino", destino);
                        intent.putExtra("referencia", referencia);

                        startActivity(intent);

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
}