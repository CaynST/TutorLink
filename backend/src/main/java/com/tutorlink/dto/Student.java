package com.tutorlink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @NotBlank
    @Email
    private String correo;

    @NotBlank
    @Size(min = 8, max = 30)
    private String contrasena;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;

    private String matricula; // opcional
    private String telefono; // opcional
    private String correoAlternativo; // opcional
    private String ciudad; // opcional
    private String pais; // opcional
    private String fotoPerfilUrl; // opcional
}
