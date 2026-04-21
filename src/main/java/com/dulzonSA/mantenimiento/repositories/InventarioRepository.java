package com.dulzonSA.mantenimiento.repositories;
import com.dulzonSA.mantenimiento.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByCartaGantt_Id(Long cartaGanttId);
}
