/*
 Simple TypeScript client for TutorLink backend (fetch-based)
 Corrected BASE_URL resolution to avoid `process` in browser.
*/

// Resolve base URL safely for browser and build tools (Vite uses import.meta.env)
const BASE_URL =
  // window override for quick local testing (set window.__TUTORLINK_BASE_URL__ in dev)
  (typeof window !== 'undefined' && (window as any).__TUTORLINK_BASE_URL__) ||
  // Vite / modern bundlers: import.meta.env.VITE_TUTORLINK_BASE_URL
  (typeof import.meta !== 'undefined' && (import.meta as any).env && (import.meta as any).env.VITE_TUTORLINK_BASE_URL) ||
  // fallback
  'http://localhost:18081';

type User = {
  id_usuario?: number;
  correo?: string;
  nombre?: string;
  rol?: string;
};

type AuthResponse = {
  token: string;
  usuario: User;
};

async function request<T>(path: string, opts: RequestInit = {}, token?: string): Promise<T> {
  const headers: Record<string,string> = {
    'Content-Type': 'application/json',
    ...(opts.headers as Record<string,string> || {}),
  };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  const res = await fetch(`${BASE_URL}${path}`, { ...opts, headers });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status} ${res.statusText}: ${text}`);
  }
  const text = await res.text();
  try {
    return text ? JSON.parse(text) : ({} as T);
  } catch (e) {
    // not JSON
    return text as unknown as T;
  }
}

// Auth
export async function login(correo: string, contrasena: string): Promise<AuthResponse> {
  return request<AuthResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ correo, contrasena }),
  });
}

export async function register(payload: { correo: string; contrasena: string; nombre?: string }): Promise<any> {
  return request<any>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

// Preguntas
export async function createQuestion(payload: { titulo: string; contenido: string }, token: string): Promise<any> {
  return request<any>('/preguntas', {
    method: 'POST',
    body: JSON.stringify(payload),
  }, token);
}

export async function getQuestion(id: number, token?: string): Promise<any> {
  return request<any>(`/preguntas/${id}`, { method: 'GET' }, token);
}

// Generación LLM
export async function generateAnswer(preguntaId: number, token: string): Promise<any> {
  // Body is empty according to backend; pass {} to be safe
  return request<any>(`/preguntas/${preguntaId}/generar`, { method: 'POST', body: JSON.stringify({}) }, token);
}

// Respuestas: aprobar/rechazar
export async function approveAnswer(respuestaId: number, token: string): Promise<any> {
  return request<any>(`/respuestas/${respuestaId}/aprobar`, { method: 'PUT' }, token);
}

export async function rejectAnswer(respuestaId: number, token: string): Promise<any> {
  return request<any>(`/respuestas/${respuestaId}/rechazar`, { method: 'PUT' }, token);
}

// Helper: extract token from login response and set default (optional)
export function bearer(token: string) {
  return `Bearer ${token}`;
}

export default {
  login,
  register,
  createQuestion,
  getQuestion,
  generateAnswer,
  approveAnswer,
  rejectAnswer,
};
