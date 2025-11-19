### Proyecto para la materia de POO. Plataforma de tutorías con IA generativa.

- **Backend:** Saúl Angulo Triana (Cayn)
- **Frontend:** Quetzali Yatana Roa Moreno
- **Database:** Marco Antonio Solís

---

#### Tecnologías

- **Backend:** Java Spring Boot
- **Frontend:** React con Vite
- **Base de Datos:** PostgreSQL
- **Modelo de IA:** Ollama
- **Despliegue:** AWS EC2

---

#### Descripción del Sistema

Tutorlink es una plataforma de tutorías académicas que utiliza inteligencia artificial generativa (Ollama) para mejorar el aprendizaje. Los estudiantes pueden realizar preguntas sobre temas específicos, las cuales son respondidas por un modelo de lenguaje (LLM). Las respuestas generadas pasan por un proceso de revisión realizado por tutores asignados antes de ser publicadas. Los administradores tienen herramientas para gestionar usuarios, preguntas y el flujo de registro.

**Flujo Principal:**
1.  Un estudiante se loguea.
2.  Realiza una pregunta.
3.  El sistema genera una respuesta con Ollama.
4.  La respuesta entra en revisión.
5.  Un tutor aprueba o rechaza la respuesta.
6.  Si se aprueba, la respuesta se vuelve visible para el estudiante.

**Actores:** Alumnos, Tutores, Administradores, Súper Usuarios.

---

#### Requisitos Funcionales (RFs)

| Código | Requisito | Descripción |
| :--- | :--- | :--- |
| RF-01 | Panel de registro | Permitir a alumnos y tutores registrarse. |
| RF-02 | Panel de login | Permitir a los usuarios iniciar sesión. |
| RF-03 | Panel de control | Panel de gestión para admins y súper usuarios. |
| RF-04 | Realizar pregunta | Permitir a los alumnos hacer preguntas. |
| RF-05 | Escoger alcance de pregunta | Permitir escoger el alcance (general, carrera, facultad, semestre). |
| RF-06 | Sugerir pregunta | Detectar preguntas similares ya realizadas. |
| RF-07 | Buscar preguntas | Contar con un buscador de preguntas. |
| RF-08 | Filtrar búsqueda de preguntas | Permitir filtrar la búsqueda por alcance. |
| RF-09 | Notificar pregunta contestada | Notificar al alumno cuando su duda sea respondida. |
| RF-10 | Notificar respuesta del LLM | Notificar a los tutores cuando haya una nueva respuesta del LLM. |
| RF-11 | Visualizar preguntas en revisión | Permitir a los tutores ver preguntas pendientes. |
| RF-12 | Validar respuesta | Permitir a los tutores marcar una respuesta como válida. |
| RF-13 | Rechazar respuesta | Permitir a los tutores rechazar una respuesta. |
| RF-14 | Consultar perfil | Permitir a los usuarios ver su perfil. |
| RF-15 | Cambiar foto de perfil | Permitir cambiar la foto de perfil. |
| RF-16 | Consultar tutorados | Permitir a los tutores ver a sus tutorados. |
| RF-17 | Buscar tutorados | Contar con un buscador de tutorados. |
| RF-18 | Filtrar tutorados | Permitir filtrar tutorados por plan educativo o semestre. |
| RF-19 | Gestionar tutores | Permitir a los administradores gestionar tutores. |
| RF-20 | Gestionar tutorados | Permitir a los administradores gestionar tutorados. |
| RF-21 | Validar registro | Permitir a los administradores admitir registros. |
| RF-22 | Gestionar preguntas | Permitir a los administradores gestionar preguntas. |
| RF-23 | Control de súper usuario | Contar con un súper usuario con control total. |

---

#### Arquitectura Backend

El backend sigue una arquitectura de 4 capas:

1.  **Controller:** Recibe solicitudes HTTP.
2.  **Service:** Coordina operaciones.
3.  **Business:** Contiene la lógica de negocio.
4.  **Data Access (Repository):** Acceso a la base de datos.

---

#### Base de Datos

La estructura de la base de datos está definida en `EntidadesDB.txt`. Incluye tablas para roles, usuarios, detalles específicos por rol, jerarquía académica y el flujo principal de preguntas y respuestas.

---

#### API REST

**Base URL por defecto:** `http://localhost:8080`  
**Autenticación:** Bearer JWT en el header `Authorization: Bearer <token>` para rutas protegidas (`/api/**`), excepto `/api/auth/register` y `/api/auth/login`.

##### Endpoints Principales

- **POST `/api/auth/register`** (público)
    - Body (JSON):
        ```json
        {
            "correo":"tutor2@example.com",
            "contrasena":"Pass1234!",
            "rol":"TUTOR",
            "nombre":"Tutor",
            "apellidos":"Dos",
            "telefono":"555-5678"
        }
        ```
    - Respuesta 201 Created: Usuario (sin contraseña)

- **POST `/api/auth/login`** (público)
    - Body (JSON):
        ```json
        {
            "correo": "string",
            "contrasena": "string"
        }
       ```
    - Respuesta 200 OK: `{ "token": "<JWT>" }`

- **GET `/api/auth/me`** (protegido)
    - Header: `Authorization: Bearer <JWT>`
    - Respuesta 200 OK: `{ username, authorities }`

- **POST `/api/preguntas`** (protegido)
    - Body (JSON):
        ```json
        {
            "titulo": "string",
            "texto": "string",
            "scope_tipo": "GENERAL|FACULTAD|PLAN",
            "id_facultad_scope": 1,
            "id_plan_educativo_scope": 1
        }
        ```
    - Respuesta 201 Created: PreguntaResponseDto

- **GET `/api/preguntas`** (protegido, paginado)
    - Query params: `estado`, `scope_tipo`, `id_facultad_scope`, `id_plan_educativo_scope`, `page`, `size`, `sort`.
    - Respuesta 200 OK: `Page<PreguntaSummaryDto>`

---

#### Pruebas

- Usamos el perfil `test` con una base de datos H2 en memoria.
- Ejecutar pruebas: `mvn test` desde la carpeta `backend/`.
- Todas las clases de pruebas de integración deben llevar `@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)`.

---

#### Estado del Proyecto

- ✅ **Backend:** Completado. Todas las funcionalidades principales están implementadas y probadas.
- 🟡 **Frontend:** En desarrollo.
- 🔧 **Despliegue:** Preparado para despliegue directo en AWS EC2 (sin Docker).

---

#### Cómo Ejecutar el Backend

1.  Clona este repositorio.
2.  Navega a la carpeta `backend/tutorlink-backend`.
3.  Ejecuta: `mvn spring-boot:run`
4.  El backend estará disponible en `http://localhost:8080`.

---

#### Notas Importantes

- El backend crea automáticamente los roles base (ESTUDIANTE, TUTOR, ADMIN, SUDO, LLM) al iniciar.
- Configura `jwt.secret` (Base64, >= 256 bits) y las credenciales de PostgreSQL en `backend/src/main/resources/application.properties`.
