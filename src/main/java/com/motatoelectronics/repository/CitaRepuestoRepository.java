package com.motatoelectronics.repository;

import com.motatoelectronics.entity.CitaRepuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepuestoRepository extends JpaRepository<CitaRepuesto, Long> {

    /** Lista los repuestos usados en una cita. */
    List<CitaRepuesto> findByCitaIdOrderByIdAsc(Long citaId);

    /** Lista solo los aprobados de una cita (para sumar al total). */
    List<CitaRepuesto> findByCitaIdAndEstadoOrderByIdAsc(Long citaId, String estado);
}

