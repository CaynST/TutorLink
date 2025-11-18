package com.tutorlink.business.interfaces;

/**
 * Reglas de negocio para operaciones de administración del sistema.
 */
public interface AdminBusinessInterface {

    /** Aprueba el registro de un usuario (cambia estado o marca como verificado). */
    void aprobarRegistroUsuario(Long idUsuario);

    /** Asigna un tutor a un estudiante. */
    void asignarTutorAEstudiante(Long idEstudiante, Long idTutor);

    /** Desactiva/activa un usuario (gestión básica). */
    void cambiarEstadoUsuario(Long idUsuario, boolean activo);

    /** Elimina o bloquea una pregunta del sistema. */
    void gestionarPreguntaEliminar(Long idPregunta);
}
