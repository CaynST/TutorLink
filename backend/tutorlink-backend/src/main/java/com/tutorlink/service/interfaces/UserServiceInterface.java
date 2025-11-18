package com.tutorlink.service.interfaces;

import com.tutorlink.model.Usuario;

import java.util.List;

public interface UserServiceInterface {
    Usuario registrarAlumno(String correo,
                            String contrasenaHash,
                            String nombre,
                            String apellidos,
                            String matricula,
                            Long idPlanEducativo,
                            Long idTutorAsignado);

    Usuario registrarTutor(String correo,
                           String contrasenaHash,
                           String nombre,
                           String apellidos,
                           String telefono,
                           Long idProgramaEducativo);

    Usuario login(String correo, String contrasenaRaw);

    Usuario consultarPerfil(Long idUsuario);

    Usuario actualizarFotoPerfil(Long idUsuario, String nuevaUrlFoto);

    List<Usuario> obtenerTutoradosDeTutor(Long idTutor);

    List<Usuario> buscarTutorados(Long idTutor, String query);

    List<Usuario> filtrarTutorados(Long idTutor, Long idPlanEducativo, Integer semestre);
}
