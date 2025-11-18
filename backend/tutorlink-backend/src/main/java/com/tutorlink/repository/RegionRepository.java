package com.tutorlink.repository;

import com.tutorlink.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    // Métodos personalizados si se necesitan
}