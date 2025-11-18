package com.tutorlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_tutor")
public class DetalleTutor {

    @Id
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public DetalleTutor() {}

    public DetalleTutor(Usuario usuario) {
        this.usuario = usuario;
        this.idUsuario = usuario != null ? usuario.getIdUsuario() : null;
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) this.idUsuario = usuario.getIdUsuario();
    }
}
