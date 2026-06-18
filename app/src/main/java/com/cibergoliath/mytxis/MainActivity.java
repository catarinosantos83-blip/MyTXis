package com.cibergoliath.mytxis;

import android.os.Bundle;
import android.widget.EditText;

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

        bottomNavigation = findViewById(R.id.bottomNavigation);

        btnSolicitarViaje.setOnClickListener(v -> {

            String origen = txtOrigen.getText().toString();
            String destino = txtDestino.getText().toString();
            String referencia = edtReferencia.getText().toString();

            Intent intent = new Intent(
                    MainActivity.this,
                    ActividadesActivity.class
            );

            intent.putExtra("origen", origen);
            intent.putExtra("destino", destino);
            intent.putExtra("referencia", referencia);

            startActivity(intent);

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