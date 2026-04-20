package com.dulzonSA.mantenimiento.dto.response;

import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AvanceMantenimientoResponse {
    private Long cartaGanttId;

    // Datos de la máquina
    private String maquinaNombre;
    private String maquinaTipo;
    private String codigoInternoMaquina;

    // Planificación
    private String turnoNombre;
    private LocalDate fechaProgramada;
    private String operadorNombre;

    // Ejecución real
    private LocalDateTime fechaInicioReal;
    private LocalDateTime fechaFinReal;
    private EstadoMantenimiento estado;

    // Métricas de avance
    private int totalActividades;
    private int actividadesCompletadas;
    private int porcentajeAvance;

    /** Minutos totales de desviación acumulada (solo actividades ya terminadas) */
    private Long desviacionTotalMinutos;

    // Detalle actividad por actividad
    private List<ActividadResponse> actividades;

    // Observaciones registradas al cierre del proceso completo
    private List<ObservacionResponse> observacionesGenerales;
}
