package com.tutorlink.controller;

import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Usuario;
import com.tutorlink.model.dto.CreateQuestionRequest;
import com.tutorlink.model.dto.QuestionDTO;
import com.tutorlink.service.JwtService;
import com.tutorlink.service.interfaces.QuestionServiceInterface;
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
 * Controlador para operaciones de preguntas.
 */
@RestController
@RequestMapping
public class QuestionController {

    private final QuestionServiceInterface questionService;
    private final JwtService jwtService;

    public QuestionController(QuestionServiceInterface questionService, JwtService jwtService) {
        this.questionService = questionService;
        this.jwtService = jwtService;
    }

    private Long extraerUid(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) return null;
        Claims c = jwtService.extraerClaims(header.substring(7));
        Number uid = c.get("uid", Number.class);
        return uid != null ? uid.longValue() : null;
    }

    /** Crea una nueva pregunta. */
    @PostMapping("/preguntas")
    @PreAuthorize("hasAnyRole('ESTUDIANTE','TUTOR','SUDO')")
    public ResponseEntity<QuestionDTO> crearPregunta(@Valid @RequestBody CreateQuestionRequest body,
                                                     HttpServletRequest request) {
        Long uid = extraerUid(request);
        Usuario autor = new Usuario();
        autor.setIdUsuario(uid);
        Pregunta p = questionService.crearPregunta(
                autor,
                body.getTitulo(),
                body.getTexto(),
                body.getScopeTipo(),
                body.getIdFacultadScope(),
                body.getIdPlanEducativoScope()
        );
        return ResponseEntity.ok(QuestionDTO.fromEntity(p));
    }

    /** Sugerencias rápidas por texto. Público. */
    @GetMapping("/preguntas/sugerencias")
    public ResponseEntity<List<QuestionDTO>> sugerencias(@RequestParam("q") String q) {
        List<Pregunta> lista = questionService.sugerirPreguntas(q);
        return ResponseEntity.ok(lista.stream().map(QuestionDTO::fromEntity).collect(Collectors.toList()));
    }

    /** Búsqueda de preguntas con filtros de alcance. Público. */
    @GetMapping("/preguntas/buscar")
    public ResponseEntity<List<QuestionDTO>> buscar(@RequestParam(value = "q", required = false) String q,
                                                    @RequestParam(value = "scope", required = false) String scope,
                                                    @RequestParam(value = "idFacultad", required = false) Long idFacultad,
                                                    @RequestParam(value = "idPlan", required = false) Long idPlan) {
        List<Pregunta> lista = questionService.buscarPreguntas(q, scope, idFacultad, idPlan);
        return ResponseEntity.ok(lista.stream().map(QuestionDTO::fromEntity).collect(Collectors.toList()));
    }

    /** Listado de pendientes para un tutor (sólo el propio tutor o SUDO). */
    @GetMapping("/tutores/{idTutor}/pendientes")
    @PreAuthorize("hasAnyRole('TUTOR','SUDO')")
    public ResponseEntity<List<QuestionDTO>> pendientesTutor(@PathVariable Long idTutor,
                                                             HttpServletRequest request) {
        Long uid = extraerUid(request);
        if (uid == null) {
            throw new com.tutorlink.exception.UnauthorizedException("Token inválido");
        }
        if (!uid.equals(idTutor)) {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header == null || !header.startsWith("Bearer ")) {
                throw new com.tutorlink.exception.UnauthorizedException("Token inválido");
            }
            io.jsonwebtoken.Claims c = jwtService.extraerClaims(header.substring(7));
            String rol = c.get("rol", String.class);
            if (rol == null || !rol.equalsIgnoreCase("SUDO")) {
                throw new com.tutorlink.exception.UnauthorizedException("No puede ver pendientes de otro tutor");
            }
        }
        List<Pregunta> lista = questionService.listarPendientesParaTutor(idTutor);
        return ResponseEntity.ok(lista.stream().map(QuestionDTO::fromEntity).collect(Collectors.toList()));
    }
}
