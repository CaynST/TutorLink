package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rol",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_rol_nombre", columnNames = {"nombre_rol"})
       })
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false)
    private String nombreRol; // Valores: "ESTUDIANTE","TUTOR","ADMIN","SUDO","LLM"
}
