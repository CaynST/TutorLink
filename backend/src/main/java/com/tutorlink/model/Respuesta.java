package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "respuesta")
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Integer idRespuesta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private Pregunta pregunta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_autor", nullable = false)
    private Usuario autor; // Será el id_usuario del 'LLM'

    @ManyToOne
    @JoinColumn(name = "id_aprobador")
    private Usuario aprobador; // Tutor que la valida/rechaza

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "version_respuesta", nullable = false)
    private Integer versionRespuesta = 1;

    @Column(name = "estado_respuesta", nullable = false)
    private String estadoRespuesta; // "PENDIENTE_REVISION","PUBLICADA"

    @Column(name = "fecha_respuesta")
    private Instant fechaRespuesta;
}
