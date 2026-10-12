package com.motatoelectronics.reportes;

import com.motatoelectronics.entity.Cita;
import com.motatoelectronics.entity.Pago;
import com.motatoelectronics.entity.Usuario;
import com.motatoelectronics.repository.CitaRepository;
import com.motatoelectronics.repository.PagoRepository;
import com.motatoelectronics.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * SERVICIO: ReporteService
 * ============================================================
 * Genera los datos para los informes.
 * ============================================================
 */
@Service
public class ReporteService {

    @Autowired
    private CitaRepository citaRepo;

    @Autowired
    private PagoRepository pagoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    // =========================================================
    // CITAS POR RANGO DE FECHAS
    // =========================================================
    public List<Cita> obtenerCitasPorRango(LocalDate desde, LocalDate hasta) {
        return citaRepo.findAll().stream()
                .filter(c -> c.getFecha() != null)
                .filter(c -> !c.getFecha().isBefore(desde) && !c.getFecha().isAfter(hasta))
                .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                .toList();
    }

    // =========================================================
    // RESUMEN FINANCIERO POR RANGO
    // =========================================================
    public Map<String, Object> obtenerResumenFinanciero(LocalDate desde, LocalDate hasta) {

        List<Cita> citas = obtenerCitasPorRango(desde, hasta).stream()
                .filter(c -> "COMPLETADA".equals(c.getEstado()))
                .toList();

        BigDecimal totalManoObra = BigDecimal.ZERO;
        BigDecimal totalRepuestos = BigDecimal.ZERO;
        BigDecimal totalGeneral = BigDecimal.ZERO;
        int citasPagadas = 0;
        int citasPendientesPago = 0;

        for (Cita c : citas) {
            if (c.getManoDeObra() != null) totalManoObra = totalManoObra.add(c.getManoDeObra());
            if (c.getTotalRepuestos() != null) totalRepuestos = totalRepuestos.add(c.getTotalRepuestos());
            if (c.getTotal() != null) totalGeneral = totalGeneral.add(c.getTotal());

            if (Boolean.TRUE.equals(c.getPagado())) {
                citasPagadas++;
            } else {
                citasPendientesPago++;
            }
        }

        Map<String, BigDecimal> porMetodo = new HashMap<>();
        porMetodo.put("EFECTIVO", BigDecimal.ZERO);
        porMetodo.put("NEQUI", BigDecimal.ZERO);
        porMetodo.put("TARJETA", BigDecimal.ZERO);
        porMetodo.put("PSE", BigDecimal.ZERO);

        List<Pago> pagos = pagoRepo.findAll().stream()
                .filter(p -> p.getFecha() != null)
                .filter(p -> {
                    LocalDate f = p.getFecha().toLocalDate();
                    return !f.isBefore(desde) && !f.isAfter(hasta);
                })
                .toList();

        for (Pago p : pagos) {
            porMetodo.merge(p.getMetodo(), p.getPrecio(), BigDecimal::add);
        }

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("desde", desde);
        resumen.put("hasta", hasta);
        resumen.put("totalCitas", citas.size());
        resumen.put("citasPagadas", citasPagadas);
        resumen.put("citasPendientesPago", citasPendientesPago);
        resumen.put("totalManoObra", totalManoObra);
        resumen.put("totalRepuestos", totalRepuestos);
        resumen.put("totalGeneral", totalGeneral);
        resumen.put("ingresoEfectivo", porMetodo.get("EFECTIVO"));
        resumen.put("ingresoNequi", porMetodo.get("NEQUI"));
        resumen.put("ingresoTarjeta", porMetodo.get("TARJETA"));
        resumen.put("ingresoPSE", porMetodo.get("PSE"));
        resumen.put("cantidadPagos", pagos.size());

        return resumen;
    }

    // =========================================================
    // CUMPLIMIENTO DE TÉCNICOS
    // ---------------------------------------------------------
    // ✅ Tasa de cumplimiento = completadas / totalAsignadas
    // =========================================================
    public List<Map<String, Object>> obtenerCumplimientoTecnicos(LocalDate desde, LocalDate hasta) {

        List<Usuario> tecnicos = usuarioRepo.findAll().stream()
                .filter(u -> "TECNICO".equals(u.getRol()))
                .toList();

        List<Cita> citas = obtenerCitasPorRango(desde, hasta);

        return tecnicos.stream().map(t -> {

            // Convertir el Integer del técnico a Long para comparar
            Long tecnicoIdLong = t.getId().longValue();

            List<Cita> citasTecnico = citas.stream()
                    .filter(c -> c.getTecnicoId() != null
                            && c.getTecnicoId().equals(tecnicoIdLong))
                    .toList();

            long pendientes = citasTecnico.stream().filter(c -> "PENDIENTE".equals(c.getEstado())).count();
            long asignadas = citasTecnico.stream().filter(c -> "ASIGNADA".equals(c.getEstado())).count();
            long enProceso = citasTecnico.stream().filter(c -> "EN_PROCESO".equals(c.getEstado())).count();
            long completadas = citasTecnico.stream().filter(c -> "COMPLETADA".equals(c.getEstado())).count();
            long canceladas = citasTecnico.stream().filter(c -> "CANCELADA".equals(c.getEstado())).count();

            BigDecimal totalGenerado = citasTecnico.stream()
                    .filter(c -> "COMPLETADA".equals(c.getEstado()) && c.getTotal() != null)
                    .map(Cita::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // ============================================
            // ✅ CÁLCULO DE TASAS
            // ============================================

            // Total de citas que le fueron asignadas al técnico
            long totalAsignadas = citasTecnico.size();

            // Citas finalizadas (completadas + canceladas)
            long totalFinalizadas = completadas + canceladas;

            // 🎯 Tasa de cumplimiento = completadas / totalAsignadas
            // Mide el % del trabajo que aceptó y efectivamente terminó
            double tasaCumplimiento = totalAsignadas > 0
                    ? (completadas * 100.0 / totalAsignadas)
                    : 0.0;

            // 🎯 Tasa de éxito = completadas / finalizadas
            // Mide el % de las citas finalizadas que fueron exitosas
            double tasaExito = totalFinalizadas > 0
                    ? (completadas * 100.0 / totalFinalizadas)
                    : 0.0;

            // 🎯 Tasa de cancelación = canceladas / totalAsignadas
            double tasaCancelacion = totalAsignadas > 0
                    ? (canceladas * 100.0 / totalAsignadas)
                    : 0.0;

            Map<String, Object> fila = new HashMap<>();
            fila.put("tecnicoId", t.getId());
            fila.put("nombre", t.getNombre());
            fila.put("correo", t.getCorreo());
            fila.put("totalCitas", totalAsignadas);
            fila.put("pendientes", pendientes);
            fila.put("asignadas", asignadas);
            fila.put("enProceso", enProceso);
            fila.put("completadas", completadas);
            fila.put("canceladas", canceladas);
            fila.put("ingresosGenerados", totalGenerado);

            // Redondear a 1 decimal
            fila.put("tasaCumplimiento", Math.round(tasaCumplimiento * 10.0) / 10.0);
            fila.put("tasaExito", Math.round(tasaExito * 10.0) / 10.0);
            fila.put("tasaCancelacion", Math.round(tasaCancelacion * 10.0) / 10.0);

            return fila;
        }).toList();
    }

    // =========================================================
    // LISTADO DE USUARIOS
    // =========================================================
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepo.findAll();
    }
}

