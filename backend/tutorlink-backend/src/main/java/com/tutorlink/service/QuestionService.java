package com.tutorlink.service;

import com.tutorlink.business.interfaces.QuestionBusinessInterface;
import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Usuario;
import com.tutorlink.service.interfaces.QuestionServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService implements QuestionServiceInterface {

    private final QuestionBusinessInterface questionBusiness;

    public QuestionService(QuestionBusinessInterface questionBusiness) {
        this.questionBusiness = questionBusiness;
    }

    @Override
    @Transactional
    public Pregunta crearPregunta(Usuario autor, String titulo, String texto, String scopeTipo, Long idFacultadScope, Long idPlanEducativoScope) {
        return questionBusiness.crearPregunta(autor, titulo, texto, scopeTipo, idFacultadScope, idPlanEducativoScope);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> sugerirPreguntas(String textoLibre) {
        return questionBusiness.sugerirPreguntas(textoLibre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> buscarPreguntas(String query, String scopeTipo, Long idFacultadScope, Long idPlanEducativoScope) {
        return questionBusiness.buscarPreguntas(query, scopeTipo, idFacultadScope, idPlanEducativoScope);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> listarPendientesParaTutor(Long idTutor) {
        return questionBusiness.listarPendientesParaTutor(idTutor);
    }
}
