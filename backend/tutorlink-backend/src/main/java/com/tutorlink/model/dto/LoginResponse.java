package com.tutorlink.model.dto;

/**
 * DTO de respuesta al iniciar sesión.
 */
public class LoginResponse {
    private String token;
    private UserDTO usuario;

    public LoginResponse() {}

    public LoginResponse(String token, UserDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserDTO getUsuario() { return usuario; }
    public void setUsuario(UserDTO usuario) { this.usuario = usuario; }
}
