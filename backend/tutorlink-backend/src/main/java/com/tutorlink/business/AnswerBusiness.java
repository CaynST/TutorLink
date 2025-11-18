package com.tutorlink.business;

import com.tutorlink.business.interfaces.AnswerBusinessInterface;
import com.tutorlink.exception.ResourceNotFoundException;
import com.tutorlink.exception.UnauthorizedException;
import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Respuesta;
import com.tutorlink.model.Usuario;
import com.tutorlink.repository.DetalleEstudianteRepository;
import com.tutorlink.repository.PreguntaRepository;
import com.tutorlink.repository.RespuestaRepository;
import com.tutorlink.repository.UsuarioRepository;
import com.tutorlink.service.OllamaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnswerBusiness implements AnswerBusinessInterface {
    private final RespuestaRepository respuestaRepository;
    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DetalleEstudianteRepository detalleEstudianteRepository;
    private final OllamaService ollamaService;

    public AnswerBusiness(RespuestaRepository respuestaRepository,
                          PreguntaRepository preguntaRepository,
                          UsuarioRepository usuarioRepository,
                          DetalleEstudianteRepository detalleEstudianteRepository,
                          OllamaService ollamaService) {
        this.respuestaRepository = respuestaRepository;
        this.preguntaRepository = preguntaRepository;
        this.usuarioRepository = usuarioRepository;
        this.detalleEstudianteRepository = detalleEstudianteRepository;
        this.ollamaService = ollamaService;
    }

    @Override
    public Respuesta generarRespuestaConOllama(Long idPregunta, Long idUsuarioLLM) {
        Pregunta pregunta = preguntaRepository.findById(idPregunta)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada"));

        Usuario llm = usuarioRepository.findById(idUsuarioLLM)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario LLM no encontrado"));

        // 1) Preparar prompt según alcance y contexto
        String prompt = prepararPromptParaOllama(pregunta);
        // 2) Invocar Ollama y obtener el contenido (stub)
    String contenidoGenerado = ollamaService.generarRespuesta(prompt, null);
        // 3) Guardar respuesta como PENDIENTE_REVISION
        Respuesta r = new Respuesta(pregunta, llm, contenidoGenerado);
        r.setEstadoRespuesta("PENDIENTE_REVISION");

        Respuesta guardada = respuestaRepository.save(r);

        // 4) Notificar al tutor (stub)
        notificarTutorDeNuevaRespuesta(pregunta);

        return guardada;
    }

    @Override
    public Respuesta aprobarRespuesta(Long idRespuesta, Long idTutorAprobador) {
        Respuesta respuesta = respuestaRepository.findById(idRespuesta)
                .orElseThrow(() -> new ResourceNotFoundException("Respuesta no encontrada"));
        Usuario tutor = usuarioRepository.findById(idTutorAprobador)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado"));

        // Validar que el tutor sea el asignado al estudiante autor de la pregunta
        var detalle = detalleEstudianteRepository.findByIdUsuario(respuesta.getPregunta().getAutor().getIdUsuario());
        if (detalle == null || !detalle.getTutorAsignado().getIdUsuario().equals(tutor.getIdUsuario())) {
            throw new UnauthorizedException("El tutor no puede aprobar esta respuesta");
        }

        respuesta.setAprobador(tutor);
        respuesta.setEstadoRespuesta("PUBLICADA");
        respuesta = respuestaRepository.save(respuesta);

        // Marcar la pregunta como PUBLICADA
        Pregunta p = respuesta.getPregunta();
        p.setEstado("PUBLICADA");
        preguntaRepository.save(p);

        // Notificar al alumno
        notificarAlumnoPreguntaContestada(p);

        return respuesta;
    }

    @Override
    public Respuesta rechazarYRegenerarRespuesta(Long idRespuesta, Long idTutorRevisor) {
        Respuesta respuesta = respuestaRepository.findById(idRespuesta)
                .orElseThrow(() -> new ResourceNotFoundException("Respuesta no encontrada"));
        Usuario tutor = usuarioRepository.findById(idTutorRevisor)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado"));

        var detalle = detalleEstudianteRepository.findByIdUsuario(respuesta.getPregunta().getAutor().getIdUsuario());
        if (detalle == null || !detalle.getTutorAsignado().getIdUsuario().equals(tutor.getIdUsuario())) {
            throw new UnauthorizedException("El tutor no puede rechazar esta respuesta");
        }

        // Generar una nueva versión de respuesta
        Pregunta p = respuesta.getPregunta();
        String prompt = prepararPromptParaOllama(p) + "\nObservación del tutor: por favor, mejora la respuesta.";
    String contenidoGenerado = ollamaService.generarRespuesta(prompt, null);

        Respuesta nueva = new Respuesta();
        nueva.setPregunta(p);
        nueva.setAutor(respuesta.getAutor()); // autor sigue siendo LLM
        nueva.setVersionRespuesta(respuesta.getVersionRespuesta() + 1);
        nueva.setContenido(contenidoGenerado);
        nueva.setEstadoRespuesta("PENDIENTE_REVISION");

        Respuesta guardada = respuestaRepository.save(nueva);

        // Notificar al tutor que hay nueva versión pendiente
        notificarTutorDeNuevaRespuesta(p);

        return guardada;
    }

    // --- Métodos auxiliares (stubs) ---

    private String prepararPromptParaOllama(Pregunta p) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un tutor académico. Responde en español con claridad y precisión.\n");
        sb.append("Pregunta: ").append(p.getTitulo()).append("\n");
        sb.append("Detalle: ").append(p.getTexto()).append("\n");
        sb.append("Alcance: ").append(p.getScopeTipo()).append("\n");
        if (p.getScopeTipo().equalsIgnoreCase("FACULTAD") && p.getFacultadScope() != null) {
            sb.append("Facultad ID: ").append(p.getFacultadScope().getIdFacultad()).append("\n");
        }
        if (p.getScopeTipo().equalsIgnoreCase("PLAN") && p.getPlanEducativoScope() != null) {
            sb.append("Plan ID: ").append(p.getPlanEducativoScope().getIdPlan()).append("\n");
        }
        return sb.toString();
    }

    // Se delega la invocación real a OllamaService

    private void notificarTutorDeNuevaRespuesta(Pregunta p) {
        // TODO: Integrar notificaciones (email/websocket). RF-10
    }

    private void notificarAlumnoPreguntaContestada(Pregunta p) {
        // TODO: Integrar notificaciones al alumno. RF-09
    }
}
