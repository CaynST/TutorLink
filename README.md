# Proyecto TutorLink UV

Proyecto para la materia de POO. Plataforma de tutorías con IA generativa.

## Miembros del Equipo

* **Backend:** [Tu Nombre]
* **Frontend:** [Nombre de tu compañero]
* **Database:** [Nombre de tu compañero]

## Stack Tecnológico

* **Backend:** Java Spring Boot
* **Frontend:** React con vite
* **Base de Datos:** PostgreSQL
* **Entorno:** Docker
* **Despliegue:** AWS EC2 con Nginx y Let's Encrypt

## Instrucciones de Despliegue (Local)
> Nota: El proyecto ahora soporta perfiles Spring (`dev`, `prod`). Para desarrollo se usa H2 en memoria y puerto 8081; para producción PostgreSQL y puerto 8080.

### Backend (Spring Boot)

Perfiles disponibles:
- `dev`: Base H2 en memoria, consola H2, logs SQL verbosos, puerto 8081.
- `prod`: PostgreSQL, logs moderados, puerto 8080.

Archivos de configuración:
- `backend/tutorlink-backend/src/main/resources/application.properties` (común)
- `backend/tutorlink-backend/src/main/resources/application-dev.properties` (perfil dev)
- `backend/tutorlink-backend/src/main/resources/application-prod.properties` (perfil prod)

#### Ejecutar en desarrollo (fish shell)
```fish
cd "backend/tutorlink-backend"
mvn spring-boot:run -DskipTests -Dspring-boot.run.profiles=dev
```
Acceso:
- API: http://localhost:8081/
- Consola H2: http://localhost:8081/h2 (JDBC URL = `jdbc:h2:mem:tutorlink`)

#### Ejecutar en producción local (simulación)
Exporta variables de entorno antes (ajusta credenciales):
```fish
set -x JDBC_URL "jdbc:postgresql://localhost:5432/tutorlink"
set -x JDBC_DRIVER "org.postgresql.Driver"
set -x JDBC_USER "postgres"
set -x JDBC_PASSWORD "secret"
set -x JPA_DDL_AUTO "update"  # o validate en despliegue real
cd "backend/tutorlink-backend"
mvn spring-boot:run -DskipTests -Dspring-boot.run.profiles=prod
```
API: http://localhost:8080/

#### Variables de entorno relevantes (backend)
| Variable | Perfil | Descripción |
|----------|--------|-------------|
| JDBC_URL | prod | URL JDBC PostgreSQL |
| JDBC_USER | prod | Usuario base de datos |
| JDBC_PASSWORD | prod | Password BD |
| JPA_DDL_AUTO | prod | Estrategia DDL (validate/update) |
| MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD, MAIL_FROM | ambos | Configuración SMTP |
| TUTORLINK_OLLAMA_BASE_URL | ambos | URL base de Ollama |
| TUTORLINK_OLLAMA_MODEL | ambos | Nombre del modelo (ej: llama3.1) |
| TUTORLINK_OLLAMA_TIMEOUT | ambos | Timeout ms para llamadas a LLM |

#### Cambio rápido de puerto en dev
Edita `application-dev.properties` (`server.port=8081`).

### Frontend (React + Vite)
```fish
cd "frontend"
npm install
npm run dev
```
Acceso: http://localhost:3000/

### Despliegue con Docker (opcional futurible)
Si se desea contenerizar más adelante:
1. Crear `Dockerfile` en `backend/` y `frontend/`.
2. Definir un `docker-compose.yml` con servicios `backend`, `frontend` y `nginx` (reverse proxy + certificados).
3. Variable de entorno `SPRING_PROFILES_ACTIVE=prod` para backend.

### Despliegue en AWS EC2 (resumen)
1. Instalar JDK 21 y PostgreSQL (o usar RDS).
2. Exportar variables de entorno (ver tabla arriba).
3. Construir jar: `mvn -q -DskipTests package`.
4. Ejecutar: `java -jar tutorlink-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`.
5. Configurar Nginx como reverse proxy (80/443) -> 8080.
6. Certificados con Let's Encrypt (certbot) y renovación automática.

### Errores comunes / troubleshooting
| Error | Causa probable | Solución |
|-------|----------------|----------|
| `Not a managed type` | Clase sin `@Entity` usada en un repositorio | Agregar `@Entity`, PK con `@Id` |
| `Address already in use` | Puerto ocupado | Cambiar `server.port` o cerrar proceso previo |
| `Cannot load driver class: org.h2.Driver` | Dependencia H2 sólo en scope test | Mover H2 a runtime (ya aplicado) |
| `validate` falla en prod | Esquema no creado | Usar `update` temporalmente o migraciones |

---
## Instrucciones de Despliegue (Local con Docker legacy)
Las antiguas instrucciones (Docker) se han movido aquí como referencia histórica:

1. Asegurarse de tener Docker y Docker Compose instalados.
2. Clonar este repositorio.
3. Crear los `Dockerfile` dentro de `/backend` y `/frontend`.
4. Ejecutar `docker compose up -d --build` desde la raíz del proyecto.
5. El sitio estará disponible en `localhost:3000`.


## API Backend (Resumen rápido)

Base URL por defecto: `http://localhost:8080`

Autenticación: Bearer JWT en el header `Authorization: Bearer <token>` para rutas protegidas (`/api/**`), excepto `/api/auth/register` y `/api/auth/login`.

### Autenticación

- POST `/api/auth/register` (público)
	- Body (JSON):
		- correo (string, email, requerido)
		- contrasena (string, 8-30, requerido)
		- nombre (string, requerido)
		- apellidos (string, requerido)
		- matricula, telefono, correoAlternativo, ciudad, pais, fotoPerfilUrl (opcionales)
	- Respuesta 201 Created: Usuario (sin contraseña)

