package com.motatoelectronics.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTIDAD: Repuesto
 * ============================================================
 * Representa un repuesto del inventario.
 * Los repuestos se asocian a citas a través de cita_repuestos.
 */
@Entity
@Table(name = "repuestos")
public class Repuesto {

    /** Identificador único del repuesto. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del repuesto (ej: Panel LED 55"). */
    @Column(nullable = false, length = 150)
    private String nombre;

    /** Descripción del repuesto. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Precio unitario. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /** Cantidad disponible en inventario. */
    @Column(nullable = false)
    private Integer stock = 0;

    /** ¿El proveedor da garantía? */
    @Column(name = "lleva_garantia", nullable = false)
    private Boolean llevaGarantia = false;

    /** Días de garantía (solo si llevaGarantia = true). */
    @Column(name = "garantia_dias")
    private Integer garantiaDias;

    /** Estado del repuesto en el inventario. */
    @Column(nullable = false, length = 20)
    private String estado = "DISPONIBLE";

    /** Fecha en que se registró el repuesto. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // =========================================================
    // Constructor vacío
    // =========================================================
    public Repuesto() {
    }

    // =========================================================
    // Getters y Setters
    // =========================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getLlevaGarantia() { return llevaGarantia; }
    public void setLlevaGarantia(Boolean llevaGarantia) { this.llevaGarantia = llevaGarantia; }

    public Integer getGarantiaDias() { return garantiaDias; }
    public void setGarantiaDias(Integer garantiaDias) { this.garantiaDias = garantiaDias; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}

