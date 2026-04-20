package com.dulzonSA.mantenimiento.repositories;

import com.dulzonSA.mantenimiento.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByCartaGantt_Id(Long cartaGanttId);
}
