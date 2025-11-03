package com.tutorlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan_educativo",
       uniqueConstraints = {
           // Representa la PK compuesta lógica (id_programa, nombre_plan) como UNIQUE adicional
           @UniqueConstraint(name = "uk_plan_programa_nombre", columnNames = {"id_programa", "nombre_plan"})
       })
public class PlanEducativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer idPlan;

    @ManyToOne
    @JoinColumn(name = "id_programa")
    private ProgramaEducativo programaEducativo;

    @Column(name = "nombre_plan", nullable = false)
    private String nombrePlan;
}
