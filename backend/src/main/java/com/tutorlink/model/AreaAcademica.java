package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "area_academica",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_area_nombre", columnNames = {"nombre_area"})
       })
public class AreaAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_area")
    private Integer idArea;

    @Column(name = "nombre_area", nullable = false)
    private String nombreArea;
}
