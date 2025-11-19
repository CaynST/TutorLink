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

1.  Asegurarse de tener Docker y Docker Compose instalados.
2.  Clonar este repositorio.
3.  (Solo la primera vez) Crear los `Dockerfile` dentro de `/backend` y `/frontend`.
4.  Ejecutar `docker compose up -d --build` desde la raíz del proyecto.
5.  El sitio estará disponible en `localhost:3000`.


## API Backend (Resumen rápido)

Base URL por defecto (producción): `http://localhost:8080`

Nota de desarrollo: al usar el perfil `dev` el backend arranca en el puerto `8081` (H2 en memoria). URL dev: `http://localhost:8081`

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
