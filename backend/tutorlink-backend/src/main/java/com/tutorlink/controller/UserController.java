package com.tutorlink.controller;

import com.tutorlink.exception.UnauthorizedException;
import com.tutorlink.model.Usuario;
import com.tutorlink.model.dto.ApiResponse;
import com.tutorlink.model.dto.UpdateProfileRequest;
import com.tutorlink.model.dto.UserDTO;
import com.tutorlink.service.JwtService;
import com.tutorlink.service.interfaces.UserServiceInterface;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para operaciones de usuarios.
 */
@RestController
@RequestMapping("/usuarios")
public class UserController {

    private final UserServiceInterface userService;
    private final JwtService jwtService;

    public UserController(UserServiceInterface userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    private Long extraerUid(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) return null;
        Claims c = jwtService.extraerClaims(header.substring(7));
        Number uid = c.get("uid", Number.class);
        return uid != null ? uid.longValue() : null;
    }

    /** Obtiene el perfil del usuario por id. */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> obtenerPerfil(@PathVariable("id") Long id) {
        Usuario u = userService.consultarPerfil(id);
        return ResponseEntity.ok(UserDTO.fromEntity(u));
    }

    /** Actualiza la foto del perfil, sólo el propio usuario puede hacerlo. */
    @PutMapping("/{id}/foto")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> actualizarFoto(@PathVariable("id") Long id,
                                                  @Valid @RequestBody UpdateProfileRequest body,
                                                  HttpServletRequest request) {
        Long uid = extraerUid(request);
        if (uid == null || !uid.equals(id)) {
            throw new UnauthorizedException("No puede actualizar la foto de otro usuario");
        }
        Usuario actualizado = userService.actualizarFotoPerfil(id, body.getFotoPerfilUrl());
        return ResponseEntity.ok(UserDTO.fromEntity(actualizado));
    }

    /** Lista de tutorados para un tutor. */
    @GetMapping("/tutores/{idTutor}/tutorados")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<List<UserDTO>> obtenerTutorados(@PathVariable Long idTutor,
                                                          HttpServletRequest request) {
        Long uid = extraerUid(request);
        if (uid == null || !uid.equals(idTutor)) {
            throw new UnauthorizedException("No puede consultar tutorados de otro tutor");
        }
        List<Usuario> lista = userService.obtenerTutoradosDeTutor(idTutor);
        return ResponseEntity.ok(lista.stream().map(UserDTO::fromEntity).collect(Collectors.toList()));
    }

    /** Búsqueda de tutorados por texto libre. */
    @GetMapping("/tutores/{idTutor}/tutorados/buscar")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<List<UserDTO>> buscarTutorados(@PathVariable Long idTutor,
                                                         @RequestParam("q") String q,
                                                         HttpServletRequest request) {
        Long uid = extraerUid(request);
        if (uid == null || !uid.equals(idTutor)) {
            throw new UnauthorizedException("No puede buscar tutorados de otro tutor");
        }
        List<Usuario> lista = userService.buscarTutorados(idTutor, q);
        return ResponseEntity.ok(lista.stream().map(UserDTO::fromEntity).collect(Collectors.toList()));
    }

    /** Filtrado de tutorados por plan/semestre. */
    @GetMapping("/tutores/{idTutor}/tutorados/filtrar")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<List<UserDTO>> filtrarTutorados(@PathVariable Long idTutor,
                                                          @RequestParam(value = "idPlan", required = false) Long idPlan,
                                                          @RequestParam(value = "semestre", required = false) Integer semestre,
                                                          HttpServletRequest request) {
        Long uid = extraerUid(request);
        if (uid == null || !uid.equals(idTutor)) {
            throw new UnauthorizedException("No puede filtrar tutorados de otro tutor");
        }
        List<Usuario> lista = userService.filtrarTutorados(idTutor, idPlan, semestre);
        return ResponseEntity.ok(lista.stream().map(UserDTO::fromEntity).collect(Collectors.toList()));
    }
}
