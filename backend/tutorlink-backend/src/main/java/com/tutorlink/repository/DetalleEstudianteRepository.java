package com.tutorlink.repository;

import com.tutorlink.model.DetalleEstudiante;
import com.tutorlink.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleEstudianteRepository extends JpaRepository<DetalleEstudiante, Long> {
    // Buscar estudiantes por tutor asignado
    List<DetalleEstudiante> findByTutorAsignado(Usuario tutorAsignado);

    // Buscar por plan educativo (composición por campos del plan)
    List<DetalleEstudiante> findByPlanEducativo_IdProgramaAndPlanEducativo_NombrePlan(Long idPrograma, String nombrePlan);

    // Buscar por semestre
    List<DetalleEstudiante> findBySemestre(Integer semestre);

    // Buscar por usuario (idUsuario)
    DetalleEstudiante findByIdUsuario(Long idUsuario);
}