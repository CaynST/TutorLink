package com.tutorlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_educativo")
public class PlanEducativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan", nullable = false)
    private Long idPlan; // Clave primaria SERIAL

    // Atributo para la FK que forma parte de la identidad lógica
    @Column(name = "id_programa", nullable = false)
    private Long idPrograma;

    // Atributo para la parte de la identidad lógica
    @Column(name = "nombre_plan", nullable = false)
    private String nombrePlan;

    // Relación con ProgramaEducativo (muchos planes pertenecen a un programa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_programa", insertable = false, updatable = false) // No inserta ni actualiza este campo aquí
    private ProgramaEducativo programa;

    // Constructores
    public PlanEducativo() {}

    public PlanEducativo(ProgramaEducativo programa, String nombrePlan) {
        this.idPrograma = programa.getIdPrograma(); // Inicializa el campo FK
        this.nombrePlan = nombrePlan;
        this.programa = programa;
    }

    // Getters y Setters
    public Long getIdPlan() { return idPlan; }
    public void setIdPlan(Long idPlan) { this.idPlan = idPlan; }

    public Long getIdPrograma() { return idPrograma; }
    public void setIdPrograma(Long idPrograma) { this.idPrograma = idPrograma; }

    public String getNombrePlan() { return nombrePlan; }
    public void setNombrePlan(String nombrePlan) { this.nombrePlan = nombrePlan; }

    public ProgramaEducativo getPrograma() { return programa; }
    public void setPrograma(ProgramaEducativo programa) { this.programa = programa; }
}