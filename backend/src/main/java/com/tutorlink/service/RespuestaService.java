package com.tutorlink.service;

import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Respuesta;
import com.tutorlink.model.enums.EstadoPregunta;
import com.tutorlink.model.Usuario;
import com.tutorlink.repository.UsuarioRepository;
import com.tutorlink.repository.PreguntaRepository;
import com.tutorlink.repository.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Respuesta aprobarRespuesta(Long idRespuesta, String correoTutor) {
        Usuario tutor = usuarioRepository.findByCorreo(correoTutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tutor no encontrado"));
        int id = Math.toIntExact(idRespuesta);
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta no encontrada"));

        Pregunta pregunta = respuesta.getPregunta();
        if (pregunta == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La respuesta no tiene pregunta asociada");
        }

        // Cambiar estados
        respuesta.setEstadoRespuesta("PUBLICADA");
        pregunta.setEstado(EstadoPregunta.PUBLICADA);
        // Asignar revisor/aprobador
        pregunta.setRevisor(tutor);
        respuesta.setAprobador(tutor);

        // Persistir
        preguntaRepository.save(pregunta);
        return respuestaRepository.save(respuesta);
    }

    @Transactional
    public Respuesta rechazarRespuesta(Long idRespuesta, String correoTutor) {
        Usuario tutor = usuarioRepository.findByCorreo(correoTutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tutor no encontrado"));
        int id = Math.toIntExact(idRespuesta);
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta no encontrada"));

        // Cambiar estado de respuesta a RECHAZADA
        respuesta.setEstadoRespuesta("RECHAZADA");
        // Asignar revisor
        if (respuesta.getPregunta() != null) {
            Pregunta pregunta = respuesta.getPregunta();
            pregunta.setRevisor(tutor);
            preguntaRepository.save(pregunta);
        }
        // (Futuro) notificar al LLM para regenerar

        return respuestaRepository.save(respuesta);
    }
}
