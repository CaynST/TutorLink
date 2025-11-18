package com.tutorlink.service.interfaces;

public interface AdminServiceInterface {
    void aprobarRegistroUsuario(Long idUsuario);
    void asignarTutorAEstudiante(Long idEstudiante, Long idTutor);
    void cambiarEstadoUsuario(Long idUsuario, boolean activo);
    void gestionarPreguntaEliminar(Long idPregunta);
}
