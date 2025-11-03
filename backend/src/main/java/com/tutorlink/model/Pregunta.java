package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import com.tutorlink.model.enums.EstadoPregunta;
import com.tutorlink.model.enums.ScopePregunta;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pregunta")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta")
    private Integer idPregunta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_autor", nullable = false)
    private Usuario autor; // Quien pregunta

    @ManyToOne
    @JoinColumn(name = "id_revisor")
    private Usuario revisor; // Tutor que aprueba/rechaza

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPregunta estado; // PENDIENTE, PUBLICADA, RECHAZADA

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope_tipo", nullable = false)
    private ScopePregunta scopeTipo; // GENERAL, FACULTAD, PLAN

    @ManyToOne
    @JoinColumn(name = "id_facultad_scope")
    private Facultad facultadScope;

    @ManyToOne
    @JoinColumn(name = "id_plan_educativo_scope")
    private PlanEducativo planEducativoScope;
}
