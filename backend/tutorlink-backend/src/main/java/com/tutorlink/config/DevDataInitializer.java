package com.tutorlink.config;

import com.tutorlink.model.AreaAcademica;
import com.tutorlink.model.Facultad;
import com.tutorlink.model.PlanEducativo;
import com.tutorlink.model.ProgramaEducativo;
import com.tutorlink.model.Region;
import com.tutorlink.repository.AreaAcademicaRepository;
import com.tutorlink.repository.FacultadRepository;
import com.tutorlink.repository.PlanEducativoRepository;
import com.tutorlink.repository.ProgramaEducativoRepository;
import com.tutorlink.repository.RegionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final AreaAcademicaRepository areaRepository;
    private final FacultadRepository facultadRepository;
    private final ProgramaEducativoRepository programaRepository;
    private final PlanEducativoRepository planRepository;

    public DevDataInitializer(RegionRepository regionRepository,
                              AreaAcademicaRepository areaRepository,
                              FacultadRepository facultadRepository,
                              ProgramaEducativoRepository programaRepository,
                              PlanEducativoRepository planRepository) {
        this.regionRepository = regionRepository;
        this.areaRepository = areaRepository;
        this.facultadRepository = facultadRepository;
        this.programaRepository = programaRepository;
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear región mínima
        String regionName = "Dev Region";
        Region region = regionRepository.findAll().stream()
                .filter(r -> regionName.equals(r.getNombreRegion()))
                .findFirst()
                .orElseGet(() -> regionRepository.save(new Region(regionName)));

        // Crear área académica mínima
        String areaName = "Dev Area";
        AreaAcademica area = areaRepository.findAll().stream()
                .filter(a -> areaName.equals(a.getNombreArea()))
                .findFirst()
                .orElseGet(() -> areaRepository.save(new AreaAcademica(areaName)));

        // Crear facultad
        String facultadName = "Dev Facultad";
        Facultad facultad = facultadRepository.findAll().stream()
                .filter(f -> facultadName.equals(f.getNombreFacultad()))
                .findFirst()
                .orElseGet(() -> {
                    Facultad f = new Facultad(facultadName);
                    f.setRegion(region);
                    return facultadRepository.save(f);
                });

        // Crear programa educativo
        String programaName = "Dev Programa";
        ProgramaEducativo programa = programaRepository.findAll().stream()
                .filter(p -> programaName.equals(p.getNombrePrograma()))
                .findFirst()
                .orElseGet(() -> {
                    ProgramaEducativo p = new ProgramaEducativo(programaName);
                    p.setFacultad(facultad);
                    p.setArea(area);
                    return programaRepository.save(p);
                });

        // Crear plan educativo
        String planName = "Dev Plan";
        Optional<PlanEducativo> existingPlan = planRepository.findAll().stream()
                .filter(pl -> planName.equals(pl.getNombrePlan()))
                .findFirst();
        if (existingPlan.isEmpty()) {
            PlanEducativo plan = new PlanEducativo();
            plan.setIdPrograma(programa.getIdPrograma());
            plan.setNombrePlan(planName);
            plan.setPrograma(programa);
            planRepository.save(plan);
        }
    }
}
