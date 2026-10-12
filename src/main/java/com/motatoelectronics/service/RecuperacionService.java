package com.motatoelectronics.service;

import com.motatoelectronics.entity.CodigoRecuperacion;
import com.motatoelectronics.entity.Usuario;
import com.motatoelectronics.repository.CodigoRecuperacionRepository;
import com.motatoelectronics.repository.UsuarioRepository;
import com.motatoelectronics.util.PasswordValidator;  // ← Importante
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class RecuperacionService {

    @Autowired
    private CodigoRecuperacionRepository codigoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private static final int MINUTOS_EXPIRACION = 15;

    // =========================================================
    // GENERAR CÓDIGO DE 6 DÍGITOS
    // =========================================================
    private String generarCodigo() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }

    // =========================================================
    // SOLICITAR CÓDIGO DE RECUPERACIÓN
    // =========================================================
    public void solicitarRecuperacion(String correo) {

        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("No existe una cuenta con ese correo.");
        }

        String codigo = generarCodigo();
        CodigoRecuperacion cr = new CodigoRecuperacion();
        cr.setCorreo(correo);
        cr.setCodigo(codigo);
        cr.setTipo("RECUPERACION");
        cr.setFechaCreacion(LocalDateTime.now());
        cr.setFechaExpiracion(LocalDateTime.now().plusMinutes(MINUTOS_EXPIRACION));
        cr.setUsado(false);
        codigoRepo.save(cr);

        emailService.enviarCodigo(
                correo,
                codigo,
                "Recuperación de contraseña — Motato Electronics",
                "Recupera tu contraseña"
        );
    }

    // =========================================================
    // VALIDAR CÓDIGO Y CAMBIAR CONTRASEÑA
    // ---------------------------------------------------------
    // Ahora valida la política de contraseñas.
    // =========================================================
    public void validarYCambiarPassword(String correo, String codigo, String nuevaPassword) {

        Optional<CodigoRecuperacion> crOpt =
                codigoRepo.findFirstByCorreoAndTipoAndUsadoFalseOrderByFechaCreacionDesc(
                        correo, "RECUPERACION");

        if (crOpt.isEmpty()) {
            throw new RuntimeException("No hay un código activo para este correo.");
        }
        CodigoRecuperacion cr = crOpt.get();

        if (LocalDateTime.now().isAfter(cr.getFechaExpiracion())) {
            throw new RuntimeException("El código expiró. Solicita uno nuevo.");
        }

        if (!cr.getCodigo().equals(codigo)) {
            throw new RuntimeException("El código es incorrecto.");
        }

        // 🔒 Validar política de contraseñas
        PasswordValidator.validar(nuevaPassword);

        Usuario usuario = usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepo.save(usuario);

        cr.setUsado(true);
        codigoRepo.save(cr);
    }

    // =========================================================
    // SOLICITAR CÓDIGO PARA REGISTRO
    // =========================================================
    public void solicitarCodigoRegistro(String correo) {

        if (usuarioRepo.existsByCorreo(correo)) {
            throw new RuntimeException("Ese correo ya está registrado. Inicia sesión.");
        }

        String codigo = generarCodigo();
        CodigoRecuperacion cr = new CodigoRecuperacion();
        cr.setCorreo(correo);
        cr.setCodigo(codigo);
        cr.setTipo("REGISTRO");
        cr.setFechaCreacion(LocalDateTime.now());
        cr.setFechaExpiracion(LocalDateTime.now().plusMinutes(MINUTOS_EXPIRACION));
        cr.setUsado(false);
        codigoRepo.save(cr);

        emailService.enviarCodigo(
                correo,
                codigo,
                "Verifica tu correo — Motato Electronics",
                "Verifica tu correo"
        );
    }

    // =========================================================
    // VALIDAR CÓDIGO DE REGISTRO
    // =========================================================
    public boolean validarCodigoRegistro(String correo, String codigo) {

        Optional<CodigoRecuperacion> crOpt =
                codigoRepo.findFirstByCorreoAndTipoAndUsadoFalseOrderByFechaCreacionDesc(
                        correo, "REGISTRO");

        if (crOpt.isEmpty()) {
            throw new RuntimeException("No hay un código activo para este correo.");
        }
        CodigoRecuperacion cr = crOpt.get();

        if (LocalDateTime.now().isAfter(cr.getFechaExpiracion())) {
            throw new RuntimeException("El código expiró. Solicita uno nuevo.");
        }

        if (!cr.getCodigo().equals(codigo)) {
            throw new RuntimeException("El código es incorrecto.");
        }

        cr.setUsado(true);
        codigoRepo.save(cr);

        return true;
    }
}
