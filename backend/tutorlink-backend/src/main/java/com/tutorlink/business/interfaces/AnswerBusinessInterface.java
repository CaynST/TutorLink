package com.tutorlink.business.interfaces;

import com.tutorlink.model.Respuesta;

/**
 * Reglas de negocio para respuestas del sistema (generadas por LLM y revisadas por tutor).
 */
public interface AnswerBusinessInterface {

    /**
     * Genera una respuesta usando Ollama para una pregunta dada.
     * No implementa la llamada HTTP aquí; sólo define los pasos y persiste el resultado como pendiente de revisión.
     */
    Respuesta generarRespuestaConOllama(Long idPregunta, Long idUsuarioLLM);

    /** Aprueba una respuesta (marca la respuesta como PUBLICADA y la pregunta como PUBLICADA). */
    Respuesta aprobarRespuesta(Long idRespuesta, Long idTutorAprobador);

    /** Rechaza una respuesta y solicita/genera una nueva versión vía LLM, dejando la nueva como PENDIENTE_REVISION. */
    Respuesta rechazarYRegenerarRespuesta(Long idRespuesta, Long idTutorRevisor);
}
