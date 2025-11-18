package com.tutorlink.model.dto;

import com.tutorlink.model.Respuesta;
import java.time.LocalDateTime;

/**
 * Representación de una respuesta.
 */
public class AnswerDTO {
    private Long id;
    private Long preguntaId;
    private String contenido;
    private Integer version;
    private String estado;
    private LocalDateTime fecha;
    private Long aprobadorId;

    public AnswerDTO() {}

    public static AnswerDTO fromEntity(Respuesta r) {
        AnswerDTO dto = new AnswerDTO();
        dto.id = r.getIdRespuesta();
        dto.preguntaId = (r.getPregunta() != null ? r.getPregunta().getIdPregunta() : null);
        dto.contenido = r.getContenido();
        dto.version = r.getVersionRespuesta();
        dto.estado = r.getEstadoRespuesta();
        dto.fecha = r.getFechaRespuesta();
        dto.aprobadorId = (r.getAprobador() != null ? r.getAprobador().getIdUsuario() : null);
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPreguntaId() { return preguntaId; }
    public void setPreguntaId(Long preguntaId) { this.preguntaId = preguntaId; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Long getAprobadorId() { return aprobadorId; }
    public void setAprobadorId(Long aprobadorId) { this.aprobadorId = aprobadorId; }
}
