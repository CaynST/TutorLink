package com.tutorlink.controller;

import com.tutorlink.model.Usuario;
import com.tutorlink.model.dto.LoginRequest;
import com.tutorlink.model.dto.LoginResponse;
import com.tutorlink.model.dto.UserDTO;
import com.tutorlink.service.interfaces.AuthServiceInterface;
import com.tutorlink.service.interfaces.UserServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceInterface authService;
    private final UserServiceInterface userService;

    public AuthController(AuthServiceInterface authService,
                          UserServiceInterface userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * Inicia sesión y retorna un JWT junto con la información básica del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Autenticar para obtener datos del usuario (DTO)
        Usuario usuario = userService.login(request.getCorreo(), request.getContrasena());
        // Generar token
        String token = authService.iniciarSesion(request.getCorreo(), request.getContrasena());
        return ResponseEntity.ok(new LoginResponse(token, UserDTO.fromEntity(usuario)));
    }
}
