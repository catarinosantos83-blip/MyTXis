package com.cibergoliath.mytxis.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocoderHelper {

    public static String obtenerDireccion(
            Context context,
            double latitud,
            double longitud) {

        try {

            Geocoder geocoder =
                    new Geocoder(context, Locale.getDefault());

            List<Address> direcciones =
                    geocoder.getFromLocation(
                            latitud,
                            longitud,
                            1
                    );

            if (direcciones != null && !direcciones.isEmpty()) {

                Address direccion = direcciones.get(0);

                return direccion.getAddressLine(0);

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

        return "Ubicación no disponible";

    }

}
