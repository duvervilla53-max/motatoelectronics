package com.motatoelectronics.controller;

import com.motatoelectronics.entity.Usuario;
import com.motatoelectronics.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * CONTROLADOR: UsuarioController
 * ============================================================
 *
 * Expone los endpoints REST para gestionar usuarios.
 * URL base: /api
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // =========================================================
    // LISTAR TODOS LOS USUARIOS
    // GET /api/usuarios
    // =========================================================
    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return service.listarUsuarios();
    }

    // =========================================================
    // BUSCAR POR ID
    // GET /api/usuarios/{id}
    // =========================================================
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = service.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // REGISTRO PÚBLICO
    // POST /api/usuarios
    // Solo permite CLIENTE o TECNICO.
    // =========================================================
    @PostMapping("/usuarios")
    public ResponseEntity<?> guardar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevo = service.guardarUsuario(usuario);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // CREAR USUARIO DESDE EL PANEL ADMIN
    // POST /api/admin/usuarios
    // Permite cualquier rol.
    // ⚠️ En Fase 3 se protegerá con JWT.
    // =========================================================
    @PostMapping("/admin/usuarios")
    public ResponseEntity<?> crearDesdeAdmin(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(service.crearPorAdmin(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // ACTUALIZAR USUARIO
    // PUT /api/usuarios/{id}
    // =========================================================
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id,
                                        @RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(service.actualizarUsuario(id, usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // CAMBIAR ESTADO (aprobar técnico, inactivar, reactivar)
    // PUT /api/admin/usuarios/{id}/estado?estado=ACTIVO
    // =========================================================
    @PutMapping("/admin/usuarios/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id,
                                           @RequestParam String estado) {
        try {
            return ResponseEntity.ok(service.cambiarEstado(id, estado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // ELIMINAR USUARIO
    // DELETE /api/usuarios/{id}
    // =========================================================
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            service.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}