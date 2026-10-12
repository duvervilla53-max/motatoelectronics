package com.motatoelectronics.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * ============================================================
 * CONFIGURACIÓN: SecurityConfig
 * ============================================================
 * Define qué rutas son públicas y cuáles requieren token.
 *
 * IMPORTANTE: Los archivos estáticos (HTML, CSS, JS, IMG)
 * son públicos. Solo las APIs privadas requieren token.
 * ============================================================
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Desactivar CSRF (usamos JWT)
            .csrf(csrf -> csrf.disable())

            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Sin sesiones de servidor
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configurar autorización
            .authorizeHttpRequests(auth -> auth

                // ============================================
                // ARCHIVOS ESTÁTICOS (HTML, CSS, JS, IMG)
                // Los dejamos TODOS públicos
                // ============================================
                .requestMatchers(
                        "/", "/index.html", "/citas.html", "/registro.html",
                        "/cliente.html", "/tecnico.html", "/admin.html",
                        "/login.html", "/servicios.html",
                        "/css/**", "/js/**", "/img/**",
                        "/favicon.ico", "/error",
                        "/*.html", "/*.css", "/*.js"
                ).permitAll()

                // ============================================
                // RUTAS PÚBLICAS DE LA API
                // ============================================
                .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/recuperar/**",
                        "/api/auth/registro/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/repuestos/**").permitAll()

                // ============================================
                // RUTAS SOLO ADMIN
                // ============================================
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")

                // ============================================
                // TODO LO DEMÁS REQUIERE TOKEN
                // ============================================
                .anyRequest().authenticated()
            )

            // Filtro JWT
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
