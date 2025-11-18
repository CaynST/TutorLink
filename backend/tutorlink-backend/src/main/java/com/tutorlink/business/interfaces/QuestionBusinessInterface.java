package com.tutorlink.business.interfaces;

import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Usuario;

import java.util.List;

/**
 * Reglas de negocio para preguntas (creación, búsqueda/sugerencias, listados de revisión).
 */
public interface QuestionBusinessInterface {

    /**
     * Crea una nueva pregunta validando el alcance (GENERAL, FACULTAD, PLAN) y el autor.
     * Si el alcance es PLAN, valida que el autor pertenezca a dicho plan.
     */
    Pregunta crearPregunta(Usuario autor,
                           String titulo,
                           String texto,
                           String scopeTipo,
                           Long idFacultadScope,
                           Long idPlanEducativoScope);

    /** Sugerencias de preguntas por similitud básica en título/texto. */
    List<Pregunta> sugerirPreguntas(String textoLibre);

    /** Búsqueda de preguntas por texto y filtros de alcance. */
    List<Pregunta> buscarPreguntas(String query,
                                   String scopeTipo,
                                   Long idFacultadScope,
                                   Long idPlanEducativoScope);

    /** Lista preguntas pendientes relevantes para un tutor (de sus tutorados y/o alcance). */
    List<Pregunta> listarPendientesParaTutor(Long idTutor);
}
