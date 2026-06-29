package com.cibergoliath.mytxis;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.widget.Button;
import android.content.Intent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MapaActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    private Marker marcador;

    private LatLng puntoSeleccionado;

    private Button btnConfirmarUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa);



        btnConfirmarUbicacion =
                findViewById(R.id.btnConfirmarUbicacion);

        btnConfirmarUbicacion.setEnabled(false);


        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMapClickListener(this);

        LatLng temascalcingo = new LatLng(19.9169, -100.0030);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        temascalcingo,
                        15
                )
        );

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        puntoSeleccionado = latLng;

        if (marcador != null) {
            marcador.remove();
        }

        marcador = mMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title("Origen seleccionado")
        );

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        17
                )
        );

        btnConfirmarUbicacion.setEnabled(true);

    }
}