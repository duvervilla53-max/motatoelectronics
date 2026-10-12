/**
 * =====================================================
 * panel-guard.js
 * -----------------------------------------------------
 * Utilidades compartidas por los 3 paneles:
 * cliente.html, tecnico.html, admin.html
 *
 * Funciones:
 *  - protegerPanel(rolEsperado): valida sesión y rol.
 *  - cerrarSesion(): limpia sesión y va al login.
 * =====================================================
 */

/**
 * Verifica que exista una sesión activa y que el rol del
 * usuario coincida con el esperado en este panel.
 *
 * Si NO hay sesión → manda al login (citas.html).
 * Si el rol NO coincide → lo manda a su panel correcto.
 * Si todo está bien → devuelve el usuario.
 *
 * @param {string} rolEsperado  "CLIENTE" | "TECNICO" | "ADMINISTRADOR"
 * @returns {object|null}
 */
function protegerPanel(rolEsperado) {

    const raw = localStorage.getItem("usuario");

    // 1) No hay sesión → al login
    if (!raw) {
        window.location.href = "/citas.html";
        return null;
    }

    // 2) Intentar parsear la sesión
    let usuario;
    try {
        usuario = JSON.parse(raw);
    } catch (e) {
        localStorage.removeItem("usuario");
        window.location.href = "/citas.html";
        return null;
    }

    // 3) El rol no coincide → redirigir a su panel correcto
    if (!usuario || usuario.rol !== rolEsperado) {
        switch (usuario && usuario.rol) {
            case "ADMINISTRADOR":
                window.location.href = "/admin.html";
                break;
            case "TECNICO":
                window.location.href = "/tecnico.html";
                break;
            case "CLIENTE":
                window.location.href = "/cliente.html";
                break;
            default:
                localStorage.removeItem("usuario");
                window.location.href = "/citas.html";
        }
        return null;
    }

    // 4) Todo OK
    return usuario;
}

/**
 * Cierra la sesión actual y vuelve al login.
 */
function cerrarSesion() {
    localStorage.removeItem("usuario");
    window.location.href = "/citas.html";
}