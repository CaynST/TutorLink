package com.tutorlink.controller;

import com.tutorlink.dto.PreguntaRequestDto;
import com.tutorlink.dto.PreguntaResponseDto;
import com.tutorlink.dto.PreguntaSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import com.tutorlink.service.PreguntaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/preguntas")
@RequiredArgsConstructor
@Validated
public class PreguntaController {

    private final PreguntaService preguntaService;

    @PostMapping
    public ResponseEntity<PreguntaResponseDto> crearPregunta(@Valid @RequestBody PreguntaRequestDto dto,
                                                  Authentication authentication) {
        PreguntaResponseDto creada = preguntaService.crearPregunta(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public ResponseEntity<Page<PreguntaSummaryDto>> listarPreguntas(
        @PageableDefault(sort = "fechaCreacion", direction = Sort.Direction.DESC) @NonNull Pageable pageable,
            @RequestParam(required = false)
            @Pattern(regexp = "PENDIENTE|PUBLICADA|RECHAZADA", flags = Pattern.Flag.CASE_INSENSITIVE)
            String estado,
            @RequestParam(required = false)
            @Pattern(regexp = "GENERAL|FACULTAD|PLAN", flags = Pattern.Flag.CASE_INSENSITIVE)
            String scope_tipo,
            @RequestParam(required = false) Long id_facultad_scope,
            @RequestParam(required = false) Long id_plan_educativo_scope
    ) {
        Page<PreguntaSummaryDto> page = preguntaService.listarPreguntas(
                pageable, estado, scope_tipo, id_facultad_scope, id_plan_educativo_scope
        );
        return ResponseEntity.ok(page);
    }
}
