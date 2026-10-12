package com.motatoelectronics.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * ============================================================
 * ENTIDAD: Cita
 * ============================================================
 * Ahora incluye diagnóstico e informe técnico.
 * ============================================================
 */
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "tecnico_id")
    private Long tecnicoId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    // ---- Contrapropuesta del técnico ----
    @Column(name = "fecha_propuesta")
    private LocalDate fechaPropuesta;

    @Column(name = "hora_propuesta")
    private LocalTime horaPropuesta;

    @Column(name = "motivo_reprogramacion", length = 255)
    private String motivoReprogramacion;

    // ---- Datos de la cita ----
    @Column(name = "direccion_visita", nullable = false, length = 255)
    private String direccionVisita;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitud;

    /** Descripción que da el cliente al crear la cita. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Diagnóstico técnico (lo llena el técnico al completar). */
    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    /** Informe final del trabajo (lo llena el técnico al completar). */
    @Column(columnDefinition = "TEXT")
    private String informe;

    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    // ---- Desglose financiero ----
    @Column(name = "mano_de_obra", precision = 10, scale = 2)
    private BigDecimal manoDeObra;

    @Column(name = "total_repuestos", precision = 10, scale = 2)
    private BigDecimal totalRepuestos;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private Boolean pagado = false;

    // ---- Garantía del servicio ----
    @Column(name = "garantia_servicio_dias")
    private Integer garantiaServicioDias;

    @Column(name = "observaciones_garantia", length = 255)
    private String observacionesGarantia;

    @Column(name = "fecha_completada")
    private LocalDate fechaCompletada;

    @Column(name = "fecha_fin_garantia")
    private LocalDate fechaFinGarantia;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // =========================================================
    // Constructores
    // =========================================================
    public Cita() {
    }

    // =========================================================
    // Getters y Setters
    // =========================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public LocalDate getFechaPropuesta() { return fechaPropuesta; }
    public void setFechaPropuesta(LocalDate fechaPropuesta) { this.fechaPropuesta = fechaPropuesta; }

    public LocalTime getHoraPropuesta() { return horaPropuesta; }
    public void setHoraPropuesta(LocalTime horaPropuesta) { this.horaPropuesta = horaPropuesta; }

    public String getMotivoReprogramacion() { return motivoReprogramacion; }
    public void setMotivoReprogramacion(String motivoReprogramacion) { this.motivoReprogramacion = motivoReprogramacion; }

    public String getDireccionVisita() { return direccionVisita; }
    public void setDireccionVisita(String direccionVisita) { this.direccionVisita = direccionVisita; }

    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }

    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getInforme() { return informe; }
    public void setInforme(String informe) { this.informe = informe; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getManoDeObra() { return manoDeObra; }
    public void setManoDeObra(BigDecimal manoDeObra) { this.manoDeObra = manoDeObra; }

    public BigDecimal getTotalRepuestos() { return totalRepuestos; }
    public void setTotalRepuestos(BigDecimal totalRepuestos) { this.totalRepuestos = totalRepuestos; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Boolean getPagado() { return pagado; }
    public void setPagado(Boolean pagado) { this.pagado = pagado; }

    public Integer getGarantiaServicioDias() { return garantiaServicioDias; }
    public void setGarantiaServicioDias(Integer garantiaServicioDias) { this.garantiaServicioDias = garantiaServicioDias; }

    public String getObservacionesGarantia() { return observacionesGarantia; }
    public void setObservacionesGarantia(String observacionesGarantia) { this.observacionesGarantia = observacionesGarantia; }

    public LocalDate getFechaCompletada() { return fechaCompletada; }
    public void setFechaCompletada(LocalDate fechaCompletada) { this.fechaCompletada = fechaCompletada; }

    public LocalDate getFechaFinGarantia() { return fechaFinGarantia; }
    public void setFechaFinGarantia(LocalDate fechaFinGarantia) { this.fechaFinGarantia = fechaFinGarantia; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
