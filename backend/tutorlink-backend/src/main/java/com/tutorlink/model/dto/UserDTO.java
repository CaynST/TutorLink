package com.tutorlink.model.dto;

import com.tutorlink.model.Usuario;

/**
 * Representación segura de un usuario para respuestas.
 */
public class UserDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String rol;
    private String fotoPerfilUrl;
    private String telefono;

    public UserDTO() {}

    public static UserDTO fromEntity(Usuario u) {
        UserDTO dto = new UserDTO();
        dto.id = u.getIdUsuario();
        dto.nombre = u.getNombre();
        dto.apellidos = u.getApellidos();
        dto.correo = u.getCorreo();
        dto.rol = (u.getRol() != null ? u.getRol().getNombreRol() : null);
        dto.fotoPerfilUrl = u.getFotoPerfilUrl();
        dto.telefono = u.getTelefono();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
