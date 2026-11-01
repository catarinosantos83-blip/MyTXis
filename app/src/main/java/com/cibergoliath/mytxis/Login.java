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

            Call<LoginResponse> call = apiService.loginSeguro(
                    email,
                    pass
            );

            call.enqueue(new Callback<LoginResponse>() {

                @Override
                public void onResponse(
                        Call<LoginResponse> call,
                        Response<LoginResponse> response) {

                    if (!response.isSuccessful()) {

                        String mensaje =
                                "No fue posible iniciar sesión. Código: "
                                        + response.code();

                        try {
                            if (response.errorBody() != null) {

                                LoginResponse error =
                                        new com.google.gson.Gson().fromJson(
                                                response.errorBody().string(),
                                                LoginResponse.class
                                        );

                                if (error != null
                                        && error.getMensaje() != null) {
                                    mensaje = error.getMensaje();
                                }
                            }
                        } catch (Exception e) {
                            // Se conserva el mensaje HTTP como alternativa.
                        }

                        Toast.makeText(
                                Login.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    LoginResponse resultado = response.body();

                    if (resultado == null || !resultado.isSuccess()) {

                        String mensaje = resultado != null
                                && resultado.getMensaje() != null
                                ? resultado.getMensaje()
                                : "Respuesta inválida del servidor";

                        Toast.makeText(
                                Login.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String tipoUsuario = resultado.getTipoUsuario();
                    String token = resultado.getToken();

                    if (tipoUsuario == null
                            || token == null
                            || token.trim().isEmpty()) {

                        Toast.makeText(
                                Login.this,
                                "El servidor no devolvió una sesión válida",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    boolean esAdmin =
                            tipoUsuario.equalsIgnoreCase("admin");

                    boolean esCliente =
                            tipoUsuario.equalsIgnoreCase("cliente");

                    boolean esConductor =
                            tipoUsuario.equalsIgnoreCase("conductor");

                    if (!esAdmin && !esCliente && !esConductor) {

                        Toast.makeText(
                                Login.this,
                                "Tipo de usuario no reconocido",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // Guardar los datos de la sesión y el token.
                    getSharedPreferences("sesion", MODE_PRIVATE)
                            .edit()
                            .putBoolean("logueado", true)
                            .putString("tipo_usuario", tipoUsuario)
                            .putString("email", email)
                            .putString("token", token)
                            .apply();

                    Intent intent;

                    if (esAdmin) {

                        intent = new Intent(
                                Login.this,
                                MainAdminActivity.class
                        );

                    } else if (esCliente) {

                        intent = new Intent(
                                Login.this,
                                MainActivity.class
                        );

                    } else {

                        intent = new Intent(
                                Login.this,
                                ConductorActivity.class
                        );
                    }

                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(
                        Call<LoginResponse> call,
                        Throwable t) {

                    Toast.makeText(
                            Login.this,
                            "Error de conexión: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });


        });




    }
}