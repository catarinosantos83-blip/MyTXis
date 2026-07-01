package com.cibergoliath.mytxis.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {

    private final FusedLocationProviderClient fusedLocationClient;

    public LocationHelper(Context context) {
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);
    }

    public interface OnLocationResult {

        void onLocationReceived(Location location);

        void onError(String mensaje);

    }

    @SuppressLint("MissingPermission")
    public void obtenerUbicacionActual(
            @NonNull OnLocationResult listener) {

        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        listener.onLocationReceived(location);

                    } else {

                        listener.onError(
                                "No fue posible obtener la ubicación."
                        );

                    }

                })
                .addOnFailureListener(e ->

                        listener.onError(e.getMessage())

                );

    }

}
