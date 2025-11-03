package com.tutorlink.service;

import com.tutorlink.dto.Student;
import com.tutorlink.dto.LoginDto;
import com.tutorlink.model.Rol;
import com.tutorlink.model.Usuario;
import com.tutorlink.repository.RolRepository;
import com.tutorlink.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public Usuario registrarEstudiante(Student student) {
        // Buscar rol ESTUDIANTE
        Rol rolEstudiante = rolRepository.findByNombreRol("ESTUDIANTE")
                .orElseThrow(() -> new IllegalStateException("Rol ESTUDIANTE no configurado en la base de datos"));

        // Mapear DTO -> entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setRol(rolEstudiante);
        usuario.setCorreo(student.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(student.getContrasena()));
        usuario.setNombre(student.getNombre());
        usuario.setApellidos(student.getApellidos());
        usuario.setMatricula(student.getMatricula());
        usuario.setTelefono(student.getTelefono());
        usuario.setCorreoAlternativo(student.getCorreoAlternativo());
        usuario.setCiudad(student.getCiudad());
        usuario.setPais(student.getPais());
        usuario.setFotoPerfilUrl(student.getFotoPerfilUrl());

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getCorreo(),
                        loginDto.getContrasena()
                )
        );

        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Credenciales inválidas");
        }

    Usuario usuario = usuarioRepository.findByCorreo(loginDto.getCorreo())
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado tras autenticar"));

        return jwtService.generateToken(usuario.getCorreo());
    }
}
