package com.tutorlink.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud para crear una nueva pregunta.
 */
public class CreateQuestionRequest {
    @NotBlank
    private String titulo;
    @NotBlank
    private String texto;
    @NotBlank
    private String scopeTipo; // GENERAL, FACULTAD, PLAN
    private Long idFacultadScope; // opcional
    private Long idPlanEducativoScope; // opcional

    public CreateQuestionRequest() {}

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getScopeTipo() { return scopeTipo; }
    public void setScopeTipo(String scopeTipo) { this.scopeTipo = scopeTipo; }
    public Long getIdFacultadScope() { return idFacultadScope; }
    public void setIdFacultadScope(Long idFacultadScope) { this.idFacultadScope = idFacultadScope; }
    public Long getIdPlanEducativoScope() { return idPlanEducativoScope; }
    public void setIdPlanEducativoScope(Long idPlanEducativoScope) { this.idPlanEducativoScope = idPlanEducativoScope; }
}
