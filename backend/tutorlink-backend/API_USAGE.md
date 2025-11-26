# TutorLink - API Usage (examples)

Base URL (dev): http://localhost:18081

## Authentication

### Register (Estudiante)
POST /api/auth/register
Content-Type: application/json

{
  "correo": "estudiante@example.com",
  "contrasena": "secret123",
  "nombre": "Juan",
  "apellidos": "Perez",
  "rol": "ESTUDIANTE",
  "matricula": "MAT2025001",
  "idPlanEducativo": 1
}

Response: 200 OK
{
  "ok": true,
  "mensaje": "Registro de estudiante exitoso"
}

---

### Login
POST /api/auth/login
Content-Type: application/json

{
  "correo": "estudiante@example.com",
  "contrasena": "secret123"
}

Response: 200 OK
{
  "token": "<JWT_TOKEN>",
  "usuario": { "idUsuario": 5, "correo": "estudiante@example.com", "nombre": "Juan" }
}

Use the token in subsequent requests:
Header: `Authorization: Bearer <JWT_TOKEN>`

## Questions

### Create question (authenticated)
POST /preguntas
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "titulo": "¿Cómo integrar X en Java?",
  "texto": "Necesito ayuda para integrar la librería X en mi proyecto",
  "scopeTipo": "GENERAL",
  "idFacultadScope": null,
  "idPlanEducativoScope": null
}

Response: 200 OK
{
  "id": 42,
  "titulo": "¿Cómo integrar X en Java?",
  "texto": "...",
  "estado": "PENDIENTE",
  ...

### Simular Ollama localmente (modo mock)
Si el servicio Ollama no está disponible o no hay un modelo cargado, puedes ejecutar un mock local que responda al endpoint `POST /api/generate` en el puerto `11434`.

1) Desde la carpeta `backend/tutorlink-backend` ejecuta:

```bash
python3 mock_ollama.py
```

Esto levantará un servidor que responde con un JSON simulado: `{"response":"Respuesta simulada por el mock de Ollama para pruebas locales."}`.

2) Arranca el backend apuntando a la URL del mock (si tu backend no fue configurado con la URL por defecto):

```fish
}

java -jar target/tutorlink-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --tutorlink.ollama.base-url=http://localhost:11434
```

3) Ahora llama `POST /preguntas/{id}/generar` con un token válido; el backend recibirá la respuesta simulada y devolverá `200 OK` con `contenido` rellenado.

Notes:
- Si prefieres desactivar las llamadas a Ollama y que el backend devuelva 503 mientras pruebas, ajusta la propiedad `tutorlink.ollama.enabled=false` en `src/main/resources/application.properties` o exporta `TUTORLINK_OLLAMA_ENABLED=false` antes de arrancar.
### Generate LLM answer for a question (authenticated)
POST /preguntas/{id}/generar
Authorization: Bearer <JWT>

Optional query parameter: `idUsuarioLLM` (si no se manda, se usa la propiedad configurada `tutorlink.ollama.llm-user-id`)

Response: 200 OK
{
  "id": 100,
  "preguntaId": 42,
  "contenido": "Respuesta generada por el LLM...",
  "estadoRespuesta": "PENDIENTE_REVISION"
}

## Tutor actions

### Approve an answer (Tutor)
POST /respuestas/{id}/aprobar
Authorization: Bearer <JWT> (must be the tutor assigned)

Response: 200 OK
{
  "id": 100,
  "estadoRespuesta": "PUBLICADA",
  ...
}

### Reject and regenerate (Tutor)
POST /respuestas/{id}/rechazar
Authorization: Bearer <JWT> (must be the tutor assigned)

Response: 200 OK
{
  "id": 101,
  "estadoRespuesta": "PENDIENTE_REVISION",
  ...
}

## Admin

### Delete question
DELETE /preguntas/{id}
Authorization: Bearer <JWT> (ADMIN)

Response: 200 OK
{ "ok": true, "mensaje": "Pregunta eliminada" }

## Notes for frontend integration
- Swagger UI available at `/swagger-ui/index.html` and OpenAPI JSON at `/v3/api-docs` once the backend is running.
- CORS enabled for common localhost frontend origins: `http://localhost:5173`, `http://localhost:3000`.
- Ensure `idPlanEducativo` is numeric in JSON (not string).
- To trigger generation automatically after creating a question, the frontend can call `POST /preguntas/{id}/generar`.

