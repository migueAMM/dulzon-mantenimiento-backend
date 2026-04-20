package com.dulzonSA.mantenimiento.repositories;

import com.dulzonSA.mantenimiento.models.Turno;
import com.dulzonSA.mantenimiento.models.enums.TipoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    Optional<Turno> findByNombre(TipoTurno nombre);
}
