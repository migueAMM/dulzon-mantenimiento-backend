package com.dulzonSA.mantenimiento.controllers;

import com.dulzonSA.mantenimiento.dto.request.ObservacionRequest;
import com.dulzonSA.mantenimiento.dto.request.ProgramarMantenimientoRequest;
import com.dulzonSA.mantenimiento.dto.response.AvanceMantenimientoResponse;
import com.dulzonSA.mantenimiento.models.ActividadMantenimiento;
import com.dulzonSA.mantenimiento.models.CartaGantt;
import com.dulzonSA.mantenimiento.models.Observacion;
import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import com.dulzonSA.mantenimiento.services.MantenimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mantenimiento")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    // ── OPERADOR ──────────────────────────────────────────────────

    /**
     * Carga la carta gantt en el sistema con las actividades
     * que entregó la empresa de mantenimiento.
     * POST /api/mantenimiento/programar?operadorId=1
     */
    @PostMapping("/programar")
    public ResponseEntity<CartaGantt> programar(
            @RequestParam Long operadorId,
            @Valid @RequestBody ProgramarMantenimientoRequest request) {
        return ResponseEntity.ok(mantenimientoService.programarMantenimiento(operadorId, request));
    }

    // ── SUPERVISOR ────────────────────────────────────────────────

    /**
     * La cuadrilla de mantenimiento llegó → registra inicio exacto.
     * PUT /api/mantenimiento/{id}/iniciar
     */
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<CartaGantt> iniciarMantenimiento(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.iniciarMantenimiento(id));
    }

    /**
     * Supervisor pulsa "Iniciar actividad" en el móvil.
     * PUT /api/mantenimiento/actividad/{actividadId}/iniciar
     */
    @PutMapping("/actividad/{actividadId}/iniciar")
    public ResponseEntity<ActividadMantenimiento> iniciarActividad(@PathVariable Long actividadId) {
        return ResponseEntity.ok(mantenimientoService.iniciarActividad(actividadId));
    }

    /**
     * Supervisor pulsa "Cerrar actividad" en el móvil.
     * PUT /api/mantenimiento/actividad/{actividadId}/cerrar
     */
    @PutMapping("/actividad/{actividadId}/cerrar")
    public ResponseEntity<ActividadMantenimiento> cerrarActividad(@PathVariable Long actividadId) {
        return ResponseEntity.ok(mantenimientoService.cerrarActividad(actividadId));
    }

    /**
     * Supervisor registra una observación de texto sobre una actividad.
     * POST /api/mantenimiento/actividad/{actividadId}/observacion?supervisorId=2
     */
    @PostMapping("/actividad/{actividadId}/observacion")
    public ResponseEntity<Observacion> registrarObservacion(
            @PathVariable Long actividadId,
            @RequestParam Long supervisorId,
            @Valid @RequestBody ObservacionRequest request) {
        return ResponseEntity.ok(
                mantenimientoService.registrarObservacionEnActividad(actividadId, supervisorId, request));
    }

    /**
     * Supervisor cierra el proceso completo con observaciones finales.
     * PUT /api/mantenimiento/{id}/terminar?supervisorId=2
     * Body (opcional): lista de observaciones de cierre
     */
    @PutMapping("/{id}/terminar")
    public ResponseEntity<CartaGantt> terminarMantenimiento(
            @PathVariable Long id,
            @RequestParam Long supervisorId,
            @Valid @RequestBody(required = false) List<ObservacionRequest> observaciones) {
        return ResponseEntity.ok(
                mantenimientoService.terminarMantenimiento(id, supervisorId, observaciones));
    }

    // ── TODOS ─────────────────────────────────────────────────────

    /**
     * Vista planificado vs real de una mantención.
     * GET /api/mantenimiento/{id}/avance
     */
    @GetMapping("/{id}/avance")
    public ResponseEntity<AvanceMantenimientoResponse> avance(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.obtenerAvance(id));
    }

    /**
     * Mantenciones que están ahora mismo en ejecución.
     * GET /api/mantenimiento/en-proceso
     */
    @GetMapping("/en-proceso")
    public ResponseEntity<List<AvanceMantenimientoResponse>> enProceso() {
        return ResponseEntity.ok(mantenimientoService.listarEnProceso());
    }

    /**
     * Listar por estado. Default: PROGRAMADO.
     * GET /api/mantenimiento?estado=TERMINADO
     */
    @GetMapping
    public ResponseEntity<List<AvanceMantenimientoResponse>> listar(
            @RequestParam(defaultValue = "PROGRAMADO") String estado) {
        return ResponseEntity.ok(
                mantenimientoService.listarPorEstado(EstadoMantenimiento.valueOf(estado.toUpperCase())));
    }

    /**
     * Mantenciones programadas para el día de hoy.
     * GET /api/mantenimiento/hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<AvanceMantenimientoResponse>> hoy() {
        return ResponseEntity.ok(mantenimientoService.listarProgramadasHoy());
    }
}
