package com.tutorlink.config;

import com.tutorlink.model.Rol;
import com.tutorlink.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;

    public DataInitializer(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure basic roles exist
        List<String> needed = List.of("ESTUDIANTE", "TUTOR", "ADMIN", "SUDO", "LLM");
        for (String r : needed) {
            rolRepository.findByNombreRol(r).orElseGet(() -> rolRepository.save(new Rol(r)));
        }
    }
}
