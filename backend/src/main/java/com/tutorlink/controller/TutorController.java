package com.tutorlink.controller;

import com.tutorlink.dto.PreguntaSummaryDto;
import com.tutorlink.dto.RespuestaEstadoDto;
import com.tutorlink.model.Respuesta;
import com.tutorlink.service.PreguntaService;
import com.tutorlink.service.RespuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/api/tutor")
@RequiredArgsConstructor
public class TutorController {

    private final PreguntaService preguntaService;
    private final RespuestaService respuestaService;

    @GetMapping("/preguntas/pendientes")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<Page<PreguntaSummaryDto>> listarPendientes(
            @PageableDefault(sort = "fechaCreacion", direction = Sort.Direction.DESC) @NonNull Pageable pageable
    ) {
        Page<PreguntaSummaryDto> page = preguntaService.getPreguntasPendientes(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/respuestas/{idRespuesta}/aprobar")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<RespuestaEstadoDto> aprobar(@PathVariable Long idRespuesta, org.springframework.security.core.Authentication authentication) {
        Respuesta actualizada = respuestaService.aprobarRespuesta(idRespuesta, authentication.getName());
        return ResponseEntity.ok(new RespuestaEstadoDto(actualizada.getEstadoRespuesta()));
    }

    @PostMapping("/respuestas/{idRespuesta}/rechazar")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<RespuestaEstadoDto> rechazar(@PathVariable Long idRespuesta, org.springframework.security.core.Authentication authentication) {
        Respuesta actualizada = respuestaService.rechazarRespuesta(idRespuesta, authentication.getName());
        return ResponseEntity.ok(new RespuestaEstadoDto(actualizada.getEstadoRespuesta()));
    }
}
