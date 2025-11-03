package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "programa_educativo",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_programa_nombre", columnNames = {"nombre_programa"})
       })
public class ProgramaEducativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programa")
    private Integer idPrograma;

    @ManyToOne
    @JoinColumn(name = "id_facultad")
    private Facultad facultad;

    @ManyToOne
    @JoinColumn(name = "id_area")
    private AreaAcademica areaAcademica;

    @Column(name = "nombre_programa", nullable = false)
    private String nombrePrograma;
}
