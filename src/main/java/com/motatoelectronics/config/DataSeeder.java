package com.motatoelectronics.config;

import com.motatoelectronics.entity.Usuario;
import com.motatoelectronics.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ============================================================
 * SEEDER: DataSeeder
 * ============================================================
 *
 * Se ejecuta automáticamente al arrancar la aplicación.
 *
 * Su única tarea es garantizar que exista al menos un
 * ADMINISTRADOR inicial en la base de datos.
 *
 * Si ya existe, no hace nada.
 * ============================================================
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ADMIN_CORREO   = "admin@motato.com";
    private static final String ADMIN_PASSWORD = "Admin123*";

    @Override
    public void run(String... args) {

        // Si ya existe el admin, no hacemos nada
        if (repository.findByCorreo(ADMIN_CORREO).isPresent()) {
            System.out.println("✅ Administrador ya existe. No se crea de nuevo.");
            return;
        }

        // Construir el usuario administrador
        Usuario admin = new Usuario();
        admin.setNombre("Administrador General");
        admin.setCedula("0000000000");
        admin.setCorreo(ADMIN_CORREO);
        admin.setTelefono("0000000000");
        admin.setDireccion("Oficina principal Motato Electronics");
        admin.setRol("ADMINISTRADOR");
        admin.setEstado("ACTIVO");
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setFechaCreacion(LocalDateTime.now());

        repository.save(admin);

        // Avisar por consola
        System.out.println("====================================================");
        System.out.println("🔐 ADMINISTRADOR CREADO");
        System.out.println("   Correo:     " + ADMIN_CORREO);
        System.out.println("   Contraseña: " + ADMIN_PASSWORD);
        System.out.println("   ⚠️  Cámbiala después de iniciar sesión.");
        System.out.println("====================================================");
    }
}