package com.motatoelectronics.repository;

import com.motatoelectronics.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================
 * REPOSITORIO: UsuarioRepository
 * ============================================================
 *
 * Proporciona acceso a la tabla "usuarios" sin escribir SQL.
 * Spring Data JPA genera la implementación automáticamente.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /** Busca un usuario por correo (usado en login y validaciones). */
    Optional<Usuario> findByCorreo(String correo);

    /** Busca un usuario por cédula (para evitar duplicados). */
    Optional<Usuario> findByCedula(String cedula);

    /** Verifica si ya existe un correo registrado. */
    boolean existsByCorreo(String correo);

    /** Verifica si ya existe una cédula registrada. */
    boolean existsByCedula(String cedula);
}