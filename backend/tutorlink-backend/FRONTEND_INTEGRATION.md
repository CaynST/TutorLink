# Guía de integración Frontend — TutorLink (backend)

Este documento explica cómo el equipo frontend debe conectarse al backend de TutorLink: endpoints, autenticación, ejemplos de llamadas y recomendaciones para pruebas locales.

**Resumen mínimo**
- Base URL dev: `http://localhost:18081`
- Auth: JWT Bearer en header `Authorization: Bearer <token>`
- Ollama (LLM): mock en `http://localhost:11434` (levántalo para pruebas E2E)
- OpenAPI: `backend/tutorlink-backend/openapi.json` → usar para generar cliente

1) Flujo de autenticación (ejemplo)
- Registrar (opcional): `POST /api/auth/register` (body: `correo`, `contrasena`, `nombre`...)
- Login: `POST /api/auth/login` -> respuesta incluye `token` y `usuario`.
- Guardar token en storage seguro (por ejemplo `localStorage` o memory store) y enviarlo en cada petición.

Ejemplo usando `fetch` (login y uso del token):
```js
// login
const resp = await fetch('http://localhost:18081/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ correo: 'sudo@example.com', contrasena: 'sudopass' })
});
const data = await resp.json();
const token = data.token;

// llamada protegida ejemplo
const res = await fetch('http://localhost:18081/preguntas/1', {
  method: 'GET',
  headers: { 'Authorization': `Bearer ${token}` }
});
const pregunta = await res.json();
```

Ejemplo usando `axios`:
```js
import axios from 'axios';
const api = axios.create({ baseURL: 'http://localhost:18081' });

// login
const { data } = await api.post('/api/auth/login', { correo: 'sudo@example.com', contrasena: 'sudopass' });
const token = data.token;
api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

// crear pregunta
await api.post('/preguntas', { titulo: 'Prueba', contenido: 'Contenido' });

// generar respuesta LLM
await api.post('/preguntas/1/generar');
```

2) Endpoints clave (resumen)
- `POST /api/auth/register` — registro
- `POST /api/auth/login` — login (devuelve token)
- `POST /preguntas` — crear pregunta (protegido)
- `GET /preguntas/{id}` — obtener pregunta
- `POST /preguntas/{id}/generar` — generar respuesta LLM (protegido, roles: SUDO/TUTOR según flujo)
- `PUT /respuestas/{id}/aprobar` — aprobar respuesta (tutor/admin)
- `PUT /respuestas/{id}/rechazar` — rechazar respuesta (tutor/admin)

3) Generación LLM en desarrollo
- Levanta el mock (si quieres que la UI muestre contenido generado):

```fish
python3 backend/tutorlink-backend/mock_ollama.py &
```

- Asegúrate que `tutorlink.ollama.base-url` apunte a `http://localhost:11434` o que `TUTORLINK_OLLAMA_BASE_URL` esté configurada.
- Alternativa: desactivar LLM en `application-dev.properties` poniendo `tutorlink.ollama.enabled=false` (en cuyo caso la ruta `/preguntas/{id}/generar` devolverá 503 o un mensaje controlado según la implementación).

4) Generación cliente a partir del OpenAPI
- Archivo: `backend/tutorlink-backend/openapi.json`
- Recomendación: generar cliente TypeScript con `openapi-generator` o `swagger-codegen` para evitar errores de tipado.

Ejemplo (openapi-generator):
```bash
openapi-generator-cli generate -i backend/tutorlink-backend/openapi.json -g typescript-axios -o frontend/src/api-client
```

5) CORS y entorno local
- El backend tiene configuración CORS para orígenes locales; si experimentas errores CORS, verificar `CorsConfig` en `src/main/java/com/tutorlink/config`.
- Si el frontend corre en `http://localhost:5173` (Vite), usa esa URL en las pruebas.

6) Manejo de roles y permisos
- Los endpoints están protegidos según roles. Claims del JWT contienen `rol` (ej: `SUDO`, `TUTOR`, `ESTUDIANTE`).
- Para pruebas de funcionalidades administrativas, usa una cuenta con rol `SUDO` o `ADMIN`.

7) Errores comunes y cómo manejarlos en UI
- 401 Unauthorized: token faltante/expirado → redirigir a login
- 403 Forbidden: el usuario no tiene rol/permiso → mostrar mensaje y bloquear UI
- 503 / 500 en `/preguntas/{id}/generar`: backend no pudo contactar Ollama → mostrar fallback ("Generación no disponible") y permitir reintentos

8) Recomendaciones para desarrollo y tests E2E
- Levantar mock Ollama para pruebas que impliquen generación de texto.
- Usar cuentas de prueba con datos preconfigurados (crear script SQL o endpoints de inicialización en dev).
- Testear con token válido (evitar usar credenciales reales en CI).

9) Contactos / referencias
- OpenAPI JSON: `backend/tutorlink-backend/openapi.json`
- Mock Ollama: `backend/tutorlink-backend/mock_ollama.py`
- Docs de API y ejemplos se encuentran en `backend/tutorlink-backend/API_USAGE.md`


