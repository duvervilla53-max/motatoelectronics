package com.motatoelectronics.dto;

/**
 * ============================================================
 * DTO: LoginRequest
 * ============================================================
 *
 * Objeto que representa la petición de login enviada por el
 * frontend. Solo contiene los datos mínimos necesarios.
 */
public class LoginRequest {

    private String correo;
    private String password;

    public LoginRequest() {
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}