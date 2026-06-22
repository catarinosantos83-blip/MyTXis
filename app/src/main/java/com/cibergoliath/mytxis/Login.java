package com.cibergoliath.mytxis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    TextView txtRegistrate;

    Button btnIngresar;

    EditText txtUsuario;
    EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        boolean sesionActiva = getSharedPreferences(
                "sesion",
                MODE_PRIVATE
        ).getBoolean("logueado", false);

        if (sesionActiva) {

            String tipoUsuario = getSharedPreferences(
                    "sesion",
                    MODE_PRIVATE
            ).getString("tipo_usuario", "");

            Intent intent;

            switch (tipoUsuario) {

                case "admin":

                    intent = new Intent(
                            Login.this,
                            MainAdminActivity.class
                    );
                    break;

                case "conductor":

                    intent = new Intent(
                            Login.this,
                            ConductorActivity.class
                    );
                    break;

                default:

                    intent = new Intent(
                            Login.this,
                            MainActivity.class
                    );
                    break;
            }

            startActivity(intent);
            finish();
        }



        txtRegistrate = findViewById(R.id.txtRegistrate);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnIngresar = findViewById(R.id.btnIngresar);

        txtRegistrate.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });

        btnIngresar.setOnClickListener(v -> {

            String email = txtUsuario.getText().toString().trim();
            String pass = txtPassword.getText().toString().trim();

            if(email.isEmpty() || pass.isEmpty()) {

                Toast.makeText(
                        Login.this,
                        "Completa todos los campos",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Call<String> call = apiService.loginUsuario(
                    email,
                    pass
            );

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {

                    if(response.isSuccessful() &&
                            response.body() != null) {

                        String resultado = response.body().trim();

                        if(resultado.equalsIgnoreCase("admin")) {

                            getSharedPreferences("sesion", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("logueado", true)
                                    .putString("tipo_usuario", "admin")
                                    .apply();

                            Intent intent = new Intent(
                                    Login.this,
                                    MainAdminActivity.class
                            );

                            startActivity(intent);
                            finish();

                        }
                        else if(resultado.equalsIgnoreCase("cliente")) {

                            getSharedPreferences("sesion", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("logueado", true)
                                    .putString("tipo_usuario", "cliente")
                                    .apply();

                            Intent intent = new Intent(
                                    Login.this,
                                    MainActivity.class
                            );

                            startActivity(intent);
                            finish();

                        }
                        else if(resultado.equalsIgnoreCase("conductor")) {

                            getSharedPreferences("sesion", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("logueado", true)
                                    .putString("tipo_usuario", "conductor")
                                    .apply();

                            Intent intent = new Intent(
                                    Login.this,
                                    ConductorActivity.class
                            );


                            startActivity(intent);
                            finish();

                        }
                        else {

                            Toast.makeText(
                                    Login.this,
                                    resultado,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call,
                                      Throwable t) {

                    Toast.makeText(
                            Login.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });


        });




    }
}