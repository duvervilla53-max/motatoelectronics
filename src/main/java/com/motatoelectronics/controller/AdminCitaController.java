package com.motatoelectronics.controller;

import com.motatoelectronics.dto.CitaResponse;
import com.motatoelectronics.entity.Cita;
import com.motatoelectronics.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================
 * CONTROLADOR: AdminCitaController
 * ============================================================
 * Endpoints exclusivos del panel de administración.
 * Requieren rol ADMINISTRADOR.
 *
 * URL base: /api/admin/citas
 * ============================================================
 */
@RestController
@RequestMapping("/api/admin/citas")
@CrossOrigin(origins = "*")
public class AdminCitaController {

    @Autowired
    private CitaService service;

    // =========================================================
    // LISTAR CITAS CON FILTROS
    // GET /api/admin/citas?estado=X&tecnicoId=Y&clienteId=Z&desde=...&hasta=...
    // Todos los filtros son opcionales
    // =========================================================
    @GetMapping
    public List<CitaResponse> listarConFiltros(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {

        LocalDate d = (desde != null && !desde.isBlank()) ? LocalDate.parse(desde) : null;
        LocalDate h = (hasta != null && !hasta.isBlank()) ? LocalDate.parse(hasta) : null;

        List<Cita> citas = service.listarCitasConFiltros(estado, tecnicoId, clienteId, d, h);
        return service.toResponseList(citas);
    }

    // =========================================================
    // REASIGNAR CITA A OTRO TÉCNICO
    // PUT /api/admin/citas/{citaId}/reasignar/{nuevoTecnicoId}
    // =========================================================
    @PutMapping("/{citaId}/reasignar/{nuevoTecnicoId}")
    public ResponseEntity<?> reasignar(@PathVariable Long citaId,
                                       @PathVariable Long nuevoTecnicoId) {
        try {
            Cita c = service.reasignarCita(citaId, nuevoTecnicoId);
            return ResponseEntity.ok(service.toResponse(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
