package com.cibergoliath.mytxis;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActividadesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actividades);

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