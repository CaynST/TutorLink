package com.tutorlink.service.interfaces;

import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Usuario;

import java.util.List;

public interface QuestionServiceInterface {
    Pregunta crearPregunta(Usuario autor,
                           String titulo,
                           String texto,
                           String scopeTipo,
                           Long idFacultadScope,
                           Long idPlanEducativoScope);

    List<Pregunta> sugerirPreguntas(String textoLibre);

    List<Pregunta> buscarPreguntas(String query,
                                   String scopeTipo,
                                   Long idFacultadScope,
                                   Long idPlanEducativoScope);

    List<Pregunta> listarPendientesParaTutor(Long idTutor);
}
