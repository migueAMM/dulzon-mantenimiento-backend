package com.dulzonSA.mantenimiento.services;

import com.dulzonSA.mantenimiento.models.Rol;
import com.dulzonSA.mantenimiento.models.Usuario;
import com.dulzonSA.mantenimiento.models.enums.TipoRol;
import com.dulzonSA.mantenimiento.repositories.RolRepository;
import com.dulzonSA.mantenimiento.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Transactional
    public Usuario crearUsuario(String nombre, String email, String password, TipoRol tipoRol) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("Ya existe un usuario con el email: " + email);
        }

        Rol rol = rolRepository.findByNombre(tipoRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + tipoRol));

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);   // texto plano — cambiar a BCrypt en producción
        usuario.setRol(rol);
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(TipoRol rol) {
        return usuarioRepository.findByRol_Nombre(rol);
    }

    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}
