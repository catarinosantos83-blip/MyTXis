package com.cibergoliath.mytxis;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActividadesActivity extends AppCompatActivity {

    TextView txtOrigenActividad;
    TextView txtDestinoActividad;
    TextView txtReferenciaActividad;

    TextView txtFechaActividad;
    TextView txtEstadoActividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actividades);

        txtOrigenActividad = findViewById(R.id.txtOrigenActividad);
        txtDestinoActividad = findViewById(R.id.txtDestinoActividad);
        txtReferenciaActividad = findViewById(R.id.txtReferenciaActividad);

        txtOrigenActividad = findViewById(R.id.txtOrigenActividad);
        txtDestinoActividad = findViewById(R.id.txtDestinoActividad);
        txtReferenciaActividad = findViewById(R.id.txtReferenciaActividad);

        txtFechaActividad = findViewById(R.id.txtFechaActividad);
        txtEstadoActividad = findViewById(R.id.txtEstadoActividad);

        String fechaActual = new SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
        ).format(new Date());

        txtFechaActividad.setText("Fecha: " + fechaActual);
        txtEstadoActividad.setText("Estado: Pendiente");

        String origen = getIntent().getStringExtra("origen");
        String destino = getIntent().getStringExtra("destino");
        String referencia = getIntent().getStringExtra("referencia");

        if(origen != null){
            txtOrigenActividad.setText("Origen: Ubicación seleccionada");
        }

        if(destino != null){
            txtDestinoActividad.setText("Destino: Ubicación seleccionada");
        }

        if(referencia != null){
            txtReferenciaActividad.setText("Referencia: " + referencia);
        }


        BottomNavigationView bottomNavigation;

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.nav_actividades);

        bottomNavigation.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {

                Intent intent = new Intent(
                        ActividadesActivity.this,
                        MainActivity.class
                );

                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.nav_actividades) {

                return true;

            } else if (item.getItemId() == R.id.nav_usuario) {

                Intent intent = new Intent(
                        ActividadesActivity.this,
                        UsuarioActivity.class
                );

                startActivity(intent);
                return true;
            }

            return false;
        });

    }
}