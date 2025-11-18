package com.tutorlink.repository;

import com.tutorlink.model.Respuesta;
import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    // Buscar respuestas por pregunta
    List<Respuesta> findByPregunta(Pregunta pregunta);

    // Buscar respuestas por autor (LLM)
    List<Respuesta> findByAutor(Usuario autor);

    // Buscar respuestas por estado
    List<Respuesta> findByEstadoRespuesta(String estadoRespuesta);

    // Buscar respuestas pendientes de revisión (asignadas a un tutor específico)
    List<Respuesta> findByAprobadorAndEstadoRespuesta(Usuario aprobador, String estadoRespuesta);

    // Buscar respuestas aprobadas/publicadas para una pregunta específica
    List<Respuesta> findByPreguntaAndEstadoRespuesta(Pregunta pregunta, String estadoRespuesta);
}