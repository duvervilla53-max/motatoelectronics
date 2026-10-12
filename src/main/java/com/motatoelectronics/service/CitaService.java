package com.motatoelectronics.service;

// Imports de entidades y repositorios
import com.motatoelectronics.dto.CitaResponse;
import com.motatoelectronics.entity.Cita;
import com.motatoelectronics.entity.CitaRepuesto;
import com.motatoelectronics.entity.Pago;
import com.motatoelectronics.entity.Repuesto;
import com.motatoelectronics.entity.Usuario;
import com.motatoelectronics.repository.CitaRepuestoRepository;
import com.motatoelectronics.repository.CitaRepository;
import com.motatoelectronics.repository.PagoRepository;
import com.motatoelectronics.repository.RepuestoRepository;
import com.motatoelectronics.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * SERVICIO: CitaService
 * ============================================================
 * Lógica de negocio del módulo de citas.
 * Ahora incluye:
 *  - Diagnóstico e informe del técnico.
 *  - Reasignación de citas a otro técnico.
 *  - Listado con filtros para el admin.
 * ============================================================
 */
@Service
public class CitaService {

    @Autowired
    private CitaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private RepuestoRepository repuestoRepository;

    @Autowired
    private CitaRepuestoRepository citaRepuestoRepository;

    // =========================================================
    // CREAR CITA
    // =========================================================
    public Cita crearCita(Cita cita) {

        if (cita.getClienteId() == null) throw new RuntimeException("El cliente es obligatorio.");
        if (cita.getFecha() == null) throw new RuntimeException("La fecha es obligatoria.");
        if (cita.getHora() == null) throw new RuntimeException("La hora es obligatoria.");
        if (cita.getDireccionVisita() == null || cita.getDireccionVisita().isBlank())
            throw new RuntimeException("La dirección es obligatoria.");

        // 🔒 Validar fecha no pasada
        LocalDate hoy = LocalDate.now();
        if (cita.getFecha().isBefore(hoy)) {
            throw new RuntimeException("No se pueden agendar citas en fechas pasadas.");
        }

        // 🔒 Validar hora no pasada (si es hoy)
        if (cita.getFecha().isEqual(hoy)) {
            LocalTime ahora = LocalTime.now();
            if (cita.getHora().isBefore(ahora)) {
                throw new RuntimeException("La hora seleccionada ya pasó. Elige una hora futura.");
            }
        }

        cita.setEstado("PENDIENTE");
        cita.setPagado(false);
        cita.setTecnicoId(null);
        cita.setFechaPropuesta(null);
        cita.setHoraPropuesta(null);
        cita.setMotivoReprogramacion(null);
        cita.setManoDeObra(null);
        cita.setTotalRepuestos(null);
        cita.setTotal(null);
        cita.setFechaCompletada(null);
        cita.setFechaFinGarantia(null);
        cita.setGarantiaServicioDias(null);
        cita.setObservacionesGarantia(null);
        cita.setDiagnostico(null);
        cita.setInforme(null);
        cita.setFechaCreacion(LocalDateTime.now());

        return repository.save(cita);
    }

    // =========================================================
    // CONSULTAS
    // =========================================================
    public List<Cita> listarTodas() { return repository.findAll(); }
    public Optional<Cita> buscarPorId(Long id) { return repository.findById(id); }
    public List<Cita> listarPorCliente(Long clienteId) {
        return repository.findByClienteIdOrderByFechaDescHoraDesc(clienteId);
    }
    public List<Cita> listarPorTecnico(Long tecnicoId) {
        return repository.findByTecnicoIdOrderByFechaDescHoraDesc(tecnicoId);
    }
    public List<Cita> listarPorEstado(String estado) {
        return repository.findByEstadoOrderByFechaAscHoraAsc(estado);
    }

