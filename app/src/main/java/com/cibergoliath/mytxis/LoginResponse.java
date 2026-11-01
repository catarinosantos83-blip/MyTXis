package com.cibergoliath.mytxis;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("tipo_usuario")
    private String tipoUsuario;

    @SerializedName("token")
    private String token;

    @SerializedName("mensaje")
    private String mensaje;

    public boolean isSuccess() {
        return success;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public String getToken() {
        return token;
    }

    public String getMensaje() {
        return mensaje;
    }
}