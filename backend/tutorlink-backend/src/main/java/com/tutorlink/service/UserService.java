package com.tutorlink.service;

import com.tutorlink.business.interfaces.UserBusinessInterface;
import com.tutorlink.model.Usuario;
import com.tutorlink.service.interfaces.UserServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserServiceInterface {

    private final UserBusinessInterface userBusiness;

    public UserService(UserBusinessInterface userBusiness) {
        this.userBusiness = userBusiness;
    }

    @Override
    @Transactional
    public Usuario registrarAlumno(String correo, String contrasenaHash, String nombre, String apellidos, String matricula, Long idPlanEducativo, Long idTutorAsignado) {
        return userBusiness.registrarAlumno(correo, contrasenaHash, nombre, apellidos, matricula, idPlanEducativo, idTutorAsignado);
    }

    @Override
    @Transactional
    public Usuario registrarTutor(String correo, String contrasenaHash, String nombre, String apellidos, String telefono, Long idProgramaEducativo) {
        return userBusiness.registrarTutor(correo, contrasenaHash, nombre, apellidos, telefono, idProgramaEducativo);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario login(String correo, String contrasenaRaw) {
        return userBusiness.login(correo, contrasenaRaw);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario consultarPerfil(Long idUsuario) {
        return userBusiness.consultarPerfil(idUsuario);
    }

    @Override
    @Transactional
    public Usuario actualizarFotoPerfil(Long idUsuario, String nuevaUrlFoto) {
        return userBusiness.actualizarFotoPerfil(idUsuario, nuevaUrlFoto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTutoradosDeTutor(Long idTutor) {
        return userBusiness.obtenerTutoradosDeTutor(idTutor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarTutorados(Long idTutor, String query) {
        return userBusiness.buscarTutorados(idTutor, query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> filtrarTutorados(Long idTutor, Long idPlanEducativo, Integer semestre) {
        return userBusiness.filtrarTutorados(idTutor, idPlanEducativo, semestre);
    }
}
