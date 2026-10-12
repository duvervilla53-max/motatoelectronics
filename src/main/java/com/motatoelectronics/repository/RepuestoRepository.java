package com.motatoelectronics.repository;

import com.motatoelectronics.entity.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {

    /** Lista los repuestos disponibles en inventario. */
    List<Repuesto> findByEstadoOrderByNombreAsc(String estado);
}
