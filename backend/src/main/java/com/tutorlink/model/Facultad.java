package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facultad",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_facultad_nombre", columnNames = {"nombre_facultad"})
       })
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facultad")
    private Integer idFacultad;

    @ManyToOne
    @JoinColumn(name = "id_region")
    private Region region;

    @Column(name = "nombre_facultad", nullable = false)
    private String nombreFacultad;
}
