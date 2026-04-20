package com.dulzonSA.mantenimiento.repositories;

import com.dulzonSA.mantenimiento.models.Rol;
import com.dulzonSA.mantenimiento.models.enums.TipoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(TipoRol nombre);
}
