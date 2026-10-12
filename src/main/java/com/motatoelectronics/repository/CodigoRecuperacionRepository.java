package com.motatoelectronics.repository;

import com.motatoelectronics.entity.CodigoRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================
 * REPOSITORIO: CodigoRecuperacionRepository
 * ============================================================
 * Acceso a la tabla "codigos_recuperacion".
 */
@Repository
public interface CodigoRecuperacionRepository extends JpaRepository<CodigoRecuperacion, Long> {

    /**
     * Busca el último código activo (no usado) para un correo y tipo.
     * Se ordena por fecha de creación descendente para traer el más reciente.
     */
    Optional<CodigoRecuperacion> findFirstByCorreoAndTipoAndUsadoFalseOrderByFechaCreacionDesc(
            String correo, String tipo);
}
