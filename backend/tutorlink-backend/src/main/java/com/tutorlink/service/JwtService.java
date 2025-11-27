package com.tutorlink.service;

import com.tutorlink.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret:ZmFrZVNlY3JldEtleUZvclR1dG9ybGluazEyMzQ=}")
    private String secretoBase64;

    @Value("${security.jwt.expirationSeconds:3600}")
    private long expiracionSegundos;

    private SecretKey obtenerClave() {
        byte[] keyBytes = Decoders.BASE64.decode(secretoBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        String rol = usuario.getRol() != null ? usuario.getRol().getNombreRol() : "ESTUDIANTE";
        claims.put("rol", rol);
        claims.put("uid", usuario.getIdUsuario());

        Instant ahora = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getCorreo())
                .setIssuedAt(Date.from(ahora))
                .setExpiration(Date.from(ahora.plusSeconds(expiracionSegundos)))
                .signWith(obtenerClave(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean esTokenValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerCorreo(String token) {
        return extraerClaims(token).getSubject();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(obtenerClave())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String refrescarToken(String token) {
        Claims claims = extraerClaims(token);
        Usuario u = new Usuario();
        u.setCorreo(claims.getSubject());
        u.setIdUsuario(claims.get("uid", Number.class).longValue());
        // El rol no es necesario para regenerar, pero lo reconstruimos mínimo
        return generarToken(u);
    }

    // Método de conveniencia: en un proyecto real, consultaría UserRepository
    public Usuario obtenerUsuarioDesdeToken(String token) {
        Claims claims = extraerClaims(token);
        Usuario u = new Usuario();
        u.setCorreo(claims.getSubject());
        Number uid = claims.get("uid", Number.class);
        if (uid != null) u.setIdUsuario(uid.longValue());
        return u;
    }
}
