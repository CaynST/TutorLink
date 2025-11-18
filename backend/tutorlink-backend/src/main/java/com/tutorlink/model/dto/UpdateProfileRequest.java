package com.tutorlink.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud para actualizar datos de perfil. Por ahora sólo la foto.
 */
public class UpdateProfileRequest {
    @NotBlank
    private String fotoPerfilUrl;

    public UpdateProfileRequest() {}

    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }
}
