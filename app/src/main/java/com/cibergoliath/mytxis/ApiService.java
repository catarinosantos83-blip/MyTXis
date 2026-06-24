package com.cibergoliath.mytxis;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import retrofit2.http.GET;

public interface ApiService {

    @FormUrlEncoded
    @POST("insertar.php")
    Call<String> insertarUsuario(
            @Field("nombre") String nombre,
            @Field("email") String email,
            @Field("telefono") String telefono,
            @Field("pass") String pass
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<String> loginUsuario(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @FormUrlEncoded
    @POST("actualizar_tipo_usuario.php")
    Call<String> actualizarTipoUsuario(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("solicitar_viaje.php")
    Call<String> solicitarViaje(
            @Field("email") String email,
            @Field("punto_partida") String puntoPartida,
            @Field("destino") String destino
    );

    @GET("obtener_viaje_pendiente.php")
    Call<ViajeResponse> obtenerViajePendiente();

    @FormUrlEncoded
    @POST("aceptar_viaje.php")
    Call<String> aceptarViaje(
            @Field("id") int id,
            @Field("conductor_email") String conductorEmail
    );

    @FormUrlEncoded
    @POST("guardar_datos_conductor.php")
    Call<String> guardarDatosConductor(
            @Field("email") String email,
            @Field("placa") String placa,
            @Field("marca") String marca,
            @Field("modelo") String modelo,
            @Field("color") String color
    );

    @FormUrlEncoded
    @POST("obtener_conductor_cliente.php")
    Call<ConductorInfoResponse> obtenerConductorCliente(
            @Field("usuario_email") String usuarioEmail
    );

    @POST("cerrar_sesion.php")
    Call<String> logout();
}