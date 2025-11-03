package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_tutor")
public class DetalleTutor {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // Relación 1 a 1 con Usuario

    @ManyToOne
    @JoinColumn(name = "id_programa_educativo")
    private ProgramaEducativo programaEducativo; // Programa que tutora

    @Column(name = "grado")
    private String grado;
}
