package com.motatoelectronics.repository;

import com.motatoelectronics.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /** Citas de un cliente (para panel cliente). */
    List<Cita> findByClienteIdOrderByFechaDescHoraDesc(Long clienteId);

    /** Citas asignadas a un técnico (para panel técnico). */
    List<Cita> findByTecnicoIdOrderByFechaDescHoraDesc(Long tecnicoId);

    /** Citas por estado (ej: PENDIENTE para el técnico, o todas para admin). */
    List<Cita> findByEstadoOrderByFechaAscHoraAsc(String estado);
}