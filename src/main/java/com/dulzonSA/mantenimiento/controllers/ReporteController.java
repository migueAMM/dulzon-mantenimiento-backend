package com.dulzonSA.mantenimiento.controllers;

import com.dulzonSA.mantenimiento.dto.response.AvanceMantenimientoResponse;
import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import com.dulzonSA.mantenimiento.services.MantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints orientados a la gerencia.
 * Todas las rutas bajo /api/reportes
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final MantenimientoService mantenimientoService;

    /**
     * Dashboard: muestra todas las mantenciones activas en este momento.
     * La gerencia puede acceder en cualquier momento desde cualquier dispositivo.
     * GET /api/reportes/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<List<AvanceMantenimientoResponse>> dashboard() {
        return ResponseEntity.ok(mantenimientoService.listarEnProceso());
    }

    /**
     * Historial de mantenciones finalizadas.
     * GET /api/reportes/historial?estado=TERMINADO
     */
    @GetMapping("/historial")
    public ResponseEntity<List<AvanceMantenimientoResponse>> historial(
            @RequestParam(defaultValue = "TERMINADO") String estado) {
        return ResponseEntity.ok(
                mantenimientoService.listarPorEstado(EstadoMantenimiento.valueOf(estado.toUpperCase())));
    }

    /**
     * Detalle completo de una mantención: planificado vs real + observaciones.
     * GET /api/reportes/{cartaGanttId}
     */
    @GetMapping("/{cartaGanttId}")
    public ResponseEntity<AvanceMantenimientoResponse> detalle(@PathVariable Long cartaGanttId) {
        return ResponseEntity.ok(mantenimientoService.obtenerAvance(cartaGanttId));
    }
}
