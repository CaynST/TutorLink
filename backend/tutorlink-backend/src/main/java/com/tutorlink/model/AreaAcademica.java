package com.tutorlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "area_academica")
public class AreaAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArea;

    @Column(name = "nombre_area", unique = true, nullable = false)
    private String nombreArea;

    // Constructores
    public AreaAcademica() {}

    public AreaAcademica(String nombreArea) {
        this.nombreArea = nombreArea;
    }

    // Getters y Setters
    public Long getIdArea() { return idArea; }
    public void setIdArea(Long idArea) { this.idArea = idArea; }

    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }
}