package com.motatoelectronics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTIDAD: CodigoRecuperacion
 * ============================================================
 * Guarda códigos de 6 dígitos para:
 *   - RECUPERACION: recuperar contraseña olvidada
 *   - REGISTRO:     verificar correo en registro por email
 *
 * Cada código dura 15 minutos y solo se puede usar una vez.
 * ============================================================
 */
@Entity
@Table(name = "codigos_recuperacion")
public class CodigoRecuperacion {

    /** ID autoincremental. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Correo al que se envió el código. */
    @Column(nullable = false, length = 150)
    private String correo;

    /** Código de 6 dígitos (ej: "483921"). */
    @Column(nullable = false, length = 6)
    private String codigo;

    /** Tipo: RECUPERACION o REGISTRO. */
    @Column(nullable = false, length = 20)
    private String tipo = "RECUPERACION";

    /** Fecha en que se generó el código. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha en que expira (15 minutos después de crear). */
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    /** true = ya se usó, no se puede reutilizar. */
    @Column(nullable = false)
    private Boolean usado = false;

    // =========================================================
    // Constructor vacío (obligatorio para JPA)
    // =========================================================
    public CodigoRecuperacion() {
    }

    // =========================================================
    // Getters y Setters
    // =========================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public Boolean getUsado() { return usado; }
    public void setUsado(Boolean usado) { this.usado = usado; }
}
