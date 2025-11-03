package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "region",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_region_nombre", columnNames = {"nombre_region"})
       })
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private Integer idRegion;

    @Column(name = "nombre_region", nullable = false)
    private String nombreRegion;
}
