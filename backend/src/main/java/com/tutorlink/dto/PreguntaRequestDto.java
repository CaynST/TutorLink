package com.tutorlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaRequestDto {
    @NotBlank
    private String titulo;

    @NotBlank
    private String texto;

    @NotNull
    @Pattern(regexp = "GENERAL|FACULTAD|PLAN", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String scope_tipo;

    @Pattern(regexp = "PENDIENTE|PUBLICADA|RECHAZADA", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String estado; // opcional; por defecto PENDIENTE si es null

    // Alcance opcional
    private Long id_facultad_scope;
    private Long id_plan_educativo_scope;
}
