package com.motatoelectronics.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTIDAD: CitaRepuesto
 * ============================================================
 * Representa un repuesto usado en una cita.
 *
 * Hay DOS tipos:
 *  1) Del INVENTARIO  → repuestoId != null, requiere aprobación del cliente
 *  2) MANUAL          → repuestoId == null, nombreManual lleno.
 *                       NO requiere aprobación (se aprueba automáticamente).
 *
 * Estados:
 *  - PENDIENTE  → esperando aprobación del cliente (solo inventario)
 *  - APROBADO   → aprobado (manual o cliente aprobó)
 *  - RECHAZADO  → el cliente lo rechazó (solo inventario)
 */
@Entity
@Table(name = "cita_repuestos")
public class CitaRepuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Cita a la que pertenece. */
    @Column(name = "cita_id", nullable = false)
    private Long citaId;

    /** Repuesto del inventario (null si es manual). */
    @Column(name = "repuesto_id")
    private Long repuestoId;

    /** Nombre manual (solo si repuestoId es null). */
    @Column(name = "nombre_manual", length = 150)
    private String nombreManual;

    /** ¿Lleva garantía? (solo repuestos manuales). */
    @Column(name = "lleva_garantia_manual", nullable = false)
    private Boolean llevaGarantiaManual = false;

    /** Días de garantía (solo repuestos manuales con garantía). */
    @Column(name = "garantia_dias_manual")
    private Integer garantiaDiasManual;

    /** Cantidad usada. */
    @Column(nullable = false)
    private Integer cantidad = 1;

    /** Precio unitario al momento de la cita (congelado). */
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /** Subtotal = cantidad × precio_unitario. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /** ¿El cliente aprobó? (los manuales se aprueban automáticamente). */
    @Column(name = "aprobado_por_cliente", nullable = false)
    private Boolean aprobadoPorCliente = false;

    /** Fecha de aprobación. */
    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    /** Estado: PENDIENTE, APROBADO, RECHAZADO. */
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    /** Fecha de rechazo (si aplica). */
    @Column(name = "fecha_rechazo")
    private LocalDateTime fechaRechazo;

    // =========================================================
    // Constructor vacío
    // =========================================================
    public CitaRepuesto() {
    }

    // =========================================================
    // Getters y Setters
    // =========================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }

    public Long getRepuestoId() { return repuestoId; }
    public void setRepuestoId(Long repuestoId) { this.repuestoId = repuestoId; }

    public String getNombreManual() { return nombreManual; }
    public void setNombreManual(String nombreManual) { this.nombreManual = nombreManual; }

    public Boolean getLlevaGarantiaManual() { return llevaGarantiaManual; }
    public void setLlevaGarantiaManual(Boolean llevaGarantiaManual) { this.llevaGarantiaManual = llevaGarantiaManual; }

    public Integer getGarantiaDiasManual() { return garantiaDiasManual; }
    public void setGarantiaDiasManual(Integer garantiaDiasManual) { this.garantiaDiasManual = garantiaDiasManual; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public Boolean getAprobadoPorCliente() { return aprobadoPorCliente; }
    public void setAprobadoPorCliente(Boolean aprobadoPorCliente) { this.aprobadoPorCliente = aprobadoPorCliente; }

    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDateTime fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaRechazo() { return fechaRechazo; }
    public void setFechaRechazo(LocalDateTime fechaRechazo) { this.fechaRechazo = fechaRechazo; }
}
