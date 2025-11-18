package com.tutorlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @Column(name = "nombre_rol", unique = true, nullable = false)
    private String nombreRol; // ESTUDIANTE, TUTOR, ADMIN, SUDO, LLM

    // Constructores
    public Rol() {}

    public Rol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    // Getters y Setters
    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}