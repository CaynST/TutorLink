package com.tutorlink.repository;

import com.tutorlink.model.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultadRepository extends JpaRepository<Facultad, Long> {
    // Métodos personalizados si se necesitan
}