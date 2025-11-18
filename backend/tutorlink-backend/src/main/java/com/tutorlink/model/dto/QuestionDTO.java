package com.tutorlink.model.dto;

import com.tutorlink.model.Pregunta;
import java.time.LocalDateTime;

/**
 * Representación de una pregunta para respuestas JSON.
 */
public class QuestionDTO {
    private Long id;
    private String titulo;
    private String texto;
    private String estado;
    private LocalDateTime fechaCreacion;
    private String scopeTipo;
    private Long facultadId;
    private Long planEducativoId;
    private Long autorId;
    private String autorNombre;

    public QuestionDTO() {}

    public static QuestionDTO fromEntity(Pregunta p) {
        QuestionDTO dto = new QuestionDTO();
        dto.id = p.getIdPregunta();
        dto.titulo = p.getTitulo();
        dto.texto = p.getTexto();
        dto.estado = p.getEstado();
        dto.fechaCreacion = p.getFechaCreacion();
        dto.scopeTipo = p.getScopeTipo();
        dto.facultadId = (p.getFacultadScope() != null ? p.getFacultadScope().getIdFacultad() : null);
        dto.planEducativoId = (p.getPlanEducativoScope() != null ? p.getPlanEducativoScope().getIdPlan() : null);
        dto.autorId = (p.getAutor() != null ? p.getAutor().getIdUsuario() : null);
        dto.autorNombre = (p.getAutor() != null ? p.getAutor().getNombre() : null);
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Long getFacultadId() { return facultadId; }
    public void setFacultadId(Long facultadId) { this.facultadId = facultadId; }
    public Long getPlanEducativoId() { return planEducativoId; }
    public void setPlanEducativoId(Long planEducativoId) { this.planEducativoId = planEducativoId; }
    public Long getAutorId() { return autorId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }
    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }
}
