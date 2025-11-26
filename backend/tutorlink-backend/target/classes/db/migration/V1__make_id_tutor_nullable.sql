-- Flyway migration: make id_tutor_asignado nullable in detalle_estudiante
ALTER TABLE detalle_estudiante ALTER COLUMN id_tutor_asignado DROP NOT NULL;
