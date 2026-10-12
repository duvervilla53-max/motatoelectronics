package com.motatoelectronics.service;

import com.motatoelectronics.entity.Usuario;
import com.motatoelectronics.repository.UsuarioRepository;
import com.motatoelectronics.util.PasswordValidator;  // ← Importante
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================================================
    // CONSULTAS
    // =========================================================
    public List<Usuario> listarUsuarios() { return repository.findAll(); }
    public Optional<Usuario> buscarPorId(Integer id) { return repository.findById(id); }

    // =========================================================
    // REGISTRO PÚBLICO
    // ---------------------------------------------------------
    // Ahora valida la política de contraseñas.
    // =========================================================
    public Usuario guardarUsuario(Usuario usuario) {

        if (repository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        if (repository.existsByCedula(usuario.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada.");
        }

        // 🔒 Validar política de contraseñas
        PasswordValidator.validar(usuario.getPassword());

        // Rol: solo CLIENTE o TECNICO
        String rol = usuario.getRol();
        if (rol == null || (!rol.equals("CLIENTE") && !rol.equals("TECNICO"))) {
            rol = "CLIENTE";
        }
        usuario.setRol(rol);

        // Estado según rol
        usuario.setEstado(rol.equals("TECNICO") ? "PENDIENTE" : "ACTIVO");

        // Hashear contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Fecha de creación
        usuario.setFechaCreacion(LocalDateTime.now());

        return repository.save(usuario);
    }

    // =========================================================
    // CREAR USUARIO DESDE EL ADMIN
    // =========================================================
    public Usuario crearPorAdmin(Usuario usuario) {

        if (repository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        if (repository.existsByCedula(usuario.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada.");
        }

        // 🔒 Validar política de contraseñas
        PasswordValidator.validar(usuario.getPassword());

        String rol = usuario.getRol();
        if (rol == null ||
            (!rol.equals("CLIENTE") && !rol.equals("TECNICO") && !rol.equals("ADMINISTRADOR"))) {
            throw new RuntimeException("Rol inválido.");
        }

        usuario.setEstado("ACTIVO");
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaCreacion(LocalDateTime.now());

        return repository.save(usuario);
    }

    // =========================================================
    // ACTUALIZAR USUARIO
    // ---------------------------------------------------------
    // Valida la contraseña SOLO si se envía una nueva.
    // =========================================================
    public Usuario actualizarUsuario(Integer id, Usuario datos) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        usuario.setNombre(datos.getNombre());
        usuario.setCedula(datos.getCedula());
        usuario.setCorreo(datos.getCorreo());
        usuario.setTelefono(datos.getTelefono());
        usuario.setDireccion(datos.getDireccion());

        if (datos.getRol() != null && !datos.getRol().isEmpty()) {
            usuario.setRol(datos.getRol());
        }
        if (datos.getEstado() != null && !datos.getEstado().isEmpty()) {
            usuario.setEstado(datos.getEstado());
        }

        // Si se cambia la contraseña, validar la política
        if (datos.getPassword() != null && !datos.getPassword().isEmpty()) {
            PasswordValidator.validar(datos.getPassword());
            usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        return repository.save(usuario);
    }

    // =========================================================
    // CAMBIAR ESTADO
    // =========================================================
    public Usuario cambiarEstado(Integer id, String nuevoEstado) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!nuevoEstado.equals("ACTIVO") &&
            !nuevoEstado.equals("INACTIVO") &&
            !nuevoEstado.equals("PENDIENTE")) {
            throw new RuntimeException("Estado inválido.");
        }

        usuario.setEstado(nuevoEstado);
        return repository.save(usuario);
    }

    // =========================================================
    // ELIMINAR
    // =========================================================
    public void eliminarUsuario(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado.");
        }
        repository.deleteById(id);
    }

    // =========================================================
    // LOGIN
    // =========================================================
    public Optional<Usuario> login(String correo, String password) {

        Optional<Usuario> usuarioOpt = repository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) return Optional.empty();

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return Optional.empty();
        }

        if ("INACTIVO".equals(usuario.getEstado())) {
            throw new RuntimeException("Tu cuenta está inactiva. Contacta al administrador.");
        }
        if ("PENDIENTE".equals(usuario.getEstado())) {
            throw new RuntimeException("Tu cuenta está pendiente de aprobación por el administrador.");
        }

        return Optional.of(usuario);
    }
}
