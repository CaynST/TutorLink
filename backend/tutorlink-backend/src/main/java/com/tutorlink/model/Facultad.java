package com.tutorlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "facultad")
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facultad", nullable = false)
    private Long idFacultad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region")
    private Region region;

    @Column(name = "nombre_facultad", unique = true, nullable = false)
    private String nombreFacultad;

    // Constructores
    public Facultad() {}

    public Facultad(String nombreFacultad) {
        this.nombreFacultad = nombreFacultad;
    }

    // Getters y Setters
    public Long getIdFacultad() { return idFacultad; }
    public void setIdFacultad(Long idFacultad) { this.idFacultad = idFacultad; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }

    public String getNombreFacultad() { return nombreFacultad; }
    public void setNombreFacultad(String nombreFacultad) { this.nombreFacultad = nombreFacultad; }
}