package com.tutorlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "programa_educativo")
public class ProgramaEducativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrograma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facultad", nullable = false)
    private Facultad facultad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_area", nullable = false)
    private AreaAcademica area;

    @Column(name = "nombre_programa", unique = true, nullable = false)
    private String nombrePrograma;

    // Constructores
    public ProgramaEducativo() {}

    public ProgramaEducativo(String nombrePrograma) {
        this.nombrePrograma = nombrePrograma;
    }

    // Getters y Setters
    public Long getIdPrograma() { return idPrograma; }
    public void setIdPrograma(Long idPrograma) { this.idPrograma = idPrograma; }

    public Facultad getFacultad() { return facultad; }
    public void setFacultad(Facultad facultad) { this.facultad = facultad; }

    public AreaAcademica getArea() { return area; }
    public void setArea(AreaAcademica area) { this.area = area; }

    public String getNombrePrograma() { return nombrePrograma; }
    public void setNombrePrograma(String nombrePrograma) { this.nombrePrograma = nombrePrograma; }
}