package com.tutorlink.repository;

import com.tutorlink.model.DetalleAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleAdminRepository extends JpaRepository<DetalleAdmin, Long> {
    // Buscar por usuario (idUsuario)
    DetalleAdmin findByIdUsuario(Long idUsuario);
}
