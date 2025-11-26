package com.tutorlink.controller;

import com.tutorlink.exception.UnauthorizedException;
import com.tutorlink.model.Respuesta;
import com.tutorlink.model.dto.AnswerDTO;
import com.tutorlink.model.dto.ApiResponse;
import com.tutorlink.service.JwtService;
import com.tutorlink.service.interfaces.AnswerServiceInterface;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para moderación y generación de respuestas.
 */
@RestController
@RequestMapping
public class AnswerController {

    private final AnswerServiceInterface answerService;
    private final JwtService jwtService;

    public AnswerController(AnswerServiceInterface answerService, JwtService jwtService) {
        this.answerService = answerService;
        this.jwtService = jwtService;
    }

    @org.springframework.beans.factory.annotation.Value("${tutorlink.ollama.llm-user-id:1}")
    private Long defaultLlmUserId;

    private Long extraerUid(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) return null;
        Claims c = jwtService.extraerClaims(header.substring(7));
        Number uid = c.get("uid", Number.class);
        return uid != null ? uid.longValue() : null;
    }

    /** Aprueba una respuesta (sólo tutor). */
    @PostMapping("/respuestas/{id}/aprobar")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<AnswerDTO> aprobar(@PathVariable("id") Long idRespuesta,
                                             HttpServletRequest request) {
        Long uid = extraerUid(request);
        if (uid == null) throw new UnauthorizedException("Token inválido");
        Respuesta r = answerService.aprobarRespuesta(idRespuesta, uid);
        return ResponseEntity.ok(AnswerDTO.fromEntity(r));
    }

    /** Rechaza y regenera una respuesta (sólo tutor). */
    @PostMapping("/respuestas/{id}/rechazar")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<AnswerDTO> rechazarYRegenerar(@PathVariable("id") Long idRespuesta,
                                                        HttpServletRequest request) {
        Long uid = extraerUid(request);
        if (uid == null) throw new UnauthorizedException("Token inválido");
        Respuesta r = answerService.rechazarYRegenerarRespuesta(idRespuesta, uid);
        return ResponseEntity.ok(AnswerDTO.fromEntity(r));
    }

    /**
     * Endpoint para que el frontend solicite la generación de respuesta LLM para una pregunta.
     * - `idUsuarioLLM` puede pasarse como query param; si no se pasa se usa la propiedad
     *   `tutorlink.ollama.llm-user-id` (por defecto 1).
     */
    @PostMapping("/preguntas/{id}/generar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnswerDTO> generarRespuestaLLM(@PathVariable("id") Long idPregunta,
                                                         @RequestParam(value = "idUsuarioLLM", required = false) Long idUsuarioLLM,
                                                         HttpServletRequest request) {
        Long uid = extraerUid(request);
        if (uid == null) throw new UnauthorizedException("Token inválido");
        Long llmIdToUse = idUsuarioLLM != null ? idUsuarioLLM : defaultLlmUserId;
        Respuesta r = answerService.generarRespuestaConOllama(idPregunta, llmIdToUse);
        return ResponseEntity.ok(AnswerDTO.fromEntity(r));
    }
}
