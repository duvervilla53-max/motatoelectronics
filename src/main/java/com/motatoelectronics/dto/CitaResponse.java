package com.motatoelectronics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * ============================================================
 * DTO: CitaResponse
 * ============================================================
 * Respuesta enriquecida con datos del cliente/técnico
 * y con diagnóstico e informe del técnico.
 * ============================================================
 */
public class CitaResponse {

    // ---- Datos de la cita ----
    private Long id;
    private Long clienteId;
    private Long tecnicoId;

    private LocalDate fecha;
    private LocalTime hora;

    private LocalDate fechaPropuesta;
    private LocalTime horaPropuesta;
    private String motivoReprogramacion;

    private String direccionVisita;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String descripcion;
    private String diagnostico;   // ← NUEVO
    private String informe;       // ← NUEVO

    private String estado;

    // ---- Financiero ----
    private BigDecimal manoDeObra;
    private BigDecimal totalRepuestos;
    private BigDecimal total;
    private Boolean pagado;

    // ---- Garantía ----
    private Integer garantiaServicioDias;
    private String observacionesGarantia;
    private LocalDate fechaCompletada;
    private LocalDate fechaFinGarantia;

    private LocalDateTime fechaCreacion;

    // ---- Datos enriquecidos ----
    private String clienteNombre;
    private String clienteTelefono;
    private String clienteCorreo;
    private String tecnicoNombre;

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

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }

    public String getClienteCorreo() { return clienteCorreo; }
    public void setClienteCorreo(String clienteCorreo) { this.clienteCorreo = clienteCorreo; }

    public String getTecnicoNombre() { return tecnicoNombre; }
    public void setTecnicoNombre(String tecnicoNombre) { this.tecnicoNombre = tecnicoNombre; }
}
