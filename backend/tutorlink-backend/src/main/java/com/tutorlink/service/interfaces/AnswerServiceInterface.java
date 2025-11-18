package com.tutorlink.service.interfaces;

import com.tutorlink.model.Respuesta;

public interface AnswerServiceInterface {
    Respuesta generarRespuestaConOllama(Long idPregunta, Long idUsuarioLLM);
    Respuesta aprobarRespuesta(Long idRespuesta, Long idTutorAprobador);
    Respuesta rechazarYRegenerarRespuesta(Long idRespuesta, Long idTutorRevisor);
}
