package com.motatoelectronics.controller;

import com.motatoelectronics.dto.CitaResponse;
import com.motatoelectronics.entity.Cita;
import com.motatoelectronics.entity.CitaRepuesto;
import com.motatoelectronics.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================
 * CONTROLADOR: CitaController
 * ============================================================
 * Endpoints REST del módulo de citas.
 * URL base: /api/citas
 * ============================================================
 */
@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired
    private CitaService service;

    // =========================================================
    // CREAR CITA
    // =========================================================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Cita cita) {
        try {
            Cita nueva = service.crearCita(cita);
            return ResponseEntity.ok(service.toResponse(nueva));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<CitaResponse> listarTodas() {
        return service.toResponseList(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> buscarPorId(@PathVariable Long id) {
        Optional<Cita> cita = service.buscarPorId(id);
        return cita.map(c -> ResponseEntity.ok(service.toResponse(c)))
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public List<CitaResponse> listarPorCliente(@PathVariable Long clienteId) {
        return service.toResponseList(service.listarPorCliente(clienteId));
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public List<CitaResponse> listarPorTecnico(@PathVariable Long tecnicoId) {
        return service.toResponseList(service.listarPorTecnico(tecnicoId));
    }

    @GetMapping("/estado/{estado}")
    public List<CitaResponse> listarPorEstado(@PathVariable String estado) {
        return service.toResponseList(service.listarPorEstado(estado.toUpperCase()));
    }

    // =========================================================
    // CANCELAR CITA
    // =========================================================
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            Cita c = service.cancelarCita(id);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // TÉCNICO APRUEBA
    // =========================================================
    @PutMapping("/{citaId}/aprobar/{tecnicoId}")
    public ResponseEntity<?> aprobar(@PathVariable Long citaId, @PathVariable Long tecnicoId) {
        try {
            Cita c = service.aprobarCita(citaId, tecnicoId);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // REPROGRAMAR
    // =========================================================
    @PutMapping("/{citaId}/reprogramar")
    public ResponseEntity<?> reprogramar(@PathVariable Long citaId,
                                         @RequestBody Map<String, String> body) {
        try {
            Long tecnicoId = Long.parseLong(body.get("tecnicoId"));
            LocalDate fechaPropuesta = LocalDate.parse(body.get("fechaPropuesta"));
            LocalTime horaPropuesta = LocalTime.parse(body.get("horaPropuesta"));
            String motivo = body.get("motivoReprogramacion");
            Cita c = service.proponerNuevaHora(citaId, tecnicoId, fechaPropuesta, horaPropuesta, motivo);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // ACEPTAR / RECHAZAR REPROGRAMACIÓN
    // =========================================================
    @PutMapping("/{citaId}/aceptar-reprogramacion")
    public ResponseEntity<?> aceptarReprogramacion(@PathVariable Long citaId) {
        try {
            Cita c = service.aceptarReprogramacion(citaId);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{citaId}/rechazar-reprogramacion")
    public ResponseEntity<?> rechazarReprogramacion(@PathVariable Long citaId) {
        try {
            Cita c = service.rechazarReprogramacion(citaId);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // INICIAR / CANCELAR INICIO
    // =========================================================
    @PutMapping("/{citaId}/iniciar/{tecnicoId}")
    public ResponseEntity<?> iniciar(@PathVariable Long citaId, @PathVariable Long tecnicoId) {
        try {
            Cita c = service.iniciarCita(citaId, tecnicoId);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{citaId}/cancelar-inicio/{tecnicoId}")
    public ResponseEntity<?> cancelarInicio(@PathVariable Long citaId, @PathVariable Long tecnicoId) {
        try {
            Cita c = service.cancelarInicio(citaId, tecnicoId);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // COMPLETAR CITA
    // ---------------------------------------------------------
    // Ahora recibe diagnóstico e informe.
    // =========================================================
    @PutMapping("/{citaId}/completar")
    public ResponseEntity<?> completar(@PathVariable Long citaId,
                                       @RequestBody Map<String, Object> body) {
        try {
            Long tecnicoId = Long.parseLong(body.get("tecnicoId").toString());
            BigDecimal manoDeObra = new BigDecimal(body.get("manoDeObra").toString());
            Integer garantiaServicioDias = Integer.parseInt(body.get("garantiaServicioDias").toString());

            String observacionesGarantia = body.get("observacionesGarantia") != null
                    ? body.get("observacionesGarantia").toString() : null;
            String diagnostico = body.get("diagnostico") != null
                    ? body.get("diagnostico").toString() : null;
            String informe = body.get("informe") != null
                    ? body.get("informe").toString() : null;

            Cita c = service.completarCita(citaId, tecnicoId, manoDeObra,
                    garantiaServicioDias, observacionesGarantia,
                    diagnostico, informe);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // PAGAR CITA
    // =========================================================
    @PutMapping("/{citaId}/pagar")
    public ResponseEntity<?> pagar(@PathVariable Long citaId,
                                   @RequestBody Map<String, String> body) {
        try {
            String metodo = body.get("metodo");
            Cita c = service.pagarCita(citaId, metodo);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // AGREGAR REPUESTO DEL INVENTARIO
    // =========================================================
    @PostMapping("/{citaId}/repuestos")
    public ResponseEntity<?> agregarRepuesto(@PathVariable Long citaId,
                                             @RequestBody Map<String, Object> body) {
        try {
            Long tecnicoId = Long.parseLong(body.get("tecnicoId").toString());
            Long repuestoId = Long.parseLong(body.get("repuestoId").toString());
            Integer cantidad = Integer.parseInt(body.get("cantidad").toString());

            return ResponseEntity.ok(service.agregarRepuesto(citaId, tecnicoId, repuestoId, cantidad));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // AGREGAR REPUESTO MANUAL
    // =========================================================
    @PostMapping("/{citaId}/repuestos-manual")
    public ResponseEntity<?> agregarRepuestoManual(@PathVariable Long citaId,
                                                   @RequestBody Map<String, Object> body) {
        try {
            Long tecnicoId = Long.parseLong(body.get("tecnicoId").toString());
            String nombre = body.get("nombre").toString();
            BigDecimal precioUnitario = new BigDecimal(body.get("precioUnitario").toString());
            Integer cantidad = Integer.parseInt(body.get("cantidad").toString());

            Boolean llevaGarantia = body.get("llevaGarantia") != null
                    ? Boolean.parseBoolean(body.get("llevaGarantia").toString())
                    : false;

            Integer garantiaDias = null;
            if (body.get("garantiaDias") != null && !body.get("garantiaDias").toString().isEmpty()) {
                garantiaDias = Integer.parseInt(body.get("garantiaDias").toString());
            }

            return ResponseEntity.ok(service.agregarRepuestoManual(
                    citaId, tecnicoId, nombre, precioUnitario, cantidad, llevaGarantia, garantiaDias));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // LISTAR REPUESTOS DE UNA CITA
    // =========================================================
    @GetMapping("/{citaId}/repuestos")
    public List<CitaRepuesto> listarRepuestos(@PathVariable Long citaId) {
        return service.listarRepuestosDeCita(citaId);
    }

    // =========================================================
    // APROBAR / RECHAZAR REPUESTO
    // =========================================================
    @PutMapping("/repuestos/{citaRepuestoId}/aprobar")
    public ResponseEntity<?> aprobarRepuesto(@PathVariable Long citaRepuestoId) {
        try {
            return ResponseEntity.ok(service.aprobarRepuesto(citaRepuestoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/repuestos/{citaRepuestoId}/rechazar")
    public ResponseEntity<?> rechazarRepuesto(@PathVariable Long citaRepuestoId) {
        try {
            return ResponseEntity.ok(service.rechazarRepuesto(citaRepuestoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
