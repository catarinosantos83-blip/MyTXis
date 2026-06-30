package com.cibergoliath.mytxis;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import retrofit2.http.GET;
import retrofit2.http.Query;
import okhttp3.ResponseBody;

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
            @Field("destino") String destino,
            @Field("referencia") String referencia
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

    @FormUrlEncoded
    @POST("actualizar_estado_en_camino.php")
    Call<String> iniciarViaje(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("actualizar_estado_finalizado.php")
    Call<String> finalizarViaje(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("obtener_ultimo_viaje.php")
    Call<UltimoViajeResponse> obtenerUltimoViaje(
            @Field("usuario_email") String usuarioEmail
    );

    @FormUrlEncoded
    @POST("obtener_usuario.php")
    Call<UsuarioResponse> obtenerUsuario(
            @Field("email") String email
    );

    @GET("obtener_ruta.php")
    Call<ResponseBody> obtenerRuta(
            @Query("origen") String origen,
            @Query("destino") String destino
    );

    @POST("cerrar_sesion.php")
    Call<String> logout();
}