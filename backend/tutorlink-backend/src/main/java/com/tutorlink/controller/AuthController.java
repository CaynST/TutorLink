package com.tutorlink.controller;

import com.tutorlink.model.Usuario;
import com.tutorlink.model.dto.ApiResponse;
import com.tutorlink.model.dto.LoginRequest;
import com.tutorlink.model.dto.LoginResponse;
import com.tutorlink.model.dto.RegisterRequest;
import com.tutorlink.model.dto.UserDTO;
import com.tutorlink.service.interfaces.AuthServiceInterface;
import com.tutorlink.service.interfaces.UserServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceInterface authService;
    private final UserServiceInterface userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthServiceInterface authService,
                          UserServiceInterface userService,
                          PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    /** Registro público de usuarios (alumno o tutor). */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest body) {
        String rol = body.getRol() != null ? body.getRol().trim().toUpperCase() : "";
        // Hashear contraseña (aunque el business actualmente espera el hash o raw, mantener consistencia)
        String contrasenaHash = passwordEncoder.encode(body.getContrasena());

        if ("ESTUDIANTE".equals(rol)) {
            // Validar campos obligatorios para estudiantes
            if (body.getMatricula() == null || body.getMatricula().isBlank() || body.getIdPlanEducativo() == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Faltan campos obligatorios para ESTUDIANTE: matricula, idPlanEducativo"));
            }
            userService.registrarAlumno(body.getCorreo(), contrasenaHash, body.getNombre(), body.getApellidos(), body.getMatricula(), body.getIdPlanEducativo(), body.getIdTutorAsignado());
            return ResponseEntity.ok(new ApiResponse(true, "Registro de estudiante exitoso"));
        } else if ("TUTOR".equals(rol)) {
            userService.registrarTutor(body.getCorreo(), contrasenaHash, body.getNombre(), body.getApellidos(), body.getTelefono(), body.getIdProgramaEducativo());
            return ResponseEntity.ok(new ApiResponse(true, "Registro de tutor exitoso"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Rol inválido. Use ESTUDIANTE o TUTOR"));
        }
    }
}
