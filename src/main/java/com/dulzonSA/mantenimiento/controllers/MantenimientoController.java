package com.dulzonSA.mantenimiento.controllers;

import com.dulzonSA.mantenimiento.dto.request.ObservacionRequest;
import com.dulzonSA.mantenimiento.dto.request.ProgramarMantenimientoRequest;
import com.dulzonSA.mantenimiento.dto.response.AccionMantenimientoResponse;
import com.dulzonSA.mantenimiento.dto.response.AvanceMantenimientoResponse;
import com.dulzonSA.mantenimiento.dto.response.ObservacionResponse;
import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import com.dulzonSA.mantenimiento.services.MantenimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones de mantenimiento.
 *
 * CORRECCIÓN APLICADA:
 * Todos los endpoints ahora retornan DTOs (no entidades JPA) para evitar el error
 * "Type definition error: ByteBuddyInterceptor" causado por proxies lazy de Hibernate.
 *
 * Endpoints de acción (iniciar/cerrar) → AccionMantenimientoResponse
 * Endpoints de consulta y terminación   → AvanceMantenimientoResponse
 */
@RestController
@RequestMapping("/api/mantenimiento")
public class MantenimientoController {

    @Autowired
    private MantenimientoService mantenimientoService;

    // ── OPERADOR ──────────────────────────────────────────────────────────────

    /**
     * POST /api/mantenimiento/programar?operadorId=1
     * Retorna AvanceMantenimientoResponse para que el front tenga el estado inicial completo.
     */
    @PostMapping("/programar")
    public ResponseEntity<AvanceMantenimientoResponse> programar(
            @RequestParam Long operadorId,
            @Valid @RequestBody ProgramarMantenimientoRequest request) {
        return ResponseEntity.ok(mantenimientoService.programarMantenimiento(operadorId, request));
    }

    // ── SUPERVISOR ────────────────────────────────────────────────────────────

    /**
     * PUT /api/mantenimiento/{id}/iniciar
     * Retorna AccionMantenimientoResponse (DTO plano, sin proxies Hibernate).
     */
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<AccionMantenimientoResponse> iniciarMantenimiento(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.iniciarMantenimiento(id));
    }

    /**
     * PUT /api/mantenimiento/actividad/{actividadId}/iniciar
     * Retorna AccionMantenimientoResponse con estado actualizado de la actividad.
     */
    @PutMapping("/actividad/{actividadId}/iniciar")
    public ResponseEntity<AccionMantenimientoResponse> iniciarActividad(@PathVariable Long actividadId) {
        return ResponseEntity.ok(mantenimientoService.iniciarActividad(actividadId));
    }

    /**
     * PUT /api/mantenimiento/actividad/{actividadId}/cerrar
     * Retorna AccionMantenimientoResponse con estado COMPLETADA y desviación calculada.
     */
    @PutMapping("/actividad/{actividadId}/cerrar")
    public ResponseEntity<AccionMantenimientoResponse> cerrarActividad(@PathVariable Long actividadId) {
        return ResponseEntity.ok(mantenimientoService.cerrarActividad(actividadId));
    }

    /**
     * POST /api/mantenimiento/actividad/{actividadId}/observacion?supervisorId=2
     * Retorna ObservacionResponse (DTO plano).
     */
    @PostMapping("/actividad/{actividadId}/observacion")
    public ResponseEntity<ObservacionResponse> registrarObservacion(
            @PathVariable Long actividadId,
            @RequestParam Long supervisorId,
            @Valid @RequestBody ObservacionRequest request) {
        return ResponseEntity.ok(
                mantenimientoService.registrarObservacionEnActividad(actividadId, supervisorId, request));
    }

    /**
     * PUT /api/mantenimiento/{id}/terminar?supervisorId=2
     * Retorna AvanceMantenimientoResponse completo para que el front actualice toda la vista
     * y oculte correctamente las opciones de "terminar actividad".
     */
    @PutMapping("/{id}/terminar")
    public ResponseEntity<AvanceMantenimientoResponse> terminarMantenimiento(
            @PathVariable Long id,
            @RequestParam Long supervisorId,
            @RequestBody(required = false) List<ObservacionRequest> observaciones) {
        return ResponseEntity.ok(
                mantenimientoService.terminarMantenimiento(id, supervisorId, observaciones));
    }

    // ── TODOS ─────────────────────────────────────────────────────────────────

    /** GET /api/mantenimiento/{id}/avance */
    @GetMapping("/{id}/avance")
    public ResponseEntity<AvanceMantenimientoResponse> avance(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.obtenerAvance(id));
    }

    /** GET /api/mantenimiento/en-proceso */
    @GetMapping("/en-proceso")
    public ResponseEntity<List<AvanceMantenimientoResponse>> enProceso() {
        return ResponseEntity.ok(mantenimientoService.listarEnProceso());
    }

    /** GET /api/mantenimiento?estado=PROGRAMADO */
    @GetMapping
    public ResponseEntity<List<AvanceMantenimientoResponse>> listar(
            @RequestParam(defaultValue = "PROGRAMADO") String estado) {
        return ResponseEntity.ok(
                mantenimientoService.listarPorEstado(
                        EstadoMantenimiento.valueOf(estado.toUpperCase())));
    }

    /** GET /api/mantenimiento/hoy */
    @GetMapping("/hoy")
    public ResponseEntity<List<AvanceMantenimientoResponse>> hoy() {
        return ResponseEntity.ok(mantenimientoService.listarProgramadasHoy());
    }
}
