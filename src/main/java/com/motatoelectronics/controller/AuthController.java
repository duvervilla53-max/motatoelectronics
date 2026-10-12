package com.motatoelectronics.controller;

import com.motatoelectronics.dto.LoginRequest;
import com.motatoelectronics.dto.LoginResponse;
import com.motatoelectronics.entity.Usuario;
import com.motatoelectronics.security.JwtService;  // ← Importante
import com.motatoelectronics.service.RecuperacionService;
import com.motatoelectronics.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * ============================================================
 * CONTROLADOR: AuthController
 * ============================================================
 * Endpoints de autenticación:
 *   - Login (ahora devuelve token JWT)
 *   - Recuperación de contraseña
 *   - Registro por correo
 * ============================================================
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private RecuperacionService recuperacionService;

    // Servicio que genera y valida tokens JWT
    @Autowired
    private JwtService jwtService;

    // =========================================================
    // LOGIN (ahora devuelve token)
    // =========================================================
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            Optional<Usuario> usuarioOpt =
                    service.login(request.getCorreo(), request.getPassword());

            if (usuarioOpt.isPresent()) {
                Usuario u = usuarioOpt.get();

                // Generar el token JWT con los datos del usuario
                String token = jwtService.generarToken(
                        u.getCorreo(),
                        u.getRol(),
                        u.getId()
                );

                return new LoginResponse(
                        true,
                        "Ingreso exitoso. Bienvenido a Motato Electronics.",
                        u.getRol(),
                        u.getId(),
                        u.getNombre(),
                        u.getCorreo(),
                        token                    // ← el token va aquí
                );
            }
            return new LoginResponse(
                    false, "Correo o contraseña incorrectos.",
                    null, null, null, null
            );
        } catch (RuntimeException e) {
            return new LoginResponse(false, e.getMessage(), null, null, null, null);
        }
    }

    // =========================================================
    // RECUPERAR CONTRASEÑA — PASO 1: SOLICITAR CÓDIGO
    // =========================================================
    @PostMapping("/recuperar/solicitar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> body) {
        try {
            String correo = body.get("correo");
            if (correo == null || correo.isBlank()) {
                return ResponseEntity.badRequest().body("El correo es obligatorio.");
            }
            recuperacionService.solicitarRecuperacion(correo);
            return ResponseEntity.ok("Código enviado al correo " + correo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // RECUPERAR CONTRASEÑA — PASO 2: VALIDAR Y CAMBIAR
    // =========================================================
    @PostMapping("/recuperar/validar")
    public ResponseEntity<?> validarRecuperacion(@RequestBody Map<String, String> body) {
        try {
            String correo = body.get("correo");
            String codigo = body.get("codigo");
            String nuevaPassword = body.get("nuevaPassword");

            recuperacionService.validarYCambiarPassword(correo, codigo, nuevaPassword);
            return ResponseEntity.ok("Contraseña actualizada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // REGISTRO POR CORREO — PASO 1: SOLICITAR CÓDIGO
    // =========================================================
    @PostMapping("/registro/solicitar-codigo")
    public ResponseEntity<?> solicitarCodigoRegistro(@RequestBody Map<String, String> body) {
        try {
            String correo = body.get("correo");
            if (correo == null || correo.isBlank()) {
                return ResponseEntity.badRequest().body("El correo es obligatorio.");
            }
            recuperacionService.solicitarCodigoRegistro(correo);
            return ResponseEntity.ok("Código enviado al correo " + correo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================================================
    // REGISTRO POR CORREO — PASO 2: VERIFICAR CÓDIGO
    // =========================================================
    @PostMapping("/registro/verificar-codigo")
    public ResponseEntity<?> verificarCodigoRegistro(@RequestBody Map<String, String> body) {
        try {
            String correo = body.get("correo");
            String codigo = body.get("codigo");

            recuperacionService.validarCodigoRegistro(correo, codigo);
            return ResponseEntity.ok("Código válido. Continúa con el registro.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
