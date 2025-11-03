package com.tutorlink.service;

import com.tutorlink.dto.PreguntaRequestDto;
import com.tutorlink.dto.PreguntaResponseDto;
import com.tutorlink.dto.PreguntaSummaryDto;
import com.tutorlink.model.Facultad;
import com.tutorlink.model.PlanEducativo;
import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Usuario;
import com.tutorlink.model.enums.EstadoPregunta;
import com.tutorlink.model.enums.ScopePregunta;
import com.tutorlink.repository.FacultadRepository;
import com.tutorlink.repository.PlanEducativoRepository;
import com.tutorlink.repository.PreguntaRepository;
import com.tutorlink.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.domain.Specification;
import com.tutorlink.repository.PreguntaSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FacultadRepository facultadRepository;
    private final PlanEducativoRepository planEducativoRepository;

    @Transactional
    public PreguntaResponseDto crearPregunta(PreguntaRequestDto dto, String correoAutor) {
        Usuario autor = usuarioRepository.findByCorreo(correoAutor)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));

        // Convertir y validar scope_tipo a enum
        ScopePregunta scope = ScopePregunta.valueOf(dto.getScope_tipo().trim().toUpperCase());
        switch (scope) {
            case FACULTAD -> {
                if (dto.getId_facultad_scope() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id_facultad_scope es requerido cuando scope_tipo es FACULTAD");
                }
            }
            case PLAN -> {
                if (dto.getId_plan_educativo_scope() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id_plan_educativo_scope es requerido cuando scope_tipo es PLAN");
                }
            }
            case GENERAL -> {
                // no-op
            }
        }

        Pregunta pregunta = new Pregunta();
        pregunta.setAutor(autor);
    pregunta.setTitulo(dto.getTitulo());
    pregunta.setTexto(dto.getTexto());
    pregunta.setScopeTipo(scope);
    EstadoPregunta estado = dto.getEstado() != null
        ? EstadoPregunta.valueOf(dto.getEstado().trim().toUpperCase())
        : EstadoPregunta.PENDIENTE;
    pregunta.setEstado(estado);
        pregunta.setFechaCreacion(Instant.now());

        // Alcance opcional
        if (dto.getId_facultad_scope() != null) {
            int id = Math.toIntExact(dto.getId_facultad_scope());
            Facultad facultad = facultadRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Facultad no encontrada"));
            pregunta.setFacultadScope(facultad);
        }

        if (dto.getId_plan_educativo_scope() != null) {
            int id = Math.toIntExact(dto.getId_plan_educativo_scope());
            PlanEducativo plan = planEducativoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Plan educativo no encontrado"));
            pregunta.setPlanEducativoScope(plan);
        }

        Pregunta guardada = preguntaRepository.save(pregunta);
        return toResponseDto(guardada);
    }

    private PreguntaResponseDto toResponseDto(Pregunta p) {
        PreguntaResponseDto.AutorDto autorDto = new PreguntaResponseDto.AutorDto(
                p.getAutor().getNombre(),
                p.getAutor().getApellidos()
        );
    return new PreguntaResponseDto(
                p.getIdPregunta() != null ? p.getIdPregunta().longValue() : null,
                p.getTitulo(),
                p.getTexto(),
        p.getEstado() != null ? p.getEstado().name() : null,
                p.getFechaCreacion(),
        p.getScopeTipo() != null ? p.getScopeTipo().name() : null,
                autorDto
        );
    }

    @Transactional(readOnly = true)
    public Page<PreguntaSummaryDto> listarPreguntas(@NonNull Pageable pageable,
                                                    String estado,
                                                    String scopeTipo,
                                                    Long idFacultadScope,
                                                    Long idPlanEducativoScope) {
        Specification<com.tutorlink.model.Pregunta> spec = PreguntaSpecification.build(
                estado, scopeTipo, idFacultadScope, idPlanEducativoScope
        );
        return preguntaRepository.findAll(spec, pageable).map(this::toSummaryDto);
    }

    private PreguntaSummaryDto toSummaryDto(Pregunta p) {
        PreguntaSummaryDto.AutorDto autorDto = new PreguntaSummaryDto.AutorDto(
                p.getAutor().getNombre(),
                p.getAutor().getApellidos()
        );
    return new PreguntaSummaryDto(
                p.getIdPregunta() != null ? p.getIdPregunta().longValue() : null,
                p.getTitulo(),
        p.getEstado() != null ? p.getEstado().name() : null,
                p.getFechaCreacion(),
                autorDto
        );
    }

    @Transactional(readOnly = true)
    public Page<PreguntaSummaryDto> getPreguntasPendientes(@NonNull Pageable pageable) {
        Specification<Pregunta> spec = PreguntaSpecification.hasEstado("PENDIENTE");
        return preguntaRepository.findAll(spec, pageable).map(this::toSummaryDto);
    }
}
