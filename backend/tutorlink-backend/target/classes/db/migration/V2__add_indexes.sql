-- Flyway migration: add helpful indexes for common lookups
CREATE INDEX IF NOT EXISTS idx_usuario_correo ON usuario(correo);
CREATE INDEX IF NOT EXISTS idx_usuario_matricula ON usuario(matricula);
CREATE INDEX IF NOT EXISTS idx_pregunta_estado ON pregunta(estado);
CREATE INDEX IF NOT EXISTS idx_respuesta_estado ON respuesta(estado);
