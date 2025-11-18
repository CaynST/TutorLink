package com.tutorlink.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitar inicio de sesión.
 */
public class LoginRequest {

    @Email
    @NotBlank
    private String correo;

    @NotBlank
    private String contrasena;

    public LoginRequest() {}

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
