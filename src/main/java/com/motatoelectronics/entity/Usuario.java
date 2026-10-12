package com.motatoelectronics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTIDAD: Usuario
 * ============================================================
 *
 * Representa la tabla "usuarios" de la base de datos.
 *
 * Reglas de negocio:
 *  - El rol puede ser: CLIENTE, TECNICO, ADMINISTRADOR.
 *  - El estado puede ser: ACTIVO, INACTIVO, PENDIENTE.
 *  - La contraseña siempre se almacena hasheada con BCrypt.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    /** Identificador único (AUTO_INCREMENT). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Nombre completo del usuario. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Documento de identidad único. */
    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    /** Correo electrónico único (usado para login). */
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    /** Teléfono de contacto. */
    @Column(length = 20)
    private String telefono;

    /** Dirección de residencia. */
    @Column(length = 200)
    private String direccion;

    /** Rol del usuario: CLIENTE | TECNICO | ADMINISTRADOR. */
    @Column(nullable = false, length = 20)
    private String rol;

    /** Estado: ACTIVO | INACTIVO | PENDIENTE. */
    @Column(nullable = false, length = 20)
    private String estado = "ACTIVO";

    /** Fecha de creación del registro. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Contraseña hasheada con BCrypt (nunca texto plano). */
    @Column(nullable = false, length = 255)
    private String password;

    // ==========================================================
    // Constructores
    // ==========================================================

    /** Constructor vacío requerido por JPA. */
    public Usuario() {
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}