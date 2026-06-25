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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


        txtFechaActividad = findViewById(R.id.txtFechaActividad);
        txtEstadoActividad = findViewById(R.id.txtEstadoActividad);

        String emailUsuario =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                ).getString("email", "");

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        Call<UltimoViajeResponse> call =
                apiService.obtenerUltimoViaje(
                        emailUsuario
                );

        call.enqueue(new Callback<UltimoViajeResponse>() {

            @Override
            public void onResponse(
                    Call<UltimoViajeResponse> call,
                    Response<UltimoViajeResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    UltimoViajeResponse viaje =
                            response.body();

                    txtOrigenActividad.setText(
                            "Origen: "
                                    + viaje.getPunto_partida());

                    txtDestinoActividad.setText(
                            "Destino: "
                                    + viaje.getDestino());

                    txtReferenciaActividad.setText(
                            "Referencia: "
                                    + viaje.getReferencia());

                    txtEstadoActividad.setText(
                            "Estado: "
                                    + viaje.getEstado());

                    txtFechaActividad.setText(
                            "Fecha: "
                                    + viaje.getFecha());
                }
            }

            @Override
            public void onFailure(
                    Call<UltimoViajeResponse> call,
                    Throwable t) {

            }
        });


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