- POST `/api/auth/login` (público)
	- Body (JSON):
		- correo (string, email, requerido)
		- contrasena (string, requerido)
	- Respuesta 200 OK: `{ "token": "<JWT>" }`

- GET `/api/auth/me` (protegido)
	- Header: `Authorization: Bearer <JWT>`
	- Respuesta 200 OK: `{ username, authorities }`

Notas:
- El backend requiere que existan roles base (ESTUDIANTE, TUTOR, ADMIN, SUDO, LLM). Se crean automáticamente al arrancar.
- Configurar `jwt.secret` (Base64, >= 256 bits) y credenciales de Postgres en `backend/src/main/resources/application.properties`.

### Preguntas

Entidad base según plan de datos. Respuestas usan DTOs para no exponer toda la entidad.

- POST `/api/preguntas` (protegido)
	- Body (JSON):
		- titulo (string, requerido)
		- texto (string, requerido)
		- scope_tipo (string, requerido) — uno de: GENERAL | FACULTAD | PLAN
		- estado (string, opcional; por defecto PENDIENTE) — uno de: PENDIENTE | PUBLICADA | RECHAZADA
		- id_facultad_scope (long, opcional; requerido si `scope_tipo == FACULTAD`)
		- id_plan_educativo_scope (long, opcional; requerido si `scope_tipo == PLAN`)
	- Respuesta 201 Created: PreguntaResponseDto
		- id_pregunta (long)
		- titulo, texto, estado (string)
		- fecha_creacion (ISO-8601)
		- scope_tipo (string)
		- autor { nombre, apellidos }

- GET `/api/preguntas` (protegido, paginado)
	- Query params opcionales (validados estrictamente):
		- estado = PENDIENTE | PUBLICADA | RECHAZADA
		- scope_tipo = GENERAL | FACULTAD | PLAN
		- id_facultad_scope (long)
		- id_plan_educativo_scope (long)
	- Paginación y sort (Spring): `page`, `size`, `sort`.
	- Orden por defecto: `fechaCreacion,desc`.
	- Respuesta 200 OK: `Page<PreguntaSummaryDto>`
		- content[]: { id_pregunta, titulo, estado, fecha_creacion, autor { nombre, apellidos } }
		- page metadata: size, number, totalElements, totalPages, etc.

### Ejemplos de solicitud (JSON)

Registro (POST /api/auth/register):

```
{
	"correo": "ana@uv.mx",
	"contrasena": "MiClaveSegura1",
	"nombre": "Ana",
	"apellidos": "Pérez López"
}
```

Login (POST /api/auth/login):

```
{
	"correo": "ana@uv.mx",
	"contrasena": "MiClaveSegura1"
}
```

Crear pregunta (POST /api/preguntas):

```
{
	"titulo": "Duda sobre herencia en Java",
	"texto": "¿Cómo funciona super en constructores?",
	"scope_tipo": "FACULTAD",
	"id_facultad_scope": 1
}
```

Listado paginado con filtros (GET /api/preguntas):

```
/api/preguntas?estado=PENDIENTE&scope_tipo=FACULTAD&id_facultad_scope=1&page=0&size=10
```

## Convenciones de pruebas

1) Perfil de pruebas y base de datos

- Usamos el perfil `test` con una base de datos H2 en memoria para las pruebas de integración.
- La configuración vive en `backend/src/test/resources/application-test.properties` e inicia el esquema en memoria con `ddl-auto=create-drop` y compatibilidad PostgreSQL.

2) Aislamiento entre pruebas con DirtiesContext

- Todas las clases de pruebas de integración DEBEN llevar esta anotación a nivel de clase para reiniciar el contexto (y la base H2) tras cada método de prueba y evitar errores de Unique index violation:

```java
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class MiIntegrationTest { /* ... */ }
```

3) Ejecutar la suite de pruebas

- Desde la carpeta `backend/`:

```bash
mvn test
```

- Alternativa desde la raíz del repositorio:

```bash
mvn -f backend/pom.xml test
```

## Buenas prácticas de pruebas

1) Nomenclatura de clases de integración

- Todas las pruebas de integración deben terminar en `...IntegrationTest.java`.
- Ejemplos: `AuthIntegrationTest.java`, `PreguntaIntegrationTest.java`, `TutorIntegrationTest.java`.

2) Estructura de carpetas

- Los archivos de prueba viven en `backend/src/test/java/...` siguiendo el mismo package que el código fuente de `src/main/java`.
- Archivos de configuración de pruebas (por ejemplo, `application-test.properties`) viven en `backend/src/test/resources`.

3) Datos de prueba

- Cada prueba debe crear sus propios datos y no depender de efectos de pruebas previas.
- Usa `@BeforeEach` para preparar datos mínimos por prueba (usuarios de prueba, roles, etc.). Ejemplo:

```java
@BeforeEach
void setup() {
	// Crear/asegurar usuario de prueba
	// Si existe por un run previo, no fallar: buscar y reutilizar o limpiar segun el caso
}
```

- Con `@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)` el contexto (y H2) se reinician tras cada método, por lo que lo normal es crear todo lo necesario en el propio test o en `@BeforeEach`.
- Evita asumir IDs fijos. Obtén los IDs desde las respuestas de la API o del repositorio tras guardar entidades.
- Si hay mucha repetición para crear datos, considera helpers o factories de test (métodos privados reutilizables dentro de la clase o utilidades en el mismo módulo de test).
