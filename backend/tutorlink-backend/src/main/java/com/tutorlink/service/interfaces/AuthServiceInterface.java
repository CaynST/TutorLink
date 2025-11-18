package com.tutorlink.service.interfaces;

import com.tutorlink.model.Usuario;

public interface AuthServiceInterface {
    /** Autentica credenciales y retorna un JWT. */
    String iniciarSesion(String correo, String contrasenaRaw);

    /** Valida un token y retorna el usuario asociado. */
    Usuario validarToken(String token);

    /** Refresca un token (si aplica política). */
    String refrescarToken(String token);
}
