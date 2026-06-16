package com.cibergoliath.mytxis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.EditText;
import android.widget.Toast;


public class Registro extends AppCompatActivity {

    Button btnRegresar;
    Button btnRegistrar;
    EditText txtNombre;
    EditText txtEmail;
    EditText txtTelefono;
    EditText txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        btnRegresar = findViewById(R.id.btnregresar);

        txtNombre = findViewById(R.id.txtnombre);
        txtEmail = findViewById(R.id.txtEmail);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtPassword = findViewById(R.id.txtPassword);


        btnRegistrar = findViewById(R.id.btnregistrar);

        btnRegresar.setOnClickListener(v -> {



            Intent intent = new Intent(Registro.this, Login.class);
            startActivity(intent);
            finish();
        });

        btnRegistrar.setOnClickListener(v -> {




            String nombre = txtNombre.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String telefono = txtTelefono.getText().toString().trim();
            String pass = txtPassword.getText().toString().trim();

            if(nombre.isEmpty() || email.isEmpty() ||
                    telefono.isEmpty() || pass.isEmpty()) {

                Toast.makeText(Registro.this,
                        "Completa todos los campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            Call<String> call = apiService.insertarUsuario(
                    nombre,
                    email,
                    telefono,
                    pass
            );

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call,
                                       Response<String> response) {
                    if (response.body() != null) {
                        Toast.makeText(
                                Registro.this,
                                response.body(),
                                Toast.LENGTH_LONG
                        ).show();
                    }




                }

                @Override
                public void onFailure(Call<String> call,
                                      Throwable t) {

                    Toast.makeText(
                            Registro.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}