package com.tutorlink.config;

import com.tutorlink.model.Rol;
import com.tutorlink.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedRoles(RolRepository rolRepository) {
        return args -> {
            List<String> required = List.of("ESTUDIANTE", "TUTOR", "ADMIN", "SUDO", "LLM");
            for (String nombre : required) {
                rolRepository.findByNombreRol(nombre)
                        .orElseGet(() -> rolRepository.save(new Rol(null, nombre)));
            }
        };
    }
}
