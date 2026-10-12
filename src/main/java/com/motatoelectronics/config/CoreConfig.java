package com.motatoelectronics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ============================================================
 * CONFIGURACIÓN: CoreConfig
 * ============================================================
 *
 * Define beans centrales de la aplicación.
 * Aquí registramos el encriptador de contraseñas (BCrypt).
 */
@Configuration
public class CoreConfig {

    /**
     * Bean PasswordEncoder usando BCrypt.
     * Se inyecta en UsuarioService y DataSeeder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
