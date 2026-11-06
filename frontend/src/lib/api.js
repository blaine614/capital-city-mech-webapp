// Single place that knows how to reach the Spring Boot API.
// In dev the Vite proxy handles /api, so PUBLIC_API_BASE can stay empty.
const BASE = import.meta.env.PUBLIC_API_BASE ?? "";

export function apiUrl(path) {
  return `${BASE}${path}`;
}

export async function postJson(path, body) {
  const res = await fetch(apiUrl(path), {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  const payload = await res.json().catch(() => null);

  if (!res.ok) {
    const error = new Error(payload?.message || "That did not go through.");
    error.status = res.status;
    error.fieldErrors = payload?.fieldErrors ?? {};
    throw error;
  }

  return payload;
}

// Used at build time. Falls back to bundled copy so the site still builds
// when the API is not running.
export async function getJson(path, fallback) {
  try {
    const res = await fetch(apiUrl(path));
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return await res.json();
  } catch {
    return fallback;
  }
}
