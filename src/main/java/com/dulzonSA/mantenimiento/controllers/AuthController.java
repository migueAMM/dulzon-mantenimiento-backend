package com.dulzonSA.mantenimiento.controllers;

import com.dulzonSA.mantenimiento.models.Usuario;
import com.dulzonSA.mantenimiento.models.enums.TipoRol;
import com.dulzonSA.mantenimiento.services.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    /** POST /api/auth/login  { "email":"...", "password":"..." } */
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request.getEmail(), request.getPassword()));
    }

    /** POST /api/auth/registro  { "nombre":"...", "email":"...", "password":"...", "rol":"SUPERVISOR" } */
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroRequest request) {
        Usuario usuario = usuarioService.crearUsuario(
                request.getNombre(),
                request.getEmail(),
                request.getPassword(),
                TipoRol.valueOf(request.getRol().toUpperCase())
        );
        return ResponseEntity.ok(usuario);
    }

    @Data
    public static class LoginRequest {
        @Email(message = "Email inválido")
        @NotBlank(message = "Email es requerido")
        private String email;

        @NotBlank(message = "Contraseña es requerida")
        @Size(min = 6, message = "Contraseña debe tener mínimo 6 caracteres")
        private String password;
    }

    @Data
    public static class RegistroRequest {
        @NotBlank(message = "Nombre es requerido")
        @Size(min = 3, max = 100, message = "Nombre debe tener entre 3 y 100 caracteres")
        private String nombre;

        @Email(message = "Email inválido")
        @NotBlank(message = "Email es requerido")
        private String email;

        @NotBlank(message = "Contraseña es requerida")
        @Size(min = 6, message = "Contraseña debe tener mínimo 6 caracteres")
        private String password;

        @NotBlank(message = "Rol es requerido")
        private String rol;
    }
}
