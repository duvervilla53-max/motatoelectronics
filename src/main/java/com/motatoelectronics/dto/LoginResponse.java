package com.motatoelectronics.dto;

/**
 * ============================================================
 * DTO: LoginResponse
 * ============================================================
 * Respuesta del login. Nunca expone la contraseña.
 *
 * Además de los datos del usuario, ahora incluye el token JWT
 * que el frontend debe guardar y enviar en cada petición.
 * ============================================================
 */
public class LoginResponse {

    /** Indica si el login fue correcto. */
    private boolean correcto;

    /** Mensaje informativo para el usuario. */
    private String mensaje;

    /** Rol del usuario: CLIENTE | TECNICO | ADMINISTRADOR. */
    private String rol;

    /** ID del usuario. */
    private Integer id;

    /** Nombre completo del usuario. */
    private String nombre;

    /** Correo del usuario. */
    private String correo;

    /** Token JWT para autenticar las siguientes peticiones. */
    private String token;

    // =========================================================
    // Constructores
    // =========================================================
    public LoginResponse() {
    }

    /**
     * Constructor completo con token.
     */
    public LoginResponse(boolean correcto, String mensaje, String rol,
                         Integer id, String nombre, String correo, String token) {
        this.correcto = correcto;
        this.mensaje = mensaje;
        this.rol = rol;
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.token = token;
    }

    /**
     * Constructor sin token (para respuestas de error).
     */
    public LoginResponse(boolean correcto, String mensaje, String rol,
                         Integer id, String nombre, String correo) {
        this(correcto, mensaje, rol, id, nombre, correo, null);
    }

    // =========================================================
    // Getters y Setters
    // =========================================================
    public boolean isCorrecto() { return correcto; }
    public void setCorrecto(boolean correcto) { this.correcto = correcto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
