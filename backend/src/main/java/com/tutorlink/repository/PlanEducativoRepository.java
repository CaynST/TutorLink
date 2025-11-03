package com.tutorlink.repository;

import com.tutorlink.model.PlanEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanEducativoRepository extends JpaRepository<PlanEducativo, Integer> {
}
