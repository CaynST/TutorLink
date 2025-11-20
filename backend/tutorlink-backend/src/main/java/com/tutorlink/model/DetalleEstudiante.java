package com.tutorlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_estudiante")
public class DetalleEstudiante {

    @Id
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario; // Relación 1 a 1 con Usuario

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // La clave primaria de esta entidad es la misma que la de Usuario
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // Referencia al estudiante (usuario con rol ESTUDIANTE)

    // Relación con PlanEducativo usando la PK 'id_plan'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan_educativo")
    private PlanEducativo planEducativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tutor_asignado", nullable = true)
    private Usuario tutorAsignado; // Referencia al tutor (usuario con rol TUTOR)

    @Column(name = "semestre", nullable = true)
    private Integer semestre;

    @Column(name = "grado", nullable = true)
    private String grado;

    // Constructores
    public DetalleEstudiante() {}

    public DetalleEstudiante(Usuario usuario, Usuario tutorAsignado) {
        this.usuario = usuario;
        // do NOT set idUsuario here; @MapsId will copy the generated id from `usuario`
        this.tutorAsignado = tutorAsignado;
    }

    // Getters y Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public PlanEducativo getPlanEducativo() { return planEducativo; }
    public void setPlanEducativo(PlanEducativo planEducativo) { this.planEducativo = planEducativo; }

    public Usuario getTutorAsignado() { return tutorAsignado; }
    public void setTutorAsignado(Usuario tutorAsignado) { this.tutorAsignado = tutorAsignado; }

    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }
}