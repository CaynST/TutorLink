package com.tutorlink.repository;

import com.tutorlink.model.ProgramaEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramaEducativoRepository extends JpaRepository<ProgramaEducativo, Long> {
    // Métodos personalizados si se necesitan
}