package com.dulzonSA.mantenimiento.repositories;
import com.dulzonSA.mantenimiento.models.ActividadMantenimiento;
import com.dulzonSA.mantenimiento.models.enums.EstadoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ActividadMantenimientoRepository extends JpaRepository<ActividadMantenimiento, Long> {
    List<ActividadMantenimiento> findByCartaGantt_IdOrderByOrdenAsc(Long cartaGanttId);
    List<ActividadMantenimiento> findByCartaGantt_IdAndEstado(Long cartaGanttId, EstadoActividad estado);
    Optional<ActividadMantenimiento> findFirstByCartaGantt_IdAndEstadoOrderByOrdenAsc(Long cartaGanttId, EstadoActividad estado);
}
