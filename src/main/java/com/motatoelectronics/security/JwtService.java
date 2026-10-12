package com.motatoelectronics.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * ============================================================
 * SERVICIO: JwtService
 * ============================================================
 * Genera y valida tokens JWT.
 *
 * Un token JWT tiene 3 partes separadas por puntos:
 *   HEADER.PAYLOAD.SIGNATURE
 *
 *  - HEADER:     algoritmo usado para firmar.
 *  - PAYLOAD:    datos del usuario (correo, rol, id, exp).
 *  - SIGNATURE:  firma con clave secreta (evita que lo alteren).
 * ============================================================
 */
@Service
public class JwtService {

    /** Clave secreta leída desde application.properties. */
    @Value("${jwt.secret}")
    private String secret;

    /** Duración del token en milisegundos. */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Devuelve la clave criptográfica para firmar tokens.
     * Se genera a partir del String `secret`.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT con los datos del usuario.
     *
     * @param correo correo del usuario
     * @param rol    rol del usuario (CLIENTE / TECNICO / ADMINISTRADOR)
     * @param id     id del usuario
     * @return       token JWT firmado
     */
    public String generarToken(String correo, String rol, Integer id) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                .subject(correo)                             // correo como sujeto
                .claim("rol", rol)                           // rol
                .claim("id", id)                             // id
                .issuedAt(ahora)                             // fecha de creación
                .expiration(expiracion)                      // fecha de expiración
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae todos los claims (datos) de un token.
     * Si el token no es válido, lanza excepción.
     */
    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene el correo del usuario desde el token.
     */
    public String extraerCorreo(String token) {
        return extraerClaims(token).getSubject();
    }

    /**
     * Obtiene el rol del usuario desde el token.
     */
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    /**
     * Obtiene el id del usuario desde el token.
     */
    public Integer extraerId(String token) {
        return extraerClaims(token).get("id", Integer.class);
    }

    /**
     * Verifica si el token es válido (firma correcta y no expirado).
     */
    public boolean esValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

