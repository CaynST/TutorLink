package com.tutorlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaSummaryDto {
    private Long id_pregunta;
    private String titulo;
    private String estado;
    private Instant fecha_creacion;
    private AutorDto autor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AutorDto {
        private String nombre;
        private String apellidos;
    }
}
