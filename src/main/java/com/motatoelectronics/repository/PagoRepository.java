package com.motatoelectronics.repository;

import com.motatoelectronics.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /** Buscar el pago asociado a una cita. */
    Optional<Pago> findByCitaId(Long citaId);
}

