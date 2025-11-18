package com.tutorlink.repository;

import com.tutorlink.model.PlanEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanEducativoRepository extends JpaRepository<PlanEducativo, Long> { // Long es el tipo de idPlan
    // Métodos personalizados si se necesitan
    // Por ejemplo, buscar por idPrograma y nombrePlan si es común
    PlanEducativo findByIdProgramaAndNombrePlan(Long idPrograma, String nombrePlan);
}