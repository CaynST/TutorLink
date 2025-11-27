package com.tutorlink.config;

import com.tutorlink.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("JwtFilter - request {} Authorization header present: {}", request.getRequestURI(), authHeader != null);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        boolean valido = jwtService.esTokenValido(token);
        log.debug("JwtFilter - token present, esTokenValido={}", valido);
        if (!valido) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.extraerClaims(token);
        String correo = claims.getSubject();
        String rol = claims.get("rol", String.class);
        List<GrantedAuthority> authorities = rol != null
                ? List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                : Collections.emptyList();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                correo, null, authorities
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("JwtFilter - autenticación creada user={}, rol={}, authorities={}", correo, rol, authorities);

        filterChain.doFilter(request, response);
    }
}
