package com.tutorlink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "matricula", unique = true, nullable = true) // Puede ser nulo para algunos roles
    private String matricula;

    @Column(name = "correo", unique = true, nullable = false)
    @Email
    private String correo;

    @NotBlank
    @Size(min = 8)
    @Column(name = "contrasena", nullable = false) // Debería almacenarse como hash
    private String contrasena;

    @NotBlank
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank
    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "telefono", nullable = true)
    private String telefono;

    @Email
    @Column(name = "correo_alternativo", nullable = true)
    private String correoAlternativo;

    @Column(name = "ciudad", nullable = true)
    private String ciudad;

    @Column(name = "pais", nullable = true)
    private String pais;

    @Column(name = "foto_perfil_url", nullable = true)
    private String fotoPerfilUrl;

    // Relaciones inversas (preguntas hechas por este usuario)
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pregunta> preguntasHechas;

    // Relaciones inversas (respuestas generadas por este usuario - LLM)
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Respuesta> respuestasGeneradas;

    // Relaciones inversas (preguntas revisadas por este usuario - Tutor/Admin)
    @OneToMany(mappedBy = "revisor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pregunta> preguntasRevisadas;

    // Relaciones inversas (respuestas aprobadas/rechazadas por este usuario - Tutor)
    @OneToMany(mappedBy = "aprobador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Respuesta> respuestasAprobadas;

    // Constructores
    public Usuario() {}

    public Usuario(String correo, String contrasena, String nombre, String apellidos, Rol rol) {
        this.correo = correo;
        this.contrasena = contrasena; // Debería ser el hash
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.rol = rol;
    }

    // Getters y Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoAlternativo() { return correoAlternativo; }
    public void setCorreoAlternativo(String correoAlternativo) { this.correoAlternativo = correoAlternativo; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }

    public List<Pregunta> getPreguntasHechas() { return preguntasHechas; }
    public void setPreguntasHechas(List<Pregunta> preguntasHechas) { this.preguntasHechas = preguntasHechas; }

    public List<Respuesta> getRespuestasGeneradas() { return respuestasGeneradas; }
    public void setRespuestasGeneradas(List<Respuesta> respuestasGeneradas) { this.respuestasGeneradas = respuestasGeneradas; }

    public List<Pregunta> getPreguntasRevisadas() { return preguntasRevisadas; }
    public void setPreguntasRevisadas(List<Pregunta> preguntasRevisadas) { this.preguntasRevisadas = preguntasRevisadas; }

    public List<Respuesta> getRespuestasAprobadas() { return respuestasAprobadas; }
    public void setRespuestasAprobadas(List<Respuesta> respuestasAprobadas) { this.respuestasAprobadas = respuestasAprobadas; }
}