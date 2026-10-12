/**
 * ============================================================
 * API.JS
 * ============================================================
 * Funciones utilitarias para hacer peticiones al backend
 * con el token JWT automáticamente.
 *
 * Uso:
 *   const resp = await fetchConToken("/api/citas");
 *   const resp = await fetchConToken("/api/citas", { method: "POST", body: ... });
 *
 * Manejo automático:
 *  - Lee el token de localStorage.
 *  - Lo agrega en el header "Authorization: Bearer <token>".
 *  - Agrega "Content-Type: application/json" si hay body.
 *  - Si el backend responde 401 (token inválido o expirado),
 *    cierra la sesión y manda al login.
 * ============================================================
 */

/**
 * Wrapper de fetch() que agrega el token JWT automáticamente.
 *
 * @param {string} url       ruta relativa (ej: "/api/citas")
 * @param {object} opciones  opciones de fetch (method, body, etc.)
 * @returns {Promise<Response>}
 */
async function fetchConToken(url, opciones = {}) {

    // 1) Leer el token guardado en localStorage
    const token = localStorage.getItem("token");

    // 2) Armar los headers (los del usuario + el Authorization)
    const headers = opciones.headers || {};
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }
    // Si hay body y no se especificó Content-Type, agregarlo
    if (opciones.body && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    // 3) Hacer el fetch con los headers modificados
    const resp = await fetch(url, { ...opciones, headers });

    // 4) Manejar 401 (token expirado o inválido)
    if (resp.status === 401) {
        console.warn("Sesión expirada o token inválido. Redirigiendo al login...");
        localStorage.removeItem("usuario");
        localStorage.removeItem("token");
        alert("Tu sesión expiró. Por favor inicia sesión de nuevo.");
        window.location.href = "/citas.html";
        throw new Error("Sesión expirada");
    }

    // 5) Devolver la respuesta
    return resp;
}



