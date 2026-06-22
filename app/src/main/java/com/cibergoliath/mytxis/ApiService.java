package com.cibergoliath.mytxis;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

    @POST("cerrar_sesion.php")
    Call<String> logout();
}