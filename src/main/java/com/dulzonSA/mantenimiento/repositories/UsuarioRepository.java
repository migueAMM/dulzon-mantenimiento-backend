package com.dulzonSA.mantenimiento.repositories;
import com.dulzonSA.mantenimiento.models.Usuario;
import com.dulzonSA.mantenimiento.models.enums.TipoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol_Nombre(TipoRol rol);
    boolean existsByEmail(String email);
}
