package com.cibergoliath.mytxis.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationHelper {

    private final FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;

    public LocationHelper(Context context) {

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

    }

    public interface OnLocationResult {

        void onLocationReceived(Location location);

        void onError(String mensaje);

    }

    @SuppressLint("MissingPermission")
    public void iniciarActualizacionesUbicacion(
            @NonNull OnLocationResult listener) {

        LocationRequest locationRequest =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        3000
                )
                        .setMinUpdateIntervalMillis(2000)
                        .setMaxUpdateDelayMillis(3000)
                        .build();

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(
                    @NonNull LocationResult locationResult) {

                for (Location location :
                        locationResult.getLocations()) {

                    if (location != null) {

                        listener.onLocationReceived(location);

                    }

                }

            }

        };

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        );
    }

    public void detenerActualizacionesUbicacion() {

        if (locationCallback != null) {

            fusedLocationClient.removeLocationUpdates(
                    locationCallback
            );

            locationCallback = null;
        }
    }
}