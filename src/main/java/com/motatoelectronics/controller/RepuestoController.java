package com.motatoelectronics.controller;

import com.motatoelectronics.entity.Repuesto;
import com.motatoelectronics.repository.RepuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================
 * CONTROLADOR: RepuestoController
 * ============================================================
 * Endpoints REST para el inventario de repuestos.
 * URL base: /api/repuestos
 */
@RestController
@RequestMapping("/api/repuestos")
@CrossOrigin(origins = "*")
public class RepuestoController {

    @Autowired
    private RepuestoRepository repo;

    /** Lista todos los repuestos. */
    @GetMapping
    public List<Repuesto> listarTodos() {
        return repo.findAll();
    }

    /** Lista solo los que están en cierto estado (ej: DISPONIBLE). */
    @GetMapping("/estado/{estado}")
    public List<Repuesto> listarPorEstado(@PathVariable String estado) {
        return repo.findByEstadoOrderByNombreAsc(estado.toUpperCase());
    }

    /** Crea un nuevo repuesto en el inventario. */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Repuesto r) {
        try {
            if (r.getNombre() == null || r.getNombre().isBlank())
                throw new RuntimeException("El nombre es obligatorio.");
            if (r.getPrecio() == null)
                throw new RuntimeException("El precio es obligatorio.");

            // Valores por defecto
            if (r.getStock() == null) r.setStock(0);
            if (r.getLlevaGarantia() == null) r.setLlevaGarantia(false);
            if (r.getEstado() == null) r.setEstado("DISPONIBLE");
            r.setFechaCreacion(LocalDateTime.now());

            // Si lleva garantía pero no definieron días, poner 0
            if (Boolean.TRUE.equals(r.getLlevaGarantia()) && r.getGarantiaDias() == null) {
                r.setGarantiaDias(0);
            }

            return ResponseEntity.ok(repo.save(r));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
