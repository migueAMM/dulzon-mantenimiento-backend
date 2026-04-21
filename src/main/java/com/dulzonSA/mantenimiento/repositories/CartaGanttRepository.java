package com.dulzonSA.mantenimiento.repositories;

import com.dulzonSA.mantenimiento.models.CartaGantt;
import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface CartaGanttRepository extends JpaRepository<CartaGantt, Long> {

    List<CartaGantt> findByEstado(EstadoMantenimiento estado);

    List<CartaGantt> findByMaquina_Id(Long maquinaId);

    List<CartaGantt> findByFechaProgramadaBetween(LocalDate desde, LocalDate hasta);

    @Query("SELECT c FROM CartaGantt c WHERE c.estado = 'EN_PROCESO' ORDER BY c.fechaInicioReal DESC")
    List<CartaGantt> findEnProceso();

    @Query("SELECT c FROM CartaGantt c WHERE c.fechaProgramada = :hoy AND c.estado = 'PROGRAMADO'")
    List<CartaGantt> findProgramadasHoy(@Param("hoy") LocalDate hoy);
}
