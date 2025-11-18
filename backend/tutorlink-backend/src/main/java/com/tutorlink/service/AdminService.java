package com.tutorlink.service;

import com.tutorlink.business.interfaces.AdminBusinessInterface;
import com.tutorlink.service.interfaces.AdminServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService implements AdminServiceInterface {

    private final AdminBusinessInterface adminBusiness;

    public AdminService(AdminBusinessInterface adminBusiness) {
        this.adminBusiness = adminBusiness;
    }

    @Override
    @Transactional
    public void aprobarRegistroUsuario(Long idUsuario) {
        adminBusiness.aprobarRegistroUsuario(idUsuario);
    }

    @Override
    @Transactional
    public void asignarTutorAEstudiante(Long idEstudiante, Long idTutor) {
        adminBusiness.asignarTutorAEstudiante(idEstudiante, idTutor);
    }

    @Override
    @Transactional
    public void cambiarEstadoUsuario(Long idUsuario, boolean activo) {
        adminBusiness.cambiarEstadoUsuario(idUsuario, activo);
    }

    @Override
    @Transactional
    public void gestionarPreguntaEliminar(Long idPregunta) {
        adminBusiness.gestionarPreguntaEliminar(idPregunta);
    }
}
