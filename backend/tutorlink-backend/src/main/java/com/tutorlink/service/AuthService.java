package com.tutorlink.service;

import com.tutorlink.business.interfaces.UserBusinessInterface;
import com.tutorlink.model.Usuario;
import com.tutorlink.service.interfaces.AuthServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements AuthServiceInterface {

    private final UserBusinessInterface userBusiness;
    private final JwtService jwtService;

    public AuthService(UserBusinessInterface userBusiness, JwtService jwtService) {
        this.userBusiness = userBusiness;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(readOnly = true)
    public String iniciarSesion(String correo, String contrasenaRaw) {
        Usuario usuario = userBusiness.login(correo, contrasenaRaw);
        return jwtService.generarToken(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario validarToken(String token) {
        String correo = jwtService.extraerCorreo(token);
        // Reutilizamos login con contraseña vacía? Mejor obtener usuario por correo desde business o repo, pero mantenemos simple:
        // Creamos un método auxiliar en JwtService para validar y extraer claims; aquí asumimos usuario con correo válido.
        return jwtService.obtenerUsuarioDesdeToken(token);
    }

    @Override
    public String refrescarToken(String token) {
        return jwtService.refrescarToken(token);
    }
}
