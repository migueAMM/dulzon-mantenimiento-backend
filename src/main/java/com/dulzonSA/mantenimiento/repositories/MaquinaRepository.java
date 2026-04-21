package com.dulzonSA.mantenimiento.repositories;
import com.dulzonSA.mantenimiento.models.Maquina;
import com.dulzonSA.mantenimiento.models.enums.TipoMaquina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface MaquinaRepository extends JpaRepository<Maquina, Long> {
    List<Maquina> findByTipo(TipoMaquina tipo);
    Optional<Maquina> findByCodigoInterno(String codigoInterno);
}