    // =========================================================
    // CANCELAR CITA
    // =========================================================
    public Cita cancelarCita(Long id) {
        Cita cita = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden cancelar citas en estado PENDIENTE. Estado actual: " + cita.getEstado());
        }
        cita.setEstado("CANCELADA");
        return repository.save(cita);
    }

    // =========================================================
    // TÉCNICO APRUEBA
    // =========================================================
    public Cita aprobarCita(Long citaId, Long tecnicoId) {
        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden aprobar citas en estado PENDIENTE. Estado actual: " + cita.getEstado());
        }
        cita.setTecnicoId(tecnicoId);
        cita.setEstado("ASIGNADA");
        return repository.save(cita);
    }

    // =========================================================
    // REPROGRAMAR
    // =========================================================
    public Cita proponerNuevaHora(Long citaId, Long tecnicoId,
                                  LocalDate fechaPropuesta, LocalTime horaPropuesta,
                                  String motivoReprogramacion) {
        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden reprogramar citas en estado PENDIENTE. Estado actual: " + cita.getEstado());
        }
        if (fechaPropuesta == null || horaPropuesta == null)
            throw new RuntimeException("Debes indicar la nueva fecha y hora.");

        // 🔒 Validar nueva fecha no pasada
        LocalDate hoy = LocalDate.now();
        if (fechaPropuesta.isBefore(hoy)) {
            throw new RuntimeException("La nueva fecha no puede ser anterior a hoy.");
        }

        // 🔒 Si es hoy, la hora debe ser futura
        if (fechaPropuesta.isEqual(hoy)) {
            LocalTime ahora = LocalTime.now();
            if (horaPropuesta.isBefore(ahora)) {
                throw new RuntimeException("La hora propuesta ya pasó. Elige una hora futura.");
            }
        }

        cita.setTecnicoId(tecnicoId);
        cita.setFechaPropuesta(fechaPropuesta);
        cita.setHoraPropuesta(horaPropuesta);
        cita.setMotivoReprogramacion(motivoReprogramacion);
        cita.setEstado("REPROGRAMACION_PENDIENTE");
        return repository.save(cita);
    }

    // =========================================================
    // CLIENTE ACEPTA REPROGRAMACIÓN
    // =========================================================
    public Cita aceptarReprogramacion(Long citaId) {
        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"REPROGRAMACION_PENDIENTE".equals(cita.getEstado()))
            throw new RuntimeException("Esta cita no tiene una reprogramación pendiente.");

        cita.setFecha(cita.getFechaPropuesta());
        cita.setHora(cita.getHoraPropuesta());
        cita.setFechaPropuesta(null);
        cita.setHoraPropuesta(null);
        cita.setMotivoReprogramacion(null);
        cita.setEstado("ASIGNADA");
        return repository.save(cita);
    }

    // =========================================================
    // CLIENTE RECHAZA REPROGRAMACIÓN
    // =========================================================
    public Cita rechazarReprogramacion(Long citaId) {
        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"REPROGRAMACION_PENDIENTE".equals(cita.getEstado()))
            throw new RuntimeException("Esta cita no tiene una reprogramación pendiente.");

        cita.setTecnicoId(null);
        cita.setFechaPropuesta(null);
        cita.setHoraPropuesta(null);
        cita.setMotivoReprogramacion(null);
        cita.setEstado("PENDIENTE");
        return repository.save(cita);
    }

    // =========================================================
    // TÉCNICO INICIA LA CITA
    // =========================================================
    public Cita iniciarCita(Long citaId, Long tecnicoId) {
        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"ASIGNADA".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden iniciar citas en estado ASIGNADA. Estado actual: " + cita.getEstado());
        }
        if (cita.getTecnicoId() == null || !cita.getTecnicoId().equals(tecnicoId))
            throw new RuntimeException("Esta cita no está asignada a este técnico.");

        cita.setEstado("EN_PROCESO");
        return repository.save(cita);
    }

    // =========================================================
    // TÉCNICO CANCELA INICIO
    // =========================================================
    public Cita cancelarInicio(Long citaId, Long tecnicoId) {
        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"EN_PROCESO".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se puede cancelar el inicio de citas EN_PROCESO. Estado actual: " + cita.getEstado());
        }
        if (cita.getTecnicoId() == null || !cita.getTecnicoId().equals(tecnicoId))
            throw new RuntimeException("Esta cita no está asignada a este técnico.");

        cita.setEstado("ASIGNADA");
        return repository.save(cita);
    }

    // =========================================================
    // TÉCNICO COMPLETA LA CITA
    // =========================================================
    public Cita completarCita(Long citaId, Long tecnicoId,
                              BigDecimal manoDeObra,
                              Integer garantiaServicioDias,
                              String observacionesGarantia,
                              String diagnostico,
                              String informe) {

        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"EN_PROCESO".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden completar citas EN_PROCESO. Estado actual: " + cita.getEstado());
        }
        if (cita.getTecnicoId() == null || !cita.getTecnicoId().equals(tecnicoId))
            throw new RuntimeException("Esta cita no está asignada a este técnico.");

        if (manoDeObra == null || manoDeObra.compareTo(BigDecimal.ZERO) < 0)
            throw new RuntimeException("La mano de obra debe ser mayor o igual a cero.");
        if (garantiaServicioDias == null || garantiaServicioDias < 0)
            throw new RuntimeException("Los días de garantía deben ser mayores o iguales a cero.");

        // Recalcular total de repuestos aprobados antes de completar
        recalcularTotalRepuestos(citaId);
        cita = repository.findById(citaId).orElseThrow();

        BigDecimal totalRepuestos = cita.getTotalRepuestos() != null
                ? cita.getTotalRepuestos()
                : BigDecimal.ZERO;

        BigDecimal total = manoDeObra.add(totalRepuestos);

        LocalDate hoy = LocalDate.now();
        LocalDate finGarantia = hoy.plusDays(garantiaServicioDias);

        cita.setManoDeObra(manoDeObra);
        cita.setTotal(total);
        cita.setGarantiaServicioDias(garantiaServicioDias);
        cita.setObservacionesGarantia(observacionesGarantia);
        cita.setDiagnostico(diagnostico);
        cita.setInforme(informe);
        cita.setFechaCompletada(hoy);
        cita.setFechaFinGarantia(finGarantia);
        cita.setEstado("COMPLETADA");

        return repository.save(cita);
    }

    // =========================================================
    // CLIENTE PAGA
    // =========================================================
    public Cita pagarCita(Long citaId, String metodo) {

        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"COMPLETADA".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden pagar citas COMPLETADAS. Estado actual: " + cita.getEstado());
        }
        if (Boolean.TRUE.equals(cita.getPagado())) {
            throw new RuntimeException("Esta cita ya fue pagada.");
        }
        if (metodo == null ||
            (!metodo.equals("EFECTIVO") &&
             !metodo.equals("NEQUI") &&
             !metodo.equals("TARJETA") &&
             !metodo.equals("PSE"))) {
            throw new RuntimeException("Método de pago inválido.");
        }

        String nombreCliente = "Cliente";
        if (cita.getClienteId() != null) {
            var opt = usuarioRepository.findById(cita.getClienteId().intValue());
            if (opt.isPresent()) nombreCliente = opt.get().getNombre();
        }

        Pago pago = new Pago();
        pago.setCitaId(cita.getId());
        pago.setNombreCliente(nombreCliente);
        pago.setPrecio(cita.getTotal());
        pago.setFecha(LocalDateTime.now());
        pago.setMetodo(metodo);
        pagoRepository.save(pago);

        cita.setPagado(true);
        return repository.save(cita);
    }

    // =========================================================
    // AGREGAR REPUESTO DEL INVENTARIO
    // =========================================================
    public CitaRepuesto agregarRepuesto(Long citaId, Long tecnicoId,
                                        Long repuestoId, Integer cantidad) {

        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"EN_PROCESO".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden agregar repuestos a citas EN_PROCESO. Estado actual: " + cita.getEstado());
        }
        if (cita.getTecnicoId() == null || !cita.getTecnicoId().equals(tecnicoId)) {
            throw new RuntimeException("Esta cita no está asignada a este técnico.");
        }

        Repuesto repuesto = repuestoRepository.findById(repuestoId)
                .orElseThrow(() -> new RuntimeException("Repuesto no encontrado."));

        if (!"DISPONIBLE".equals(repuesto.getEstado())) {
            throw new RuntimeException("El repuesto no está disponible.");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero.");
        }

        BigDecimal precioUnitario = repuesto.getPrecio();
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        CitaRepuesto cr = new CitaRepuesto();
        cr.setCitaId(citaId);
        cr.setRepuestoId(repuestoId);
        cr.setCantidad(cantidad);
        cr.setPrecioUnitario(precioUnitario);
        cr.setSubtotal(subtotal);
        cr.setAprobadoPorCliente(false);
        cr.setEstado("PENDIENTE");

        return citaRepuestoRepository.save(cr);
    }

    // =========================================================
    // AGREGAR REPUESTO MANUAL
    // =========================================================
    public CitaRepuesto agregarRepuestoManual(Long citaId, Long tecnicoId,
                                              String nombre,
                                              BigDecimal precioUnitario,
                                              Integer cantidad,
                                              Boolean llevaGarantia,
                                              Integer garantiaDias) {

        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        if (!"EN_PROCESO".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden agregar repuestos a citas EN_PROCESO. Estado actual: " + cita.getEstado());
        }
        if (cita.getTecnicoId() == null || !cita.getTecnicoId().equals(tecnicoId)) {
            throw new RuntimeException("Esta cita no está asignada a este técnico.");
        }

        if (nombre == null || nombre.isBlank())
            throw new RuntimeException("El nombre del repuesto es obligatorio.");
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0)
            throw new RuntimeException("El precio debe ser mayor o igual a cero.");
        if (cantidad == null || cantidad <= 0)
            throw new RuntimeException("La cantidad debe ser mayor a cero.");

        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        CitaRepuesto cr = new CitaRepuesto();
        cr.setCitaId(citaId);
        cr.setRepuestoId(null);
        cr.setNombreManual(nombre);
        cr.setLlevaGarantiaManual(llevaGarantia != null ? llevaGarantia : false);
        cr.setGarantiaDiasManual(garantiaDias);
        cr.setCantidad(cantidad);
        cr.setPrecioUnitario(precioUnitario);
        cr.setSubtotal(subtotal);

        // Los repuestos manuales se aprueban automáticamente
        cr.setAprobadoPorCliente(true);
        cr.setEstado("APROBADO");
        cr.setFechaAprobacion(LocalDateTime.now());

        CitaRepuesto guardado = citaRepuestoRepository.save(cr);
        recalcularTotalRepuestos(citaId);
        return guardado;
    }

    // =========================================================
    // LISTAR REPUESTOS DE UNA CITA
    // =========================================================
    public List<CitaRepuesto> listarRepuestosDeCita(Long citaId) {
        return citaRepuestoRepository.findByCitaIdOrderByIdAsc(citaId);
    }

    // =========================================================
    // CLIENTE APRUEBA UN REPUESTO
    // =========================================================
    public CitaRepuesto aprobarRepuesto(Long citaRepuestoId) {
        CitaRepuesto cr = citaRepuestoRepository.findById(citaRepuestoId)
                .orElseThrow(() -> new RuntimeException("Repuesto de cita no encontrado."));

        if (!"PENDIENTE".equals(cr.getEstado())) {
            throw new RuntimeException("Este repuesto ya fue aprobado o rechazado.");
        }

        cr.setAprobadoPorCliente(true);
        cr.setEstado("APROBADO");
        cr.setFechaAprobacion(LocalDateTime.now());

        CitaRepuesto guardado = citaRepuestoRepository.save(cr);
        recalcularTotalRepuestos(cr.getCitaId());
        return guardado;
    }

    // =========================================================
    // CLIENTE RECHAZA UN REPUESTO
    // =========================================================
    public CitaRepuesto rechazarRepuesto(Long citaRepuestoId) {
        CitaRepuesto cr = citaRepuestoRepository.findById(citaRepuestoId)
                .orElseThrow(() -> new RuntimeException("Repuesto de cita no encontrado."));

        if (!"PENDIENTE".equals(cr.getEstado())) {
            throw new RuntimeException("Este repuesto ya fue aprobado o rechazado.");
        }

        cr.setAprobadoPorCliente(false);
        cr.setEstado("RECHAZADO");
        cr.setFechaRechazo(LocalDateTime.now());

        CitaRepuesto guardado = citaRepuestoRepository.save(cr);
        recalcularTotalRepuestos(cr.getCitaId());
        return guardado;
    }

    // =========================================================
    // RECALCULAR TOTAL DE REPUESTOS APROBADOS
    // =========================================================
    public void recalcularTotalRepuestos(Long citaId) {
        List<CitaRepuesto> aprobados =
                citaRepuestoRepository.findByCitaIdAndEstadoOrderByIdAsc(citaId, "APROBADO");

        BigDecimal suma = aprobados.stream()
                .map(CitaRepuesto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        cita.setTotalRepuestos(suma);
        if (cita.getManoDeObra() != null) {
            cita.setTotal(cita.getManoDeObra().add(suma));
        }
        repository.save(cita);
    }

    // =========================================================
    // ✅ ADMIN: LISTAR CITAS CON FILTROS
    // ---------------------------------------------------------
    // Todos los filtros son opcionales (si es null o vacío, se ignora).
    // =========================================================
    public List<Cita> listarCitasConFiltros(String estado, Long tecnicoId,
                                            Long clienteId,
                                            LocalDate desde, LocalDate hasta) {

        return repository.findAll().stream()
                // Filtro por estado
                .filter(c -> estado == null || estado.isBlank()
                        || estado.equalsIgnoreCase(c.getEstado()))
                // Filtro por técnico
                .filter(c -> tecnicoId == null
                        || (c.getTecnicoId() != null
                            && c.getTecnicoId().equals(tecnicoId)))
                // Filtro por cliente
                .filter(c -> clienteId == null
                        || (c.getClienteId() != null
                            && c.getClienteId().equals(clienteId)))
                // Filtro por rango de fechas
                .filter(c -> desde == null
                        || (c.getFecha() != null && !c.getFecha().isBefore(desde)))
                .filter(c -> hasta == null
                        || (c.getFecha() != null && !c.getFecha().isAfter(hasta)))
                // Ordenar por fecha descendente
                .sorted((a, b) -> {
                    if (a.getFecha() == null || b.getFecha() == null) return 0;
                    return b.getFecha().compareTo(a.getFecha());
                })
                .toList();
    }

    // =========================================================
    // ✅ ADMIN: REASIGNAR CITA A OTRO TÉCNICO
    // ---------------------------------------------------------
    // Reglas:
    //   - Solo se puede reasignar si está PENDIENTE, ASIGNADA
    //     o REPROGRAMACION_PENDIENTE.
    //   - El nuevo técnico debe existir, tener rol TECNICO y estar ACTIVO.
    //   - Solo cambia el tecnico_id. El estado no se toca.
    // =========================================================
    public Cita reasignarCita(Long citaId, Long nuevoTecnicoId) {

        Cita cita = repository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));

        // Validar estado
        String estado = cita.getEstado();
        if (!"PENDIENTE".equals(estado)
                && !"ASIGNADA".equals(estado)
                && !"REPROGRAMACION_PENDIENTE".equals(estado)) {
            throw new RuntimeException(
                "Solo se pueden reasignar citas en estado PENDIENTE, ASIGNADA " +
                "o REPROGRAMACION_PENDIENTE. Estado actual: " + estado);
        }

        // Validar el nuevo técnico
        Usuario nuevoTecnico = usuarioRepository.findById(nuevoTecnicoId.intValue())
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado."));

        if (!"TECNICO".equals(nuevoTecnico.getRol())) {
            throw new RuntimeException("El usuario seleccionado no es un técnico.");
        }
        if (!"ACTIVO".equals(nuevoTecnico.getEstado())) {
            throw new RuntimeException("El técnico seleccionado no está activo.");
        }

        // Reasignar
        cita.setTecnicoId(nuevoTecnicoId);
        return repository.save(cita);
    }

    // =========================================================
    // CONVERTIR Cita → CitaResponse
    // =========================================================
    public CitaResponse toResponse(Cita cita) {
        CitaResponse r = new CitaResponse();

        r.setId(cita.getId());
        r.setClienteId(cita.getClienteId());
        r.setTecnicoId(cita.getTecnicoId());
        r.setFecha(cita.getFecha());
        r.setHora(cita.getHora());
        r.setFechaPropuesta(cita.getFechaPropuesta());
        r.setHoraPropuesta(cita.getHoraPropuesta());
        r.setMotivoReprogramacion(cita.getMotivoReprogramacion());
        r.setDireccionVisita(cita.getDireccionVisita());
        r.setLatitud(cita.getLatitud());
        r.setLongitud(cita.getLongitud());
        r.setDescripcion(cita.getDescripcion());
        r.setDiagnostico(cita.getDiagnostico());
        r.setInforme(cita.getInforme());
        r.setEstado(cita.getEstado());
        r.setManoDeObra(cita.getManoDeObra());
        r.setTotalRepuestos(cita.getTotalRepuestos());
        r.setTotal(cita.getTotal());
        r.setPagado(cita.getPagado());
        r.setGarantiaServicioDias(cita.getGarantiaServicioDias());
        r.setObservacionesGarantia(cita.getObservacionesGarantia());
        r.setFechaCompletada(cita.getFechaCompletada());
        r.setFechaFinGarantia(cita.getFechaFinGarantia());
        r.setFechaCreacion(cita.getFechaCreacion());

        // Enriquecer con datos del cliente
        if (cita.getClienteId() != null) {
            usuarioRepository.findById(cita.getClienteId().intValue())
                .ifPresent(u -> {
                    r.setClienteNombre(u.getNombre());
                    r.setClienteTelefono(u.getTelefono());
                    r.setClienteCorreo(u.getCorreo());
                });
        }
        // Enriquecer con datos del técnico
        if (cita.getTecnicoId() != null) {
            usuarioRepository.findById(cita.getTecnicoId().intValue())
                .ifPresent(u -> r.setTecnicoNombre(u.getNombre()));
        }

        return r;
    }

    public List<CitaResponse> toResponseList(List<Cita> citas) {
        return citas.stream().map(this::toResponse).toList();
    }
}
