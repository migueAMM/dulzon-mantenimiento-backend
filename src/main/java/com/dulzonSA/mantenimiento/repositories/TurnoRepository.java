package com.dulzonSA.mantenimiento.repositories;
import com.dulzonSA.mantenimiento.models.Turno;
import com.dulzonSA.mantenimiento.models.enums.TipoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    Optional<Turno> findByNombre(TipoTurno nombre);
}
