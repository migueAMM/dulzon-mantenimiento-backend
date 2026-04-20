package com.dulzonSA.mantenimiento.repositories;

import com.dulzonSA.mantenimiento.models.Observacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservacionRepository extends JpaRepository<Observacion, Long> {
    List<Observacion> findByActividad_IdOrderByFechaHoraAsc(Long actividadId);
    List<Observacion> findByCartaGantt_IdOrderByFechaHoraAsc(Long cartaGanttId);
}
