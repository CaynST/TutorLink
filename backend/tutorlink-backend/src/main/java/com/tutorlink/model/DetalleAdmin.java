package com.tutorlink.model;

import jakarta.persistence.*;

/**
 * Detalle adicional para usuarios con rol ADMIN.
 * Mantiene una relación 1:1 con la tabla usuario reutilizando la PK (id_usuario).
 * Por ahora no se definen campos extra; se pueden agregar según necesidades.
 */
@Entity
@Table(name = "detalle_admin")
public class DetalleAdmin {

    @Id
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario; // Misma PK que usuario

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Usa la misma PK del usuario
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public DetalleAdmin() {}

    public DetalleAdmin(Usuario usuario) {
        this.usuario = usuario;
        this.idUsuario = usuario != null ? usuario.getIdUsuario() : null;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            this.idUsuario = usuario.getIdUsuario();
        }
    }
}
