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

// ---------- Employee portal ----------
// The portal keeps its sign-in token in this browser until the employee signs
// out or the session runs out on the server (7 days).
const TOKEN_KEY = "ccm_portal_token";

export function getPortalToken() {
  try {
    return window.localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

export function setPortalToken(token) {
  try {
    if (token) window.localStorage.setItem(TOKEN_KEY, token);
    else window.localStorage.removeItem(TOKEN_KEY);
  } catch {
    // Private browsing can refuse storage; the session then lasts for this page only.
  }
}

// JSON request to a portal endpoint, signed with the employee's token.
export async function portalRequest(method, path, body, token = getPortalToken()) {
  const headers = {};
  if (body !== undefined) headers["Content-Type"] = "application/json";
  if (token) headers.Authorization = `Bearer ${token}`;

  let res;
  try {
    res = await fetch(apiUrl(path), {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch {
    const error = new Error("Could not reach the server. Check your connection and try again.");
    error.status = 0;
    error.fieldErrors = {};
    throw error;
  }

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
