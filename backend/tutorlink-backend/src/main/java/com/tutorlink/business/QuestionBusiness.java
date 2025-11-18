package com.tutorlink.business;

import com.tutorlink.business.interfaces.QuestionBusinessInterface;
import com.tutorlink.exception.ResourceNotFoundException;
import com.tutorlink.exception.UnauthorizedException;
import com.tutorlink.exception.ValidationException;
import com.tutorlink.model.*;
import com.tutorlink.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionBusiness implements QuestionBusinessInterface {

    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DetalleEstudianteRepository detalleEstudianteRepository;
    private final FacultadRepository facultadRepository;
    private final PlanEducativoRepository planEducativoRepository;

    public QuestionBusiness(PreguntaRepository preguntaRepository,
                            UsuarioRepository usuarioRepository,
                            DetalleEstudianteRepository detalleEstudianteRepository,
                            FacultadRepository facultadRepository,
                            PlanEducativoRepository planEducativoRepository) {
        this.preguntaRepository = preguntaRepository;
        this.usuarioRepository = usuarioRepository;
        this.detalleEstudianteRepository = detalleEstudianteRepository;
        this.facultadRepository = facultadRepository;
        this.planEducativoRepository = planEducativoRepository;
    }

    private void validarCamposPregunta(String titulo, String texto) {
        if (titulo == null || titulo.isBlank()) {
            throw new ValidationException("El título de la pregunta es obligatorio");
        }
        if (texto == null || texto.isBlank()) {
            throw new ValidationException("El texto de la pregunta es obligatorio");
        }
    }

    private void validarScope(String scopeTipo) {
        if (scopeTipo == null) throw new ValidationException("El alcance es obligatorio");
        String s = scopeTipo.toUpperCase(Locale.ROOT);
        if (!List.of("GENERAL", "FACULTAD", "PLAN").contains(s)) {
            throw new ValidationException("Alcance inválido: " + scopeTipo);
        }
    }

    @Override
    public Pregunta crearPregunta(Usuario autor, String titulo, String texto, String scopeTipo, Long idFacultadScope, Long idPlanEducativoScope) {
        validarCamposPregunta(titulo, texto);
        validarScope(scopeTipo);

        // Verificar que el autor exista
        Usuario autorDB = usuarioRepository.findById(autor.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

        Pregunta p = new Pregunta(autorDB, titulo, texto, scopeTipo.toUpperCase(Locale.ROOT));

        switch (p.getScopeTipo()) {
            case "GENERAL":
                break;
            case "FACULTAD":
                if (idFacultadScope == null) throw new ValidationException("Debe indicar la facultad para el alcance FACULTAD");
                Facultad fac = facultadRepository.findById(idFacultadScope)
                        .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada"));
                p.setFacultadScope(fac);
                break;
            case "PLAN":
                if (idPlanEducativoScope == null) throw new ValidationException("Debe indicar el plan para el alcance PLAN");
                PlanEducativo plan = planEducativoRepository.findById(idPlanEducativoScope)
                        .orElseThrow(() -> new ResourceNotFoundException("Plan educativo no encontrado"));
                // Validar que el autor pertenece a ese plan
                var detalle = detalleEstudianteRepository.findByIdUsuario(autorDB.getIdUsuario());
                if (detalle == null || detalle.getPlanEducativo() == null || !Objects.equals(detalle.getPlanEducativo().getIdPlan(), plan.getIdPlan())) {
                    throw new UnauthorizedException("El autor no pertenece al plan educativo indicado");
                }
                p.setPlanEducativoScope(plan);
                break;
            default:
                throw new ValidationException("Alcance inválido");
        }

        // Estado inicial PENDIENTE
        p.setEstado("PENDIENTE");
        return preguntaRepository.save(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> sugerirPreguntas(String textoLibre) {
        if (textoLibre == null || textoLibre.isBlank()) return List.of();
        return preguntaRepository.findByTituloContainingIgnoreCaseOrTextoContainingIgnoreCase(textoLibre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> buscarPreguntas(String query, String scopeTipo, Long idFacultadScope, Long idPlanEducativoScope) {
        String q = (query == null) ? "" : query;

        List<Pregunta> base = preguntaRepository.findByTituloContainingIgnoreCaseOrTextoContainingIgnoreCase(q);

        if (scopeTipo == null || scopeTipo.isBlank()) return base;
        String s = scopeTipo.toUpperCase(Locale.ROOT);
        return base.stream().filter(p -> {
            if (!p.getScopeTipo().equalsIgnoreCase(s)) return false;
            if (s.equals("FACULTAD") && idFacultadScope != null) {
                return p.getFacultadScope() != null && idFacultadScope.equals(p.getFacultadScope().getIdFacultad());
            }
            if (s.equals("PLAN") && idPlanEducativoScope != null) {
                return p.getPlanEducativoScope() != null && idPlanEducativoScope.equals(p.getPlanEducativoScope().getIdPlan());
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> listarPendientesParaTutor(Long idTutor) {
        Usuario tutor = usuarioRepository.findById(idTutor)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado"));

        // Preguntas pendientes del sistema
        List<Pregunta> pendientes = new ArrayList<>();
        try {
            pendientes = preguntaRepository.findByEstado("PENDIENTE");
        } catch (Exception ignored) {
            // En caso de que el repo actual no tenga el método correcto aún, evitamos romper.
        }

        // Filtrar sólo las de sus tutorados
        var detalles = detalleEstudianteRepository.findByTutorAsignado(tutor);
        var idsTutorados = detalles.stream().map(d -> d.getUsuario().getIdUsuario()).collect(Collectors.toSet());

        return pendientes.stream()
                .filter(p -> p.getAutor() != null && idsTutorados.contains(p.getAutor().getIdUsuario()))
                .collect(Collectors.toList());
    }
}
