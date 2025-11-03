package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_usuario_matricula", columnNames = {"matricula"}),
           @UniqueConstraint(name = "uk_usuario_correo", columnNames = {"correo"})
       })
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "matricula", unique = true)
    private String matricula;

    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "contrasena", nullable = false)
    private String contrasena; // Se guardará con HASH

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "correo_alternativo")
    private String correoAlternativo;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "pais")
    private String pais;

    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl; // Para RF-15
}
