package com.tutorlink.controller;

import com.tutorlink.dto.Student;
import com.tutorlink.dto.LoginDto;
import com.tutorlink.model.Usuario;
import com.tutorlink.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@Valid @RequestBody Student student) {
        Usuario saved = authService.registrarEstudiante(student);
        // No exponer el hash en la respuesta
        saved.setContrasena(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseEntity.ok(java.util.Map.of("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<Object> me(Authentication authentication) {
        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return ResponseEntity.ok(java.util.Map.of(
                "username", authentication.getName(),
                "authorities", authorities
        ));
    }
}
