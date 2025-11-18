package com.tutorlink.repository;

import com.tutorlink.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByMatricula(String matricula); // Puede ser nulo, manejar en servicio
    boolean existsByCorreo(String correo);
    boolean existsByMatricula(String matricula);
}