package com.cibergoliath.mytxis.models;

public class ConductorDisponible {

    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private double latitud;
    private double longitud;
    private String fecha_actualizacion;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getColor() {
        return color;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getFecha_actualizacion() {
        return fecha_actualizacion;
    }
}