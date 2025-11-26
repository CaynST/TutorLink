# TutorLink — README de desarrollo (actualizado)

Resumen rápido
- Proyecto: TutorLink (backend Java Spring Boot)
- Rama actual: `backend-develop1`
- Lenguaje: Java 21, Spring Boot 3.x
- BD: PostgreSQL (dev por defecto en `jdbc:postgresql://localhost:5433/tutorlink`)
- LLM: integración via Ollama (mock disponible en `mock_ollama.py`)

**Qué incluye este README**
- Cómo levantar el backend en entorno de desarrollo
- Configuración importante (JWT, Ollama, DB)
- Cómo probar endpoints clave y ejecutar el mock
- Consideraciones y buenas prácticas

Requisitos locales
- Java 21
- Maven
- Python 3 (para el mock opcional)
- PostgreSQL (puedes usar contenedor o instalación local)

Levantar el backend (modo dev)
1. Asegura que la base de datos esté accesible en `localhost:5433` o ajusta `application-dev.properties`.
2. Construir y ejecutar (desde `backend/tutorlink-backend`):

```fish
# desde la raíz del repo
cd backend/tutorlink-backend
mvn -DskipTests package
# Ejecutar el JAR (usa application-dev.properties porque profile=dev)
java -jar target/tutorlink-backend-0.0.1-SNAPSHOT.jar
```

3. El servidor por defecto escucha en `http://localhost:18081`.

Variables y configuración importante
- JWT: la aplicación usa HS256; en `application-dev.properties` debe existir un secreto de 32 bytes (base64). Ejemplo:
  - `jwt.secret` o `TUTORLINK_JWT_SECRET` (ver `application-dev.properties`).
- Ollama: propiedades:
  - `tutorlink.ollama.base-url` (por defecto `http://localhost:11434`)
  - `tutorlink.ollama.timeout` (ms)
  - `tutorlink.ollama.enabled` (booleano; se puede desactivar para evitar llamadas externas)
- DB: `spring.datasource.url`, `username`, `password` en `application-dev.properties`

Mock de Ollama (para pruebas LLM sin modelo real)
- Archivo: `backend/tutorlink-backend/mock_ollama.py`.
- Ejecutar (en background):

```fish
python3 backend/tutorlink-backend/mock_ollama.py &
```

- Comprueba la respuesta:

```fish
curl -s -X POST http://localhost:11434/api/generate -H 'Content-Type: application/json' -d '{"prompt":"test"}'
# devuelve: {"response":"Respuesta simulada por el mock de Ollama para pruebas locales."}
```

Endpoints y pruebas básicas
- OpenAPI: `backend/tutorlink-backend/openapi.json` y Swagger UI disponible en `/swagger-ui/index.html` cuando el backend está arriba.
- Auth:
  - `POST /api/auth/register` (registro)
  - `POST /api/auth/login` (login) -> devuelve `{ token, usuario }`
- Preguntas/Respuestas:
  - `POST /preguntas` crear pregunta
  - `GET /preguntas/{id}` obtener pregunta
  - `POST /preguntas/{id}/generar` generar respuesta LLM (usa Ollama)
  - Endpoints tutor/admin para aprobar/rechazar

Ejemplo: login y generar respuesta
```fish
# login (guarda el token)
TOKEN=$(curl -s -X POST http://localhost:18081/api/auth/login -H 'Content-Type: application/json' -d '{"correo":"sudo@example.com","contrasena":"sudopass"}' | jq -r '.token')

# crear pregunta (ejemplo)
curl -s -X POST http://localhost:18081/preguntas -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"titulo":"Prueba","contenido":"¿Qué es X?"}' | jq

# generar respuesta usando el mock (suponiendo id=1)
curl -s -X POST http://localhost:18081/preguntas/1/generar -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{}' | jq
```

Diagnóstico de errores comunes
- 500 al llamar `/preguntas/{id}/generar`: normalmente significa que el backend no pudo contactar con Ollama (mock no levantado o URL incorrecta) y la excepción `OllamaCommunicationException` provocó rollback. Levanta el mock o desactiva `tutorlink.ollama.enabled=false` en dev para evitar llamadas.
- JWT inválido: verificar la secret y la expiración del token.
- Errores DB: revisa que las migraciones de Flyway (carpeta `resources/db/migration`) se apliquen correctamente.

Seguridad y buenas prácticas
- No dejar secretos en código ni en commits. Usa variables de entorno para `JWT_SECRET` y credenciales de BD.
- Para cambios en datos que deben persistir entre entornos, usar migraciones versionadas (Flyway/Liquibase). Evitar UPDATE directo en producción sin migración.
- Para pruebas de frontend, usar cuentas de prueba con roles limitados.

Información útil adicional
- Mock Ollama: `backend/tutorlink-backend/mock_ollama.py`
- OpenAPI JSON: `backend/tutorlink-backend/openapi.json`
- Archivo de configuración dev: `backend/tutorlink-backend/src/main/resources/application-dev.properties`
- Bcrypt hash recomendado para `sudopass` (si quieres aplicarlo en DB):
  - `$2b$12$lYEY7aFpyk0ZdfuwEAYbVuhAwhW.Ap5V4uCzyWnsln04YSaeMGJyC`

¿Quieres que además genere un script Flyway con un UPDATE para aplicar la contraseña segura para el usuario SUDO?