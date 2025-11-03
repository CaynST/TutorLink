package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_estudiante")
public class DetalleEstudiante {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // Relación 1 a 1 con Usuario

    @ManyToOne
    @JoinColumn(name = "id_plan_educativo")
    private PlanEducativo planEducativo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tutor_asignado", nullable = false)
    private Usuario tutorAsignado; // Regla: 1 solo tutor

    @Column(name = "semestre")
    private Integer semestre;

    @Column(name = "grado")
    private String grado;
}
