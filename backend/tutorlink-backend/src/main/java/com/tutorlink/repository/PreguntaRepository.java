package com.tutorlink.repository;

import com.tutorlink.model.Pregunta;
import com.tutorlink.model.Usuario;
import com.tutorlink.model.Facultad;
import com.tutorlink.model.PlanEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    // Buscar preguntas por autor (estudiante)
    List<Pregunta> findByAutor(Usuario autor);

    // Buscar preguntas por estado
    List<Pregunta> findByEstado(String estado);

    // Buscar preguntas pendientes de revisión (asignadas a un tutor específico)
    List<Pregunta> findByRevisorAndEstado(Usuario revisor, String estado);

    // Buscar preguntas por alcance (Facultad)
    List<Pregunta> findByScopeTipoAndFacultadScope(String scopeTipo, Facultad facultadScope);

    // Buscar preguntas por alcance (Plan)
    List<Pregunta> findByScopeTipoAndPlanEducativoScope(String scopeTipo, PlanEducativo planEducativoScope);

    // Buscar preguntas por alcance GENERAL
    List<Pregunta> findByScopeTipo(String scopeTipo);

    // Búsqueda de preguntas por título o texto (para RF-07)
    @Query("SELECT p FROM Pregunta p WHERE p.titulo LIKE %:query% OR p.texto LIKE %:query%")
    List<Pregunta> findByTituloContainingIgnoreCaseOrTextoContainingIgnoreCase(@Param("query") String query);

    // Buscar preguntas publicadas (para mostrar en el feed)
    // Usar findByEstado("PUBLICADA")

    // Buscar preguntas por autor y estado (para RF-09)
    List<Pregunta> findByAutorAndEstado(Usuario autor, String estado);
}