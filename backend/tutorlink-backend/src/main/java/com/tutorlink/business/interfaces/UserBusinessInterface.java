package com.tutorlink.business.interfaces;

import com.tutorlink.model.Usuario;

import java.util.List;

/**
 * Reglas de negocio relacionadas con usuarios (alumnos, tutores, admins y LLM).
 */
public interface UserBusinessInterface {

    /**
     * Registra un alumno validando unicidad de correo/matrícula y relacionándolo con su plan y tutor.
     */
    Usuario registrarAlumno(String correo,
                            String contrasenaHash,
                            String nombre,
                            String apellidos,
                            String matricula,
                            Long idPlanEducativo,
                            Long idTutorAsignado);

    /**
     * Registra un tutor validando unicidad de correo y asignando metadatos iniciales.
     */
    Usuario registrarTutor(String correo,
                           String contrasenaHash,
                           String nombre,
                           String apellidos,
                           String telefono,
                           Long idProgramaEducativo);

    /**
     * Valida credenciales y retorna el usuario; la emisión de JWT la maneja la capa de autenticación.
     */
    Usuario login(String correo, String contrasenaRaw);

    /** Consulta el perfil del usuario por su id. */
    Usuario consultarPerfil(Long idUsuario);

    /** Actualiza la URL de la foto de perfil del usuario. */
    Usuario actualizarFotoPerfil(Long idUsuario, String nuevaUrlFoto);

    /** Obtiene los alumnos tutorados de un tutor. */
    List<Usuario> obtenerTutoradosDeTutor(Long idTutor);

    /** Busca tutorados de un tutor por texto (nombre, apellidos, matrícula, correo). */
    List<Usuario> buscarTutorados(Long idTutor, String query);

    /** Filtra tutorados por plan educativo y/o semestre. */
    List<Usuario> filtrarTutorados(Long idTutor, Long idPlanEducativo, Integer semestre);
}
