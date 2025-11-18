package com.tutorlink.repository;

import com.tutorlink.model.DetalleTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleTutorRepository extends JpaRepository<DetalleTutor, Long> {
    // Buscar por usuario (idUsuario)
    DetalleTutor findByIdUsuario(Long idUsuario);
}