package com.tutorlink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "respuesta")
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private Pregunta pregunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor", nullable = false)
    private Usuario autor; // El LLM que responde

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aprobador")
    private Usuario aprobador; // El tutor que aprueba/rechaza

    @Column(name = "contenido", columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(name = "version_respuesta", nullable = false)
    private Integer versionRespuesta = 1;

    @Column(name = "estado_respuesta", nullable = false)
    private String estadoRespuesta; // PENDIENTE_REVISION, PUBLICADA

    @Column(name = "fecha_respuesta", nullable = false)
    private LocalDateTime fechaRespuesta;

    // Constructores
    public Respuesta() {}

    public Respuesta(Pregunta pregunta, Usuario autor, String contenido) {
        this.pregunta = pregunta;
        this.autor = autor;
        this.contenido = contenido;
        this.fechaRespuesta = LocalDateTime.now();
        this.estadoRespuesta = "PENDIENTE_REVISION";
    }

    // Getters y Setters
    public Long getIdRespuesta() { return idRespuesta; }
    public void setIdRespuesta(Long idRespuesta) { this.idRespuesta = idRespuesta; }

    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public Usuario getAprobador() { return aprobador; }
    public void setAprobador(Usuario aprobador) { this.aprobador = aprobador; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Integer getVersionRespuesta() { return versionRespuesta; }
    public void setVersionRespuesta(Integer versionRespuesta) { this.versionRespuesta = versionRespuesta; }

    public String getEstadoRespuesta() { return estadoRespuesta; }
    public void setEstadoRespuesta(String estadoRespuesta) { this.estadoRespuesta = estadoRespuesta; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}