package com.tutorlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegion;

    @Column(name = "nombre_region", unique = true, nullable = false)
    private String nombreRegion;

    // Constructores
    public Region() {}

    public Region(String nombreRegion) {
        this.nombreRegion = nombreRegion;
    }

    // Getters y Setters
    public Long getIdRegion() { return idRegion; }
    public void setIdRegion(Long idRegion) { this.idRegion = idRegion; }

    public String getNombreRegion() { return nombreRegion; }
    public void setNombreRegion(String nombreRegion) { this.nombreRegion = nombreRegion; }
}