package com.tutorlink.repository;

import com.tutorlink.model.Pregunta;
import com.tutorlink.model.enums.EstadoPregunta;
import com.tutorlink.model.enums.ScopePregunta;
import org.springframework.data.jpa.domain.Specification;

public class PreguntaSpecification {

    public static Specification<Pregunta> hasEstado(String estado) {
        return (root, query, cb) -> {
            if (estado == null || estado.isBlank()) return cb.conjunction();
            try {
                EstadoPregunta ep = EstadoPregunta.valueOf(estado.trim().toUpperCase());
                return cb.equal(root.get("estado"), ep);
            } catch (IllegalArgumentException ex) {
                return cb.conjunction();
            }
        };
    }

    public static Specification<Pregunta> hasScopeTipo(String scopeTipo) {
        return (root, query, cb) -> {
            if (scopeTipo == null || scopeTipo.isBlank()) return cb.conjunction();
            try {
                ScopePregunta sp = ScopePregunta.valueOf(scopeTipo.trim().toUpperCase());
                return cb.equal(root.get("scopeTipo"), sp);
            } catch (IllegalArgumentException ex) {
                return cb.conjunction();
            }
        };
    }

    public static Specification<Pregunta> hasFacultadId(Long idFacultad) {
        return (root, query, cb) -> idFacultad == null
                ? cb.conjunction()
                : cb.equal(root.get("facultadScope").get("idFacultad"), idFacultad.intValue());
    }

    public static Specification<Pregunta> hasPlanEducativoId(Long idPlan) {
        return (root, query, cb) -> idPlan == null
                ? cb.conjunction()
                : cb.equal(root.get("planEducativoScope").get("idPlan"), idPlan.intValue());
    }

    public static Specification<Pregunta> build(String estado,
                                                String scopeTipo,
                                                Long idFacultad,
                                                Long idPlan) {
        return Specification.where(hasEstado(estado))
                .and(hasScopeTipo(scopeTipo))
                .and(hasFacultadId(idFacultad))
                .and(hasPlanEducativoId(idPlan));
    }
}
