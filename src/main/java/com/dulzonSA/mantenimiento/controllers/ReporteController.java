package com.dulzonSA.mantenimiento.controllers;

import com.dulzonSA.mantenimiento.dto.response.AvanceMantenimientoResponse;
import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import com.dulzonSA.mantenimiento.services.MantenimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private MantenimientoService mantenimientoService;

    /** GET /api/reportes/dashboard — mantenciones activas ahora */
    @GetMapping("/dashboard")
    public ResponseEntity<List<AvanceMantenimientoResponse>> dashboard() {
        return ResponseEntity.ok(mantenimientoService.listarEnProceso());
    }

    /** GET /api/reportes/historial?estado=TERMINADO */
    @GetMapping("/historial")
    public ResponseEntity<List<AvanceMantenimientoResponse>> historial(
            @RequestParam(defaultValue = "TERMINADO") String estado) {
        return ResponseEntity.ok(
                mantenimientoService.listarPorEstado(EstadoMantenimiento.valueOf(estado.toUpperCase())));
    }

    /** GET /api/reportes/{cartaGanttId} — detalle completo */
    @GetMapping("/{cartaGanttId}")
    public ResponseEntity<AvanceMantenimientoResponse> detalle(@PathVariable Long cartaGanttId) {
        return ResponseEntity.ok(mantenimientoService.obtenerAvance(cartaGanttId));
    }
}
