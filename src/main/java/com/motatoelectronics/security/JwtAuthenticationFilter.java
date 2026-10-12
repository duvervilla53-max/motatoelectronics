package com.motatoelectronics.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * ============================================================
 * FILTRO: JwtAuthenticationFilter
 * ============================================================
 * Se ejecuta ANTES de cada petición HTTP.
 *
 * Función:
 *   1. Mira si la petición trae un header
 *      "Authorization: Bearer <token>".
 *   2. Si lo trae, valida el token.
 *   3. Si el token es válido, marca la petición como autenticada
 *      con el correo y el rol del usuario.
 *   4. Si no lo trae o no es válido, deja pasar la petición
 *      sin autenticación (Spring la rechazará si la ruta es privada).
 * ============================================================
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1) Leer el header Authorization
        String header = request.getHeader("Authorization");

        // 2) Si no hay header o no empieza con "Bearer ", seguir sin autenticar
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Extraer el token (quitando "Bearer " del inicio)
        String token = header.substring(7);

        // 4) Validar el token
        if (jwtService.esValido(token)) {

            String correo = jwtService.extraerCorreo(token);
            String rol = jwtService.extraerRol(token);

            // 5) Crear la autenticación con el rol del usuario
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            correo,
                            null,
                            Collections.singletonList(
                                    new SimpleGrantedAuthority("ROLE_" + rol)
                            )
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // 6) Marcar la petición como autenticada
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7) Continuar con la petición
        filterChain.doFilter(request, response);
    }
}
