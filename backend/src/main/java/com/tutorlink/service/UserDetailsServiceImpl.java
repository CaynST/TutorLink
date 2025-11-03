package com.tutorlink.service;

import com.tutorlink.model.Usuario;
import com.tutorlink.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        Collection<? extends GrantedAuthority> authorities = buildAuthorities(usuario);

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                authorities
        );
    }

    private Collection<? extends GrantedAuthority> buildAuthorities(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getNombreRol() == null) {
            return List.of();
        }
        String roleName = usuario.getRol().getNombreRol().trim().toUpperCase();
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }
}
