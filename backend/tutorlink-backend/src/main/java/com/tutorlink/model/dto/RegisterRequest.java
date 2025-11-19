package com.tutorlink.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para petición de registro.
 */
public class RegisterRequest {

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    @Size(min = 6)
    private String contrasena;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;

    /** Tipo de usuario: ESTUDIANTE | TUTOR */
    @NotBlank
    private String rol;

    // Campos opcionales para estudiante
    private String matricula;
    private Long idPlanEducativo;
    private Long idTutorAsignado;

    // Campos opcionales para tutor
    private String telefono;
    private Long idProgramaEducativo;

    public RegisterRequest() {}

    // Getters y setters
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public Long getIdPlanEducativo() { return idPlanEducativo; }
    public void setIdPlanEducativo(Long idPlanEducativo) { this.idPlanEducativo = idPlanEducativo; }

    public Long getIdTutorAsignado() { return idTutorAsignado; }
    public void setIdTutorAsignado(Long idTutorAsignado) { this.idTutorAsignado = idTutorAsignado; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Long getIdProgramaEducativo() { return idProgramaEducativo; }
    public void setIdProgramaEducativo(Long idProgramaEducativo) { this.idProgramaEducativo = idProgramaEducativo; }
}
