package com.tutorlink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pregunta")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPregunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor", nullable = false)
    private Usuario autor; // El estudiante que pregunta

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_revisor")
    private Usuario revisor; // El tutor que aprueba/rechaza

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "texto", columnDefinition = "TEXT", nullable = false)
    private String texto;

    @Column(name = "estado", nullable = false)
    private String estado; // PENDIENTE, PUBLICADA, RECHAZADA

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "scope_tipo", nullable = false)
    private String scopeTipo; // GENERAL, FACULTAD, PLAN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facultad_scope")
    private Facultad facultadScope;

    // Relación con PlanEducativo usando la PK 'id_plan'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan_educativo_scope")
    private PlanEducativo planEducativoScope;

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Respuesta> respuestas;

    // Constructores
    public Pregunta() {}

    public Pregunta(Usuario autor, String titulo, String texto, String scopeTipo) {
        this.autor = autor;
        this.titulo = titulo;
        this.texto = texto;
        this.estado = "PENDIENTE";
        this.fechaCreacion = LocalDateTime.now();
        this.scopeTipo = scopeTipo;
    }

    // Getters y Setters
    public Long getIdPregunta() { return idPregunta; }
    public void setIdPregunta(Long idPregunta) { this.idPregunta = idPregunta; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public Usuario getRevisor() { return revisor; }
    public void setRevisor(Usuario revisor) { this.revisor = revisor; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getScopeTipo() { return scopeTipo; }
    public void setScopeTipo(String scopeTipo) { this.scopeTipo = scopeTipo; }

    public Facultad getFacultadScope() { return facultadScope; }
    public void setFacultadScope(Facultad facultadScope) { this.facultadScope = facultadScope; }

    public PlanEducativo getPlanEducativoScope() { return planEducativoScope; }
    public void setPlanEducativoScope(PlanEducativo planEducativoScope) { this.planEducativoScope = planEducativoScope; }

    public List<Respuesta> getRespuestas() { return respuestas; }
    public void setRespuestas(List<Respuesta> respuestas) { this.respuestas = respuestas; }
}