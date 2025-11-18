package com.tutorlink.service;

import com.tutorlink.business.interfaces.AnswerBusinessInterface;
import com.tutorlink.model.Respuesta;
import com.tutorlink.service.interfaces.AnswerServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService implements AnswerServiceInterface {

    private final AnswerBusinessInterface answerBusiness;

    public AnswerService(AnswerBusinessInterface answerBusiness) {
        this.answerBusiness = answerBusiness;
    }

    @Override
    @Transactional
    public Respuesta generarRespuestaConOllama(Long idPregunta, Long idUsuarioLLM) {
        return answerBusiness.generarRespuestaConOllama(idPregunta, idUsuarioLLM);
    }

    @Override
    @Transactional
    public Respuesta aprobarRespuesta(Long idRespuesta, Long idTutorAprobador) {
        return answerBusiness.aprobarRespuesta(idRespuesta, idTutorAprobador);
    }

    @Override
    @Transactional
    public Respuesta rechazarYRegenerarRespuesta(Long idRespuesta, Long idTutorRevisor) {
        return answerBusiness.rechazarYRegenerarRespuesta(idRespuesta, idTutorRevisor);
    }
}